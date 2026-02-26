package com.androidircx.nulvex.ui

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.androidircx.nulvex.VaultServiceLocator
import com.androidircx.nulvex.reminder.ReminderConstants
import com.androidircx.nulvex.security.AppPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelReminderActionsTest {

    private lateinit var app: Application
    private lateinit var vm: MainViewModel
    private lateinit var appPreferences: AppPreferences

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        VaultServiceLocator.init(app)
        VaultServiceLocator.panicWipeService().wipeAll()
        app.getSharedPreferences("nulvex_app_settings", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        appPreferences = AppPreferences(app)
        vm = MainViewModel(app)
    }

    @Test
    fun handleReminderAction_whenLocked_queuesPendingAction() {
        vm.handleReminderAction(ReminderConstants.ACTION_DONE, "note-1")

        assertEquals(
            ReminderConstants.ACTION_DONE to "note-1",
            appPreferences.getPendingReminderAction()
        )
    }

    @Test
    fun handleReminderAction_openQueued_whenUnlocked_opensNoteDetail() {
        bootstrapUnlockedVaultWithPin()
        vm.createNote(
            text = "Reminder open target",
            checklist = emptyList(),
            labels = emptyList(),
            pinned = false,
            attachments = emptyList(),
            expiresAt = null,
            readOnce = false,
            reminderAt = null
        )
        waitFor { vm.uiState.value.notes.isNotEmpty() && !vm.uiState.value.isBusy }
        val noteId = vm.uiState.value.notes.first().id

        vm.lock()
        waitFor { vm.uiState.value.screen is Screen.Unlock }
        vm.handleReminderAction(ReminderConstants.ACTION_OPEN, noteId)
        assertEquals(
            ReminderConstants.ACTION_OPEN to noteId,
            appPreferences.getPendingReminderAction()
        )

        vm.unlock("1234")
        waitFor {
            vm.uiState.value.screen is Screen.NoteDetail &&
                vm.uiState.value.selectedNote?.id == noteId
        }
        assertEquals(null, appPreferences.getPendingReminderAction())
    }

    @Test
    fun handleReminderAction_snoozeQueued_whenUnlocked_setsReminderAndSchedule() {
        bootstrapUnlockedVaultWithPin()
        vm.createNote(
            text = "Reminder snooze target",
            checklist = emptyList(),
            labels = emptyList(),
            pinned = false,
            attachments = emptyList(),
            expiresAt = null,
            readOnce = false,
            reminderAt = null
        )
        waitFor { vm.uiState.value.notes.isNotEmpty() && !vm.uiState.value.isBusy }
        val noteId = vm.uiState.value.notes.first().id

        vm.lock()
        waitFor { vm.uiState.value.screen is Screen.Unlock }
        vm.handleReminderAction(ReminderConstants.ACTION_SNOOZE, noteId)
        assertEquals(
            ReminderConstants.ACTION_SNOOZE to noteId,
            appPreferences.getPendingReminderAction()
        )

        vm.unlock("1234")
        waitFor {
            vm.uiState.value.notes.firstOrNull { it.id == noteId }?.reminderAt != null
        }
        val reminderAt = vm.uiState.value.notes.first { it.id == noteId }.reminderAt
        assertNotNull(reminderAt)
        val schedules = appPreferences.getReminderSchedules()
        assertTrue(schedules.containsKey(noteId))
        assertTrue((schedules[noteId] ?: 0L) > System.currentTimeMillis())
    }

    @Test
    fun handleReminderAction_blankOrInvalidInput_isIgnoredSafely() {
        vm.handleReminderAction(" ", "note-1")
        vm.handleReminderAction(ReminderConstants.ACTION_OPEN, "   ")

        assertEquals(null, appPreferences.getPendingReminderAction())
    }

    @Test
    fun handleReminderAction_unknownActionQueued_doesNotCrashAfterUnlock() {
        bootstrapUnlockedVaultWithPin()
        vm.createNote(
            text = "Unknown action note",
            checklist = emptyList(),
            labels = emptyList(),
            pinned = false,
            attachments = emptyList(),
            expiresAt = null,
            readOnce = false,
            reminderAt = null
        )
        waitFor { vm.uiState.value.notes.isNotEmpty() && !vm.uiState.value.isBusy }
        val noteId = vm.uiState.value.notes.first().id

        vm.lock()
        waitFor { vm.uiState.value.screen is Screen.Unlock }
        vm.handleReminderAction("drop table reminders", noteId)
        assertEquals("drop table reminders" to noteId, appPreferences.getPendingReminderAction())

        vm.unlock("1234")
        waitFor { vm.uiState.value.screen is Screen.Vault }
        assertEquals(null, appPreferences.getPendingReminderAction())
    }

    private fun bootstrapUnlockedVaultWithPin() {
        vm.setupPins("1234", null)
        waitFor { vm.uiState.value.screen is Screen.Unlock && !vm.uiState.value.isBusy }
        vm.unlock("1234")
        waitFor { vm.uiState.value.screen is Screen.Vault && !vm.uiState.value.isBusy }
    }

    private fun waitFor(timeoutMs: Long = 8_000L, condition: () -> Boolean) {
        val startedAt = System.currentTimeMillis()
        while (!condition()) {
            if (System.currentTimeMillis() - startedAt > timeoutMs) {
                throw AssertionError("Timed out waiting for condition after ${timeoutMs}ms")
            }
            Thread.sleep(25L)
        }
    }
}
