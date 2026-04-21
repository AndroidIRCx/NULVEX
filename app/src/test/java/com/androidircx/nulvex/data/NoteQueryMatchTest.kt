package com.androidircx.nulvex.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NoteQueryMatchTest {

    private fun sampleNote(): Note {
        return Note(
            id = "n1",
            text = "Service account password",
            checklist = listOf(
                ChecklistItem(id = "c1", text = "rotate token monthly", checked = false)
            ),
            labels = listOf("ops", "credentials"),
            attachments = listOf(
                NoteAttachment(
                    id = "a1",
                    name = "vault-export.nulvxbk",
                    mimeType = "application/x-nulvex-backup",
                    byteCount = 1024L
                )
            ),
            shareKeyId = null,
            pinned = false,
            createdAt = 1L,
            expiresAt = null,
            readOnce = false
        )
    }

    @Test
    fun `blank query always matches`() {
        assertTrue(sampleNote().matchesQuery("   "))
    }

    @Test
    fun `query matches note text`() {
        assertTrue(sampleNote().matchesQuery("password"))
    }

    @Test
    fun `query matches labels case-insensitively`() {
        assertTrue(sampleNote().matchesQuery("CREDENTIALS"))
    }

    @Test
    fun `query matches attachment name`() {
        assertTrue(sampleNote().matchesQuery("nulvxbk"))
    }

    @Test
    fun `query matches checklist item text`() {
        assertTrue(sampleNote().matchesQuery("rotate token"))
    }

    @Test
    fun `query returns false when no field matches`() {
        assertFalse(sampleNote().matchesQuery("no-such-string"))
    }
}

