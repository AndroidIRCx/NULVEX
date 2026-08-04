package com.androidircx.nulvex.sync

import com.androidircx.nulvex.data.NoteDao
import com.androidircx.nulvex.data.NoteEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

class SyncRemoteOpApplierTest {
    @Test
    fun apply_upsertPayload_writesEntity() = runTest {
        val dao = mockk<NoteDao>(relaxed = true)
        val captured = mutableListOf<NoteEntity>()
        coEvery { dao.upsert(capture(captured)) } returns 1L
        val applier = SyncRemoteOpApplier(noteDao = dao, onRemotePanicWipe = {})
        val noteCipher = byteArrayOf(1, 2, 3, 4)
        val payload = """
            {
              "op_type":"upsert",
              "entity_id":"n-1",
              "note":{
                "ciphertext_b64":"${Base64.getEncoder().encodeToString(noteCipher)}",
                "created_at":100,
                "updated_at":101,
                "read_once":true
              }
            }
        """.trimIndent().toByteArray()

        val keepRunning = applier.apply(
            SyncPulledOp(
                opId = "op-1",
                entityId = "n-1",
                revision = "r1",
                baseRevision = null,
                ciphertext = payload
            )
        )

        assertTrue(keepRunning)
        assertTrue(captured.isNotEmpty())
        assertArrayEquals(noteCipher, captured.single().ciphertext)
        assertTrue(captured.single().readOnce)
    }

    @Test
    fun apply_deletePayload_zeroizesThenSoftDeletes() = runTest {
        val dao = mockk<NoteDao>(relaxed = true)
        coEvery { dao.getById("n-del") } returns NoteEntity(
            id = "n-del",
            ciphertext = byteArrayOf(9, 9, 9),
            createdAt = 1L,
            expiresAt = null,
            readOnce = false,
            deleted = false
        )
        val applier = SyncRemoteOpApplier(noteDao = dao, onRemotePanicWipe = {})
        val payload = """{"op_type":"delete","entity_id":"n-del"}""".toByteArray()

        val keepRunning = applier.apply(
            SyncPulledOp(
                opId = "op-del",
                entityId = "n-del",
                revision = null,
                baseRevision = null,
                ciphertext = payload
            )
        )

        assertTrue(keepRunning)
        coVerify(exactly = 1) { dao.overwriteCiphertext("n-del", any()) }
        coVerify(exactly = 1) { dao.softDelete("n-del") }
    }

    @Test
    fun apply_upsert_isSkipped_whenLocalCopyIsNewer() = runTest {
        val dao = mockk<NoteDao>(relaxed = true)
        coEvery { dao.getById("n-1") } returns NoteEntity(
            id = "n-1",
            ciphertext = byteArrayOf(7, 7),
            createdAt = 1L,
            expiresAt = null,
            readOnce = false,
            deleted = false,
            updatedAt = 500L
        )
        val applier = SyncRemoteOpApplier(noteDao = dao, onRemotePanicWipe = {})
        val noteCipher = byteArrayOf(1, 2)
        val payload = """
            {"op_type":"upsert","entity_id":"n-1",
             "note":{"ciphertext_b64":"${Base64.getEncoder().encodeToString(noteCipher)}",
                     "created_at":100,"updated_at":200}}
        """.trimIndent().toByteArray()

        val keepRunning = applier.apply(
            SyncPulledOp("op-1", "n-1", "r1", null, payload)
        )

        assertTrue(keepRunning)
        // Remote updated_at (200) < local updatedAt (500) → local wins, no overwrite.
        coVerify(exactly = 0) { dao.upsert(any()) }
    }

    @Test
    fun apply_upsert_isApplied_whenRemoteIsNewer() = runTest {
        val dao = mockk<NoteDao>(relaxed = true)
        coEvery { dao.getById("n-1") } returns NoteEntity(
            id = "n-1",
            ciphertext = byteArrayOf(7, 7),
            createdAt = 1L,
            expiresAt = null,
            readOnce = false,
            deleted = false,
            updatedAt = 100L
        )
        val applier = SyncRemoteOpApplier(noteDao = dao, onRemotePanicWipe = {})
        val noteCipher = byteArrayOf(1, 2)
        val payload = """
            {"op_type":"upsert","entity_id":"n-1",
             "note":{"ciphertext_b64":"${Base64.getEncoder().encodeToString(noteCipher)}",
                     "created_at":100,"updated_at":900}}
        """.trimIndent().toByteArray()

        applier.apply(SyncPulledOp("op-1", "n-1", "r1", null, payload))

        coVerify(exactly = 1) { dao.upsert(any()) }
    }

    @Test
    fun apply_delete_isSkipped_whenLocalEditIsNewerThanDelete() = runTest {
        val dao = mockk<NoteDao>(relaxed = true)
        coEvery { dao.getById("n-del") } returns NoteEntity(
            id = "n-del",
            ciphertext = byteArrayOf(9, 9, 9),
            createdAt = 1L,
            expiresAt = null,
            readOnce = false,
            deleted = false,
            updatedAt = 9_000L
        )
        val applier = SyncRemoteOpApplier(noteDao = dao, onRemotePanicWipe = {})
        // Remote delete stamped older than the local edit → local wins, no delete.
        val payload = """{"op_type":"delete","entity_id":"n-del","ts":100}""".toByteArray()

        applier.apply(SyncPulledOp("op-del", "n-del", null, null, payload))

        coVerify(exactly = 0) { dao.softDelete(any()) }
    }

    @Test
    fun apply_remotePanicPayload_invokesCallbackAndStopsCycle() = runTest {
        val dao = mockk<NoteDao>(relaxed = true)
        var panicCalled = false
        val applier = SyncRemoteOpApplier(
            noteDao = dao,
            onRemotePanicWipe = { panicCalled = true }
        )

        val keepRunning = applier.apply(
            SyncPulledOp(
                opId = "op-panic",
                entityId = "n-any",
                revision = null,
                baseRevision = null,
                ciphertext = """{"op_type":"panic_wipe"}""".toByteArray()
            )
        )

        assertFalse(keepRunning)
        assertTrue(panicCalled)
    }
}
