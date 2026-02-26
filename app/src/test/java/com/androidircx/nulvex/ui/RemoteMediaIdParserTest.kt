package com.androidircx.nulvex.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteMediaIdParserTest {

    @Test
    fun parsesMediaIdFromFullDownloadLink() {
        val parsed = resolveRemoteMediaIdInput("https://androidircx.com/api/media/download/media-token-123?token=abc#frag")
        assertEquals("media-token-123", parsed)
    }

    @Test
    fun acceptsRawMediaId() {
        val parsed = resolveRemoteMediaIdInput("media-only-777")
        assertEquals("media-only-777", parsed)
    }

    @Test
    fun blankInputReturnsEmptyString() {
        val parsed = resolveRemoteMediaIdInput("   ")
        assertEquals("", parsed)
    }

    @Test
    fun rejectsTraversalPayloads() {
        val parsed = resolveRemoteMediaIdInput("https://androidircx.com/api/media/download/../../etc/passwd")
        assertEquals("", parsed)
    }

    @Test
    fun rejectsSlashesInRawId() {
        val parsed = resolveRemoteMediaIdInput("../media-token")
        assertEquals("", parsed)
    }

    @Test
    fun rejectsNonSafeCharacters() {
        val parsed = resolveRemoteMediaIdInput("media-token?<script>alert(1)</script>")
        assertEquals("", parsed)
    }
}
