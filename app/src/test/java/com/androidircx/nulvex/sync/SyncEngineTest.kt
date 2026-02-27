package com.androidircx.nulvex.sync

import com.androidircx.nulvex.data.SyncOutboxEntity
import com.androidircx.nulvex.data.SyncStateStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncEngineTest {
    private val api = mockk<SyncApi>()
    private val stateStore = mockk<SyncStateStore>(relaxed = true)
    private val token = SyncAuthToken(
        accessToken = "a",
        refreshToken = "r",
        expiresAtEpochMillis = 999999L,
        deviceId = "dev-1"
    )

    @Test
    fun runCycle_acknowledgesAcceptedPush_andAppliesPulledOps() = runTest {
        val outbox = listOf(
            SyncOutboxEntity(
                opId = "op-1",
                deviceId = "dev-1",
                profile = "real",
                entityType = "note",
                entityId = "n-1",
                opType = "upsert",
                baseRevision = "r1",
                envelopeCiphertext = byteArrayOf(1),
                clientTs = 10L,
                createdAt = 10L
            )
        )
        coEvery { stateStore.pollReadyOutbox(any(), any()) } returns outbox
        coEvery { stateStore.getCursor("real") } returns "cur-1"
        coEvery { api.push("real", token, any()) } returns listOf(SyncPushAck("op-1", true))
        coEvery { api.pull("real", token, "cur-1", any()) } returns SyncPullResult(
            cursorToken = "cur-2",
            operations = listOf(
                SyncPulledOp(
                    opId = "rop-1",
                    entityId = "n-2",
                    revision = "r2",
                    baseRevision = null,
                    ciphertext = byteArrayOf(3)
                )
            )
        )

        var applied = 0
        val engine = SyncEngine(
            api = api,
            stateStore = stateStore,
            localRevisionLookup = { null },
            applyRemoteOp = {
                applied++
                true
            }
        )

        val report = engine.runCycle(profile = "real", token = token)

        assertEquals(1, report.pushedAccepted)
        assertEquals(0, report.pushedRejected)
        assertEquals(1, report.pulledApplied)
        assertEquals(0, report.pulledConflicts)
        assertEquals(1, applied)
        coVerify { stateStore.ackOutbox("op-1") }
        coVerify { stateStore.updateCursor("real", "cur-2", any()) }
    }

    @Test
    fun runCycle_recordsConflict_whenBaseRevisionMismatches() = runTest {
        coEvery { stateStore.pollReadyOutbox(any(), any()) } returns emptyList()
        coEvery { stateStore.getCursor("real") } returns null
        coEvery { api.pull("real", token, null, any()) } returns SyncPullResult(
            cursorToken = "cur-10",
            operations = listOf(
                SyncPulledOp(
                    opId = "rop-2",
                    entityId = "n-9",
                    revision = "r9",
                    baseRevision = "r8",
                    ciphertext = byteArrayOf(9)
                )
            )
        )

        val engine = SyncEngine(
            api = api,
            stateStore = stateStore,
            localRevisionLookup = { "local-r0" },
            applyRemoteOp = { throw IllegalStateException("Should not apply on conflict") }
        )

        val report = engine.runCycle(profile = "real", token = token)

        assertEquals(1, report.pulledConflicts)
        assertEquals(0, report.pulledApplied)
        assertTrue(report.pushedAccepted == 0 && report.pushedRejected == 0)
        coVerify(exactly = 1) {
            stateStore.recordConflict(
                profile = "real",
                entityId = "n-9",
                localRevision = "local-r0",
                remoteRevision = "r9",
                remoteOpId = "rop-2",
                resolutionPolicy = "lww",
                now = any()
            )
        }
    }

    @Test
    fun runCycle_schedulesRetry_whenPushThrows() = runTest {
        val outbox = listOf(
            SyncOutboxEntity(
                opId = "op-retry",
                deviceId = "dev-1",
                profile = "real",
                entityType = "note",
                entityId = "n-r",
                opType = "upsert",
                baseRevision = null,
                envelopeCiphertext = byteArrayOf(1, 2),
                clientTs = 22L,
                createdAt = 22L,
                attemptCount = 2
            )
        )
        coEvery { stateStore.pollReadyOutbox(any(), any()) } returns outbox
        coEvery { api.push("real", token, any()) } throws IllegalStateException("offline")
        coEvery { stateStore.getCursor("real") } returns null
        coEvery { api.pull("real", token, null, any()) } returns SyncPullResult(
            cursorToken = null,
            operations = emptyList()
        )

        val engine = SyncEngine(
            api = api,
            stateStore = stateStore,
            localRevisionLookup = { null },
            applyRemoteOp = { true }
        )

        val report = engine.runCycle(profile = "real", token = token)

        assertEquals(0, report.pushedAccepted)
        assertEquals(1, report.pushedRejected)
        coVerify(exactly = 1) { stateStore.scheduleRetry("op-retry", 2, any()) }
    }

    @Test
    fun runCycle_skipsCursorUpdate_whenApplyRequestsHalt() = runTest {
        coEvery { stateStore.pollReadyOutbox(any(), any()) } returns emptyList()
        coEvery { stateStore.getCursor("real") } returns "cur-4"
        coEvery { api.pull("real", token, "cur-4", any()) } returns SyncPullResult(
            cursorToken = "cur-5",
            operations = listOf(
                SyncPulledOp(
                    opId = "panic-op",
                    entityId = "panic",
                    revision = null,
                    baseRevision = null,
                    ciphertext = """{"op_type":"panic_wipe"}""".toByteArray()
                )
            )
        )

        val engine = SyncEngine(
            api = api,
            stateStore = stateStore,
            localRevisionLookup = { null },
            applyRemoteOp = { false }
        )

        val report = engine.runCycle(profile = "real", token = token)

        assertEquals(1, report.pulledApplied)
        coVerify(exactly = 0) { stateStore.updateCursor("real", any(), any()) }
    }
}
