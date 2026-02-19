package com.androidircx.nulvex.data

import com.androidircx.nulvex.crypto.NoteCrypto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NoteRepositoryTest {

    private lateinit var mockDao: NoteDao
    private lateinit var mockCrypto: NoteCrypto
    private lateinit var repo: NoteRepository

    private val noteKey = ByteArray(32) { it.toByte() }
    private val fakeCiphertext = ByteArray(64) { (it + 100).toByte() }
    private val fakePlaintext = NotePayloadCodec.encode(
        NotePayload(
            text = "hello",
            checklist = emptyList(),
            labels = emptyList(),
            attachments = emptyList(),
            pinned = false
        )
    ).toByteArray(Charsets.UTF_8)

    @Before
    fun setUp() {
        mockDao = mockk()
        mockCrypto = mockk()
        repo = NoteRepository(mockDao, mockCrypto)

        every { mockCrypto.encrypt(any(), any()) } returns fakeCiphertext
        every { mockCrypto.decrypt(any(), any()) } returns fakePlaintext
    }

    @Test
    fun `saveNote encrypts and persists`() = runTest {
        coEvery { mockDao.upsert(any()) } returns 1L

        val id = repo.saveNote(text = "hello", noteKey = noteKey)

        assertNotNull(id)
        val entitySlot = slot<NoteEntity>()
        coVerify { mockDao.upsert(capture(entitySlot)) }
        assertArrayEquals(fakeCiphertext, entitySlot.captured.ciphertext)
        assertFalse(entitySlot.captured.deleted)
    }

    @Test
    fun `saveNote returns provided id`() = runTest {
        coEvery { mockDao.upsert(any()) } returns 1L

        val id = repo.saveNote(id = "my-id", text = "test", noteKey = noteKey)

        assertEquals("my-id", id)
    }

    @Test
    fun `saveNote stores expiresAt and readOnce`() = runTest {
        coEvery { mockDao.upsert(any()) } returns 1L
        val expiry = System.currentTimeMillis() + 3600_000L

        repo.saveNote(text = "burn", noteKey = noteKey, expiresAt = expiry, readOnce = true)

        val entitySlot = slot<NoteEntity>()
        coVerify { mockDao.upsert(capture(entitySlot)) }
        assertEquals(expiry, entitySlot.captured.expiresAt)
        assertTrue(entitySlot.captured.readOnce)
    }

    @Test
    fun `getNoteById decrypts correctly`() = runTest {
        val entity = NoteEntity(
            id = "abc",
            ciphertext = fakeCiphertext,
            createdAt = 1000L,
            expiresAt = null,
            readOnce = false,
            deleted = false
        )
        coEvery { mockDao.getById("abc") } returns entity

        val note = repo.getNoteById("abc", noteKey)

        assertNotNull(note)
        assertEquals("abc", note!!.id)
        assertEquals("hello", note.text)
    }

    @Test
    fun `getNoteById returns null for missing note`() = runTest {
        coEvery { mockDao.getById(any()) } returns null

        val note = repo.getNoteById("missing", noteKey)

        assertNull(note)
    }

    @Test
    fun `getNoteById returns null for deleted note`() = runTest {
        val entity = NoteEntity(
            id = "del",
            ciphertext = fakeCiphertext,
            createdAt = 1000L,
            expiresAt = null,
            readOnce = false,
            deleted = true
        )
        coEvery { mockDao.getById("del") } returns entity

        val note = repo.getNoteById("del", noteKey)

        assertNull(note)
    }

    @Test
    fun `listNotes returns all active notes`() = runTest {
        val entities = listOf(
            NoteEntity("1", fakeCiphertext, 1000L, null, false, false),
            NoteEntity("2", fakeCiphertext, 2000L, null, false, false)
        )
        coEvery { mockDao.listActive() } returns entities

        val notes = repo.listNotes(noteKey)

        assertEquals(2, notes.size)
    }

    @Test
    fun `updateNote re-encrypts and overwrites ciphertext`() = runTest {
        val entity = NoteEntity("1", fakeCiphertext, 1000L, null, false, false)
        coEvery { mockDao.getById("1") } returns entity
        coEvery { mockDao.overwriteCiphertext(any(), any()) } returns 1

        val note = Note(
            id = "1", text = "updated", checklist = emptyList(),
            labels = emptyList(), attachments = emptyList(),
            pinned = false, createdAt = 1000L, expiresAt = null, readOnce = false
        )
        val result = repo.updateNote(note, noteKey)

        assertTrue(result)
        coVerify { mockDao.overwriteCiphertext("1", fakeCiphertext) }
    }

    @Test
    fun `updateNote returns false for missing note`() = runTest {
        coEvery { mockDao.getById(any()) } returns null

        val note = Note(
            id = "x", text = "t", checklist = emptyList(),
            labels = emptyList(), attachments = emptyList(),
            pinned = false, createdAt = 0L, expiresAt = null, readOnce = false
        )
        val result = repo.updateNote(note, noteKey)

        assertFalse(result)
    }

    @Test
    fun `deleteNote zeroes ciphertext then soft-deletes`() = runTest {
        val entity = NoteEntity("1", ByteArray(64) { 0xFF.toByte() }, 1000L, null, false, false)
        coEvery { mockDao.getById("1") } returns entity
        coEvery { mockDao.overwriteCiphertext(any(), any()) } returns 1
        coEvery { mockDao.softDelete(any()) } returns 1

        repo.deleteNote("1")

        val zeroSlot = slot<ByteArray>()
        coVerify { mockDao.overwriteCiphertext(eq("1"), capture(zeroSlot)) }
        assertTrue(zeroSlot.captured.all { it == 0.toByte() })
        coVerify { mockDao.softDelete("1") }
    }

    @Test
    fun `deleteNote does nothing for missing note`() = runTest {
        coEvery { mockDao.getById(any()) } returns null

        repo.deleteNote("ghost")

        coVerify(exactly = 0) { mockDao.overwriteCiphertext(any(), any()) }
        coVerify(exactly = 0) { mockDao.softDelete(any()) }
    }
}
