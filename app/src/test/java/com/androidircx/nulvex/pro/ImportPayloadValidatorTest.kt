package com.androidircx.nulvex.pro

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.ByteArrayInputStream

class ImportPayloadValidatorTest {

    @Test
    fun `supported MIME types are recognized`() {
        assertTrue(ImportPayloadValidator.isSupportedMime(NulvexFileTypes.NOTE_SHARE_MIME))
        assertTrue(ImportPayloadValidator.isSupportedMime(NulvexFileTypes.BACKUP_MIME))
        assertTrue(ImportPayloadValidator.isSupportedMime(NulvexFileTypes.KEY_MANAGER_MIME))
        assertFalse(ImportPayloadValidator.isSupportedMime("application/octet-stream"))
    }

    @Test
    fun `readWithLimit returns bytes within limit`() {
        val input = ByteArray(512) { it.toByte() }
        val decoded = ImportPayloadValidator.readWithLimit(
            input = ByteArrayInputStream(input),
            mimeType = NulvexFileTypes.NOTE_SHARE_MIME
        )

        assertArrayEquals(input, decoded)
    }

    @Test
    fun `readWithLimit throws for oversized payload`() {
        val oversized = ByteArray(ImportPayloadValidator.KEY_MANAGER_MAX_BYTES + 1)

        assertThrows(PayloadTooLargeException::class.java) {
            ImportPayloadValidator.readWithLimit(
                input = ByteArrayInputStream(oversized),
                mimeType = NulvexFileTypes.KEY_MANAGER_MIME
            )
        }
    }

    @Test
    fun `validateSizeOrThrow throws for unsupported MIME`() {
        assertThrows(UnsupportedImportMimeException::class.java) {
            ImportPayloadValidator.validateSizeOrThrow(10, "text/plain")
        }
    }

    @Test
    fun `maxBytesForMime returns configured limits`() {
        assertEquals(
            ImportPayloadValidator.NOTE_SHARE_MAX_BYTES,
            ImportPayloadValidator.maxBytesForMime(NulvexFileTypes.NOTE_SHARE_MIME)
        )
        assertEquals(
            ImportPayloadValidator.BACKUP_MAX_BYTES,
            ImportPayloadValidator.maxBytesForMime(NulvexFileTypes.BACKUP_MIME)
        )
        assertEquals(
            ImportPayloadValidator.KEY_MANAGER_MAX_BYTES,
            ImportPayloadValidator.maxBytesForMime(NulvexFileTypes.KEY_MANAGER_MIME)
        )
    }
}
