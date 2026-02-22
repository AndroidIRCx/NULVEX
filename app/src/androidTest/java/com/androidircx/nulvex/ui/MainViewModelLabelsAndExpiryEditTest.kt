package com.androidircx.nulvex.ui

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.VaultServiceLocator
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelLabelsAndExpiryEditTest {

    private lateinit var app: Application
    private lateinit var vm: MainViewModel

    @Before
    fun setUp() = runBlocking {
        app = ApplicationProvider.getApplicationContext()
        VaultServiceLocator.init(app)
        VaultServiceLocator.panicWipeService().wipeAll()
        app.getSharedPreferences("nulvex_app_settings", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        vm = MainViewModel(app)
    }

    @Test
    fun createStandaloneLabel_addsLabelWithoutAnyNote() {
        vm.createStandaloneLabel("Work")

        assertTrue(vm.uiState.value.savedLabels.contains("Work"))
    }

    private suspend fun waitUntil(timeoutMs: Long = 3000, stepMs: Long = 100, condition: () -> Boolean) {
        val maxSteps = (timeoutMs / stepMs).toInt().coerceAtLeast(1)
        repeat(maxSteps) {
            if (condition()) return
            delay(stepMs)
        }
        assertTrue("Condition was not met within ${timeoutMs}ms", condition())
    }

    @Test
    fun updateNoteText_updatesExpiryTime() = runBlocking {
        val auth = VaultServiceLocator.vaultAuthController()
        val vaultService = VaultServiceLocator.vaultService()
        val pin = "1357".toCharArray()
        auth.setupRealPin(pin.copyOf())
        val profile = auth.unlockWithPin(pin.copyOf())
        assertTrue(profile != null)

        val noteId = "expiry-edit-note"
        val oldExpiry = System.currentTimeMillis() + 60_000L
        val newExpiry = System.currentTimeMillis() + 3_600_000L
        vaultService.createNote(
            id = noteId,
            text = "before",
            expiresAt = oldExpiry
        )

        vm.unlock("1357")
        waitUntil { vm.uiState.value.notes.any { it.id == noteId } }

        vm.openNote(noteId)
        waitUntil { vm.uiState.value.selectedNote?.id == noteId }

        vm.updateNoteText(noteId, "after", newExpiry)
        waitUntil { vm.uiState.value.selectedNote?.expiresAt == newExpiry }

        val selected = vm.uiState.value.selectedNote
        assertEquals("after", selected?.text)
        assertEquals(newExpiry, selected?.expiresAt)
    }
}
