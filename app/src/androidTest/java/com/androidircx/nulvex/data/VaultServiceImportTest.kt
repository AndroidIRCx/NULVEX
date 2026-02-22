package com.androidircx.nulvex.data

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.VaultServiceLocator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VaultServiceImportTest {

    private lateinit var app: Application
    private lateinit var vaultService: VaultService

    @Before
    fun setUp() = runTest {
        app = ApplicationProvider.getApplicationContext()
        VaultServiceLocator.init(app)
        val authController = VaultServiceLocator.vaultAuthController()
        authController.setupRealPin("2468".toCharArray())
        val profile = authController.unlockWithPin("2468".toCharArray())
        assertNotNull(profile)
        vaultService = VaultServiceLocator.vaultService()
    }

    @Test
    fun importBackupJsonBytes_supportsSingleNoteShareFormat() = runTest {
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
}
