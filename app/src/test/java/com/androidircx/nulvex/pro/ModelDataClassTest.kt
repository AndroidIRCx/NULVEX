package com.androidircx.nulvex.pro

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ModelDataClassTest {

    @Test
    fun `backup record copy updates selected fields and keeps others`() {
        val original = BackupRecord(
            id = "rec-1",
            mediaId = "media-1",
            downloadPathId = "path-1",
            keyId = "key-1",
            downloadToken = "tok",
            downloadExpires = 1234L,
            sizeBytes = 256,
            sha256 = "abc",
            createdAt = 99L
        )

        val updated = original.copy(downloadToken = null, sizeBytes = 1024)

        assertEquals("rec-1", updated.id)
        assertEquals("media-1", updated.mediaId)
        assertEquals("path-1", updated.downloadPathId)
        assertEquals("key-1", updated.keyId)
        assertEquals(null, updated.downloadToken)
        assertEquals(1234L, updated.downloadExpires)
        assertEquals(1024, updated.sizeBytes)
        assertEquals("abc", updated.sha256)
        assertEquals(99L, updated.createdAt)
    }

    @Test
    fun `shared key info equality reflects changed fingerprint`() {
        val a = SharedKeyInfo(
            id = "k1",
            label = "Key A",
            source = "manual",
            format = "xchacha20poly1305_key",
            fingerprint = "fp-1",
            createdAt = 11L
        )
        val b = a.copy(fingerprint = "fp-2")

        assertNotEquals(a, b)
        assertEquals("fp-2", b.fingerprint)
        assertEquals("k1", b.id)
    }
}

