package com.androidircx.nulvex.sync

import com.androidircx.nulvex.data.SyncStateStore

data class SyncCycleReport(
    val pushedAccepted: Int,
    val pushedRejected: Int,
    val pulledApplied: Int,
    val pulledConflicts: Int
)

class SyncEngine(
    private val api: SyncApi,
    private val stateStore: SyncStateStore,
    private val localRevisionLookup: suspend (entityId: String) -> String?,
    private val applyRemoteOp: suspend (SyncPulledOp) -> Boolean
) {
    suspend fun runCycle(
        profile: String,
        token: SyncAuthToken,
        batchLimit: Int = 50
    ): SyncCycleReport {
        var pushedAccepted = 0
        var pushedRejected = 0
        var pulledApplied = 0
        var pulledConflicts = 0

        val outbox = stateStore.pollReadyOutbox(limit = batchLimit)
            .filter { it.profile == profile }
        if (outbox.isNotEmpty()) {
            val envelopes = outbox.map {
                SyncEnvelope(
                    opId = it.opId,
                    profile = it.profile,
                    entityType = it.entityType,
                    entityId = it.entityId,
                    opType = it.opType,
                    baseRevision = it.baseRevision,
                    clientTs = it.clientTs,
                    ciphertext = it.envelopeCiphertext
                )
            }

            try {
                val acks = api.push(profile = profile, token = token, operations = envelopes)
                    .associateBy { it.opId }
                outbox.forEach { op ->
                    val ack = acks[op.opId]
                    when {
                        ack?.accepted == true -> {
                            stateStore.ackOutbox(op.opId)
                            pushedAccepted++
                        }
                        else -> {
                            stateStore.scheduleRetry(op.opId, op.attemptCount)
                            pushedRejected++
                        }
                    }
                }
            } catch (_: Exception) {
                outbox.forEach { op ->
                    stateStore.scheduleRetry(op.opId, op.attemptCount)
                    pushedRejected++
                }
            }
        }

        val cursor = stateStore.getCursor(profile)
        val pull = api.pull(profile = profile, token = token, cursorToken = cursor, limit = batchLimit)
        var halted = false
        pull.operations.forEach { op ->
            if (halted) return@forEach
            val localRev = localRevisionLookup(op.entityId)
            val conflict = localRev != null && op.baseRevision != null && localRev != op.baseRevision
            if (conflict) {
                stateStore.recordConflict(
                    profile = profile,
                    entityId = op.entityId,
                    localRevision = localRev,
                    remoteRevision = op.revision,
                    remoteOpId = op.opId,
                    resolutionPolicy = "lww"
                )
                pulledConflicts++
            } else {
                val shouldContinue = applyRemoteOp(op)
                pulledApplied++
                if (!shouldContinue) halted = true
            }
        }
        if (!halted) {
            pull.cursorToken?.let { stateStore.updateCursor(profile, it) }
        }

        return SyncCycleReport(
            pushedAccepted = pushedAccepted,
            pushedRejected = pushedRejected,
            pulledApplied = pulledApplied,
            pulledConflicts = pulledConflicts
        )
    }
}
