package com.androidircx.nulvex.data

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SelfDestructServiceTest {

    private lateinit var mockDb: NulvexDatabase
    private lateinit var mockDao: NoteDao
    private lateinit var mockOpenHelper: SupportSQLiteOpenHelper
    private lateinit var mockSupportDb: SupportSQLiteDatabase
    private lateinit var service: SelfDestructService

    @Before
    fun setUp() {
        mockDao = mockk()
        mockOpenHelper = mockk()
        mockSupportDb = mockk()
        mockDb = mockk()

        every { mockDb.noteDao() } returns mockDao
        every { mockDb.openHelper } returns mockOpenHelper
        every { mockOpenHelper.writableDatabase } returns mockSupportDb
        every { mockSupportDb.execSQL(any()) } just runs

        service = SelfDestructService(mockDb)
    }

    @Test
    fun `sweepExpired does nothing when no expired notes`() = runTest {
        coEvery { mockDao.listExpired(any()) } returns emptyList()
        coEvery { mockDao.purgeDeleted() } returns 0

        service.sweepExpired(now = 9999L)

        coVerify(exactly = 0) { mockDao.overwriteCiphertext(any(), any()) }
        coVerify(exactly = 0) { mockDao.softDelete(any()) }
        coVerify { mockDao.purgeDeleted() }
    }

    @Test
    fun `sweepExpired zeroes ciphertext before soft-delete`() = runTest {
        val note = NoteEntity(
            id = "exp1",
            ciphertext = ByteArray(32) { 0xAB.toByte() },
            createdAt = 0L,
            expiresAt = 1000L,
            readOnce = false,
            deleted = false
        )
        coEvery { mockDao.listExpired(any()) } returns listOf(note)
        coEvery { mockDao.overwriteCiphertext(any(), any()) } returns 1
        coEvery { mockDao.softDelete(any()) } returns 1
        coEvery { mockDao.purgeDeleted() } returns 1

        service.sweepExpired(now = 2000L)

        val zeroSlot = slot<ByteArray>()
        coVerify { mockDao.overwriteCiphertext(eq("exp1"), capture(zeroSlot)) }
        assertTrue(zeroSlot.captured.all { it == 0.toByte() })
        coVerify { mockDao.softDelete("exp1") }
    }

    @Test
    fun `sweepExpired zeroed ciphertext matches original size`() = runTest {
        val cipherSize = 128
        val note = NoteEntity("n1", ByteArray(cipherSize) { 0xFF.toByte() }, 0L, 100L, false, false)
        coEvery { mockDao.listExpired(any()) } returns listOf(note)
        coEvery { mockDao.overwriteCiphertext(any(), any()) } returns 1
        coEvery { mockDao.softDelete(any()) } returns 1
        coEvery { mockDao.purgeDeleted() } returns 1

        service.sweepExpired(now = 200L)

        val zeroSlot = slot<ByteArray>()
        coVerify { mockDao.overwriteCiphertext(any(), capture(zeroSlot)) }
        assertEquals(cipherSize, zeroSlot.captured.size)
    }

    @Test
    fun `sweepExpired purges all deleted after sweep`() = runTest {
        coEvery { mockDao.listExpired(any()) } returns emptyList()
        coEvery { mockDao.purgeDeleted() } returns 5

        service.sweepExpired()

        coVerify { mockDao.purgeDeleted() }
    }

    @Test
    fun `sweepExpired executes VACUUM when vacuum is true`() = runTest {
        coEvery { mockDao.listExpired(any()) } returns emptyList()
        coEvery { mockDao.purgeDeleted() } returns 0

        service.sweepExpired(vacuum = true)

        coVerify { mockSupportDb.execSQL("VACUUM") }
    }

    @Test
    fun `sweepExpired skips VACUUM when vacuum is false`() = runTest {
        coEvery { mockDao.listExpired(any()) } returns emptyList()
        coEvery { mockDao.purgeDeleted() } returns 0

        service.sweepExpired(vacuum = false)

        coVerify(exactly = 0) { mockSupportDb.execSQL(any()) }
    }

    @Test
    fun `sweepExpired processes multiple expired notes`() = runTest {
        val notes = listOf(
            NoteEntity("a", ByteArray(16), 0L, 100L, false, false),
            NoteEntity("b", ByteArray(32), 0L, 200L, false, false),
            NoteEntity("c", ByteArray(48), 0L, 300L, false, false)
        )
        coEvery { mockDao.listExpired(any()) } returns notes
        coEvery { mockDao.overwriteCiphertext(any(), any()) } returns 1
        coEvery { mockDao.softDelete(any()) } returns 1
        coEvery { mockDao.purgeDeleted() } returns 3

        service.sweepExpired(now = 9999L)

        coVerify(exactly = 3) { mockDao.overwriteCiphertext(any(), any()) }
        coVerify(exactly = 3) { mockDao.softDelete(any()) }
    }
}
