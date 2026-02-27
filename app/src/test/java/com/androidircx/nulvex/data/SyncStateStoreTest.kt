package com.androidircx.nulvex.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SyncStateStoreTest {
    private val dao = mockk<SyncStateDao>(relaxed = true)
    private val store = SyncStateStore(dao)

    @Test
    fun enqueueOutbox_persistsOperation() = runTest {
        val opId = store.enqueueOutbox(
            deviceId = "dev-1",
            profile = "real",
            entityType = "note",
            entityId = "note-1",
            opType = "upsert",
            baseRevision = "rev-1",
            envelopeCiphertext = byteArrayOf(1, 2, 3),
            clientTs = 1000L
        )

        assertTrue(opId.isNotBlank())
        coVerify(exactly = 1) { dao.upsertOutbox(any()) }
    }

    @Test
    fun scheduleRetry_updatesAttemptAndNextAttempt() = runTest {
        coEvery { dao.updateOutboxRetry(any(), any(), any()) } returns 1

        val ok = store.scheduleRetry(opId = "op-1", previousAttempts = 1, now = 1000L)

        assertTrue(ok)
        coVerify(exactly = 1) {
            dao.updateOutboxRetry(
                opId = "op-1",
                attemptCount = 2,
                nextAttemptAt = 3000L
            )
        }
    }

    @Test
    fun cursor_roundTrip() = runTest {
        coEvery { dao.getCursor("real") } returns SyncCursorEntity(
            profile = "real",
            cursorToken = "cur-123",
            updatedAt = 111L
        )

        store.updateCursor(profile = "real", cursorToken = "cur-123", now = 111L)
        val cursor = store.getCursor("real")

        assertEquals("cur-123", cursor)
        coVerify(exactly = 1) { dao.upsertCursor(any()) }
    }

    @Test
    fun recordConflict_andListOpen() = runTest {
        val expected = listOf(
            SyncConflictEntity(
                id = "c-1",
                profile = "real",
                entityId = "n-1",
                localRevision = "r1",
                remoteRevision = "r2",
                remoteOpId = "op-7",
                resolutionPolicy = "lww",
                createdAt = 10L
            )
        )
        coEvery { dao.listOpenConflicts("real") } returns expected

        val id = store.recordConflict(
            profile = "real",
            entityId = "n-1",
            localRevision = "r1",
            remoteRevision = "r2",
            remoteOpId = "op-7",
            resolutionPolicy = "lww",
            now = 10L
        )
        val list = store.listOpenConflicts("real")

        assertTrue(id.isNotBlank())
        assertEquals(1, list.size)
        assertEquals("n-1", list.first().entityId)
        coVerify(exactly = 1) { dao.upsertConflict(any()) }
    }
}
