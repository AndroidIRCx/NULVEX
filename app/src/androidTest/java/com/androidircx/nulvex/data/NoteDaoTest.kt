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
        readOnce: Boolean = false
    ) = NoteEntity(
        id = id,
        ciphertext = ciphertext,
        createdAt = System.currentTimeMillis(),
        expiresAt = expiresAt,
        readOnce = readOnce,
        deleted = deleted
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
        val active = dao.listActive()
        assertEquals(2, active.size)
        assertTrue(active.none { it.deleted })
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
}
