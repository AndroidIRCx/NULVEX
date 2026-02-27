package com.androidircx.nulvex.data

import java.util.UUID
import kotlin.math.min

class SyncStateStore(
    private val dao: SyncStateDao
) {
    suspend fun enqueueOutbox(
        deviceId: String,
        profile: String,
        entityType: String,
        entityId: String,
        opType: String,
        baseRevision: String?,
        envelopeCiphertext: ByteArray,
        clientTs: Long = System.currentTimeMillis()
    ): String {
        val opId = UUID.randomUUID().toString()
        dao.upsertOutbox(
            SyncOutboxEntity(
                opId = opId,
                deviceId = deviceId,
                profile = profile,
                entityType = entityType,
                entityId = entityId,
                opType = opType,
                baseRevision = baseRevision,
                envelopeCiphertext = envelopeCiphertext,
                clientTs = clientTs,
                createdAt = clientTs,
                attemptCount = 0,
                nextAttemptAt = 0L
            )
        )
        return opId
    }

    suspend fun pollReadyOutbox(limit: Int = 50, now: Long = System.currentTimeMillis()): List<SyncOutboxEntity> {
        return dao.listReadyOutbox(now = now, limit = limit)
    }

    suspend fun ackOutbox(opId: String): Boolean {
        return dao.deleteOutbox(opId) > 0
    }

    suspend fun scheduleRetry(opId: String, previousAttempts: Int, now: Long = System.currentTimeMillis()): Boolean {
        val attemptCount = previousAttempts + 1
        val delayMs = min(60_000L, 1_000L shl min(10, previousAttempts))
        return dao.updateOutboxRetry(
            opId = opId,
            attemptCount = attemptCount,
            nextAttemptAt = now + delayMs
        ) > 0
    }

    suspend fun updateCursor(profile: String, cursorToken: String, now: Long = System.currentTimeMillis()) {
        dao.upsertCursor(SyncCursorEntity(profile = profile, cursorToken = cursorToken, updatedAt = now))
    }

    suspend fun getCursor(profile: String): String? {
        return dao.getCursor(profile)?.cursorToken
    }

    suspend fun recordConflict(
        profile: String,
        entityId: String,
        localRevision: String?,
        remoteRevision: String?,
        remoteOpId: String?,
        resolutionPolicy: String,
        now: Long = System.currentTimeMillis()
    ): String {
        val id = UUID.randomUUID().toString()
        dao.upsertConflict(
            SyncConflictEntity(
                id = id,
                profile = profile,
                entityId = entityId,
                localRevision = localRevision,
                remoteRevision = remoteRevision,
                remoteOpId = remoteOpId,
                resolutionPolicy = resolutionPolicy,
                createdAt = now
            )
        )
        return id
    }

    suspend fun listOpenConflicts(profile: String): List<SyncConflictEntity> {
        return dao.listOpenConflicts(profile)
    }

    suspend fun markConflictResolved(id: String, now: Long = System.currentTimeMillis()): Boolean {
        return dao.markConflictResolved(id = id, resolvedAt = now) > 0
    }
}
