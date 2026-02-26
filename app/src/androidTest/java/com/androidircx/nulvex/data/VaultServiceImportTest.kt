package com.androidircx.nulvex.data

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.VaultServiceLocator
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VaultServiceImportTest {

    private lateinit var app: Application
    private lateinit var vaultService: VaultService

    @Before
    fun setUp() = runBlocking {
        app = ApplicationProvider.getApplicationContext()
        VaultServiceLocator.init(app)
        VaultServiceLocator.panicWipeService().wipeAll()
        val authController = VaultServiceLocator.vaultAuthController()
        authController.setupRealPin("2468".toCharArray())
        val profile = authController.unlockWithPin("2468".toCharArray())
        assertNotNull(profile)
        vaultService = VaultServiceLocator.vaultService()
    }

    @Test
    fun importBackupJsonBytes_supportsSingleNoteShareFormat() = runBlocking {
        val payload = """
            {
              "v": 1,
              "type": "note-share",
              "note": {
                "id": "note-share-1",
                "text": "imported from share",
                "pinned": true,
                "createdAt": 1700000000000,
                "readOnce": true,
                "checklist": [],
                "labels": ["shared"],
                "attachments": []
              }
            }
        """.trimIndent().toByteArray()

        val imported = vaultService.importBackupJsonBytes(payload, merge = false)
        val notes = vaultService.listNotes()
        val note = notes.firstOrNull { it.id == "note-share-1" }

        assertEquals(1, imported)
        assertNotNull(note)
        assertEquals("imported from share", note?.text)
        assertTrue(note?.readOnce == true)
        assertTrue(note?.pinned == true)
    }

    @Test
    fun importBackupJsonBytes_rejectsMalformedJsonPayloads() = runBlocking {
        val baselineCount = vaultService.listNotes().size
        val payloads = listOf(
            "".toByteArray(),
            "not-json".toByteArray(),
            """{"v":1,"notes":[{"id":"a","text":"x"}""".toByteArray(),
            ByteArray(32) { 0x7F }
        )

        payloads.forEach { raw ->
            try {
                vaultService.importBackupJsonBytes(raw, merge = true)
                fail("Expected import to fail for malformed payload")
            } catch (_: Exception) {
                // expected rejection path
            }
        }

        val afterCount = vaultService.listNotes().size
        assertEquals(baselineCount, afterCount)
    }

    @Test
    fun importBackupJsonBytes_rejectsInvalidAttachmentBase64() = runBlocking {
        val payload = """
            {
              "v": 1,
              "notes": [
                {
                  "id": "bad-att-1",
                  "text": "bad attachment",
                  "checklist": [],
                  "labels": [],
                  "attachments": [
                    {"id":"att1","name":"f","mimeType":"text/plain","byteCount":1,"data":"%%%not-base64%%%"}
                  ]
                }
              ]
            }
        """.trimIndent().toByteArray()

        try {
            vaultService.importBackupJsonBytes(payload, merge = true)
            fail("Expected invalid base64 attachment to be rejected")
        } catch (_: Exception) {
            // expected rejection path
        }

        val note = vaultService.readNote("bad-att-1")
        assertTrue(note == null)
    }

    @Test
    fun importBackupJsonBytes_acceptsLargeNotePayloadWithoutCorruption() = runBlocking {
        val largeText = buildString {
            repeat(512_000) { append('A') }
        }
        val payload = """
            {
              "v": 1,
              "notes": [
                {
                  "id": "large-note-1",
                  "text": "$largeText",
                  "checklist": [],
                  "labels": ["bulk"],
                  "attachments": []
                }
              ]
            }
        """.trimIndent().toByteArray()

        val imported = vaultService.importBackupJsonBytes(payload, merge = true)
        val importedNote = vaultService.readNote("large-note-1")

        assertEquals(1, imported)
        assertNotNull(importedNote)
        assertEquals(512_000, importedNote!!.text.length)
    }
}
