package com.androidircx.nulvex.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NoteDaoTest {

    private lateinit var db: NulvexDatabase
    private lateinit var dao: NoteDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            NulvexDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.noteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    private fun note(
        id: String,
        ciphertext: ByteArray = ByteArray(16),
        expiresAt: Long? = null,
        deleted: Boolean = false,
        readOnce: Boolean = false,
        archivedAt: Long? = null,
        reminderAt: Long? = null,
        reminderDone: Boolean = false,
        trashedAt: Long? = null
    ) = NoteEntity(
        id = id,
        ciphertext = ciphertext,
        createdAt = System.currentTimeMillis(),
        expiresAt = expiresAt,
        readOnce = readOnce,
        deleted = deleted,
        archivedAt = archivedAt,
        reminderAt = reminderAt,
        reminderDone = reminderDone,
        trashedAt = trashedAt
    )

    @Test
    fun insertCreatesRecord() = runTest {
        dao.upsert(note("n1"))
        val retrieved = dao.getById("n1")
        assertNotNull(retrieved)
        assertEquals("n1", retrieved!!.id)
    }

    @Test
    fun getByIdReturnsCorrectNote() = runTest {
        dao.upsert(note("a"))
        dao.upsert(note("b"))
        val result = dao.getById("b")
        assertEquals("b", result!!.id)
    }

    @Test
    fun getByIdReturnsNullForMissingNote() = runTest {
        val result = dao.getById("missing")
        assertNull(result)
    }

    @Test
    fun upsertReplacesExistingRecord() = runTest {
        val original = ByteArray(16) { 0x01 }
        val updated = ByteArray(16) { 0x02 }
        dao.upsert(note("n1", ciphertext = original))
        dao.upsert(note("n1", ciphertext = updated))
        val result = dao.getById("n1")
        assertArrayEquals(updated, result!!.ciphertext)
    }

    @Test
    fun listActiveReturnsOnlyNonDeletedNotes() = runTest {
        dao.upsert(note("active1"))
        dao.upsert(note("active2"))
        dao.upsert(note("deleted1", deleted = true))
        dao.upsert(note("archived", archivedAt = System.currentTimeMillis()))
        dao.upsert(note("trashed", trashedAt = System.currentTimeMillis()))
        val active = dao.listActive()
        assertEquals(2, active.size)
        assertTrue(active.none { it.deleted })
        assertTrue(active.none { it.archivedAt != null })
        assertTrue(active.none { it.trashedAt != null })
    }

    @Test
    fun listArchivedReturnsOnlyArchivedAndNonDeletedNotes() = runTest {
        dao.upsert(note("active"))
        dao.upsert(note("archived1", archivedAt = System.currentTimeMillis()))
        dao.upsert(note("archived2", archivedAt = System.currentTimeMillis()))
        dao.upsert(note("archived-deleted", archivedAt = System.currentTimeMillis(), deleted = true))

        val archived = dao.listArchived()

        assertEquals(2, archived.size)
        assertTrue(archived.all { it.archivedAt != null && !it.deleted })
    }

    @Test
    fun listTrashedReturnsOnlyTrashedAndNonDeletedNotes() = runTest {
        dao.upsert(note("active"))
        dao.upsert(note("trashed1", trashedAt = System.currentTimeMillis()))
        dao.upsert(note("trashed2", trashedAt = System.currentTimeMillis()))
        dao.upsert(note("trashed-deleted", trashedAt = System.currentTimeMillis(), deleted = true))

        val trashed = dao.listTrashed()

        assertEquals(2, trashed.size)
        assertTrue(trashed.all { it.trashedAt != null && !it.deleted })
    }

    @Test
    fun listActiveReturnsEmptyWhenAllDeleted() = runTest {
        dao.upsert(note("d1", deleted = true))
        dao.upsert(note("d2", deleted = true))
        val active = dao.listActive()
        assertTrue(active.isEmpty())
    }

    @Test
    fun listExpiredReturnsNotesWhoseExpiryHasPassed() = runTest {
        val past = System.currentTimeMillis() - 60_000L
        val future = System.currentTimeMillis() + 60_000L
        dao.upsert(note("expired", expiresAt = past))
        dao.upsert(note("future", expiresAt = future))
        dao.upsert(note("no-expiry", expiresAt = null))

        val expired = dao.listExpired(System.currentTimeMillis())

        assertEquals(1, expired.size)
        assertEquals("expired", expired[0].id)
    }

    @Test
    fun listExpiredExcludesAlreadyDeletedNotes() = runTest {
        val past = System.currentTimeMillis() - 1000L
        dao.upsert(note("already-deleted", expiresAt = past, deleted = true))
        val expired = dao.listExpired(System.currentTimeMillis())
        assertTrue(expired.isEmpty())
    }

    @Test
    fun overwriteCiphertextUpdatesBlob() = runTest {
        dao.upsert(note("n1", ciphertext = ByteArray(16) { 0xFF.toByte() }))
        val zeroed = ByteArray(16) { 0x00 }
        dao.overwriteCiphertext("n1", zeroed)
        val result = dao.getById("n1")
        assertArrayEquals(zeroed, result!!.ciphertext)
    }

    @Test
    fun softDeleteSetsDeletedFlag() = runTest {
        dao.upsert(note("n1"))
        dao.softDelete("n1")
        val result = dao.getById("n1")
        assertTrue(result!!.deleted)
    }

    @Test
    fun softDeleteDoesNotRemoveRecord() = runTest {
        dao.upsert(note("n1"))
        dao.softDelete("n1")
        val result = dao.getById("n1")
        assertNotNull(result)
    }

    @Test
    fun purgeDeletedRemovesOnlyDeletedRecords() = runTest {
        dao.upsert(note("keep"))
        dao.upsert(note("remove", deleted = true))
        dao.purgeDeleted()
        assertNotNull(dao.getById("keep"))
        assertNull(dao.getById("remove"))
    }

    @Test
    fun purgeDeletedReturnsCountOfRemovedRows() = runTest {
        dao.upsert(note("d1", deleted = true))
        dao.upsert(note("d2", deleted = true))
        dao.upsert(note("keep"))
        val count = dao.purgeDeleted()
        assertEquals(2, count)
    }

    @Test
    fun listActiveReturnsEmptyInitially() = runTest {
        val notes = dao.listActive()
        assertTrue(notes.isEmpty())
    }

    @Test
    fun readOnceFieldIsPersistedCorrectly() = runTest {
        dao.upsert(note("ro", readOnce = true))
        val result = dao.getById("ro")
        assertTrue(result!!.readOnce)
    }

    @Test
    fun expiresAtNullIsPersistedCorrectly() = runTest {
        dao.upsert(note("no-exp", expiresAt = null))
        val result = dao.getById("no-exp")
        assertNull(result!!.expiresAt)
    }

    @Test
    fun setArchivedAtPersistsArchiveTimestamp() = runTest {
        dao.upsert(note("n1"))
        val ts = System.currentTimeMillis()
        dao.setArchivedAt("n1", ts)
        val result = dao.getById("n1")
        assertEquals(ts, result!!.archivedAt)
    }

    @Test
    fun setArchivedAtNullUnarchivesNote() = runTest {
        dao.upsert(note("n1", archivedAt = System.currentTimeMillis()))
        dao.setArchivedAt("n1", null)
        val result = dao.getById("n1")
        assertNull(result!!.archivedAt)
    }

    @Test
    fun setReminderPersistsReminderFields() = runTest {
        dao.upsert(note("n1"))
        val ts = System.currentTimeMillis() + 60_000L
        dao.setReminder("n1", ts, reminderDone = false, reminderRepeat = null)
        val result = dao.getById("n1")
        assertEquals(ts, result!!.reminderAt)
        assertFalse(result.reminderDone)
    }

    @Test
    fun listDueRemindersReturnsOnlyDueAndUndoneNotes() = runTest {
        val now = System.currentTimeMillis()
        dao.upsert(note("due", reminderAt = now - 1_000L, reminderDone = false))
        dao.upsert(note("future", reminderAt = now + 60_000L, reminderDone = false))
        dao.upsert(note("done", reminderAt = now - 1_000L, reminderDone = true))
        dao.upsert(note("deleted", reminderAt = now - 1_000L, reminderDone = false, deleted = true))

        val due = dao.listDueReminders(now)

        assertEquals(1, due.size)
        assertEquals("due", due[0].id)
    }

    @Test
    fun revisionsInsertAndListByCreatedAtDesc() = runTest {
        dao.insertRevision(
            NoteRevisionEntity(
                id = "r1",
                noteId = "n1",
                ciphertextSnapshot = byteArrayOf(1),
                expiresAt = null,
                readOnce = false,
                archivedAt = null,
                reminderAt = null,
                reminderDone = false,
                createdAt = 100L
            )
        )
        dao.insertRevision(
            NoteRevisionEntity(
                id = "r2",
                noteId = "n1",
                ciphertextSnapshot = byteArrayOf(2),
                expiresAt = null,
                readOnce = false,
                archivedAt = null,
                reminderAt = null,
                reminderDone = false,
                createdAt = 200L
            )
        )

        val revisions = dao.listRevisions("n1", 20)

        assertEquals(2, revisions.size)
        assertEquals("r2", revisions[0].id)
        assertEquals("r1", revisions[1].id)
    }

    @Test
    fun revisionsPruneKeepsLatestN() = runTest {
        (1..3).forEach { i ->
            dao.insertRevision(
                NoteRevisionEntity(
                    id = "r$i",
                    noteId = "n1",
                    ciphertextSnapshot = byteArrayOf(i.toByte()),
                    expiresAt = null,
                    readOnce = false,
                    archivedAt = null,
                    reminderAt = null,
                    reminderDone = false,
                    createdAt = i.toLong()
                )
            )
        }

        dao.pruneRevisions("n1", keep = 2)
        val revisions = dao.listRevisions("n1", 20)

        assertEquals(2, revisions.size)
        assertEquals(listOf("r3", "r2"), revisions.map { it.id })
    }

    @Test
    fun restoreFromRevisionUpdatesNoteFields() = runTest {
        dao.upsert(note("n1"))
        val snapshot = byteArrayOf(9, 8, 7)

        dao.restoreFromRevision(
            id = "n1",
            ciphertext = snapshot,
            expiresAt = 123L,
            readOnce = true,
            archivedAt = 456L,
            reminderAt = 789L,
            reminderDone = true
        )

        val result = dao.getById("n1")
        assertNotNull(result)
        assertArrayEquals(snapshot, result!!.ciphertext)
        assertEquals(123L, result.expiresAt)
        assertTrue(result.readOnce)
        assertEquals(456L, result.archivedAt)
        assertEquals(789L, result.reminderAt)
        assertTrue(result.reminderDone)
    }

    @Test
    fun sqlInjectionLikeIdIsTreatedAsLiteralValue() = runTest {
        val injectionLikeId = "' OR 1=1 --"
        dao.upsert(note("safe-1"))
        dao.upsert(note(injectionLikeId))

        val exact = dao.getById(injectionLikeId)
        val other = dao.getById("safe-1")

        assertNotNull(exact)
        assertEquals(injectionLikeId, exact!!.id)
        assertNotNull(other)
        assertEquals("safe-1", other!!.id)
    }

    @Test
    fun sqlInjectionLikeIdDoesNotAffectOtherRowsOnUpdateQueries() = runTest {
        val injectionLikeId = "' OR 1=1 --"
        dao.upsert(note("safe-1", reminderAt = null, reminderDone = false))
        dao.upsert(note(injectionLikeId, reminderAt = null, reminderDone = false))

        val targetTs = System.currentTimeMillis() + 60_000L
        dao.setReminder(injectionLikeId, targetTs, reminderDone = false, reminderRepeat = null)

        val injected = dao.getById(injectionLikeId)
        val safe = dao.getById("safe-1")
        assertEquals(targetTs, injected!!.reminderAt)
        assertNull(safe!!.reminderAt)
    }
}
