package com.androidircx.nulvex.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidircx.nulvex.VaultServiceLocator
import com.androidircx.nulvex.ads.AdManager
import com.androidircx.nulvex.data.ChecklistItem
import com.androidircx.nulvex.data.Note
import com.androidircx.nulvex.security.VaultProfile
import com.androidircx.nulvex.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

data class UiState(
    val screen: Screen = Screen.Unlock,
    val notes: List<Note> = emptyList(),
    val selectedNote: Note? = null,
    val lastProfile: VaultProfile? = null,
    val error: String? = null,
    val isBusy: Boolean = false,
    val isSetup: Boolean = false,
    val isDecoyEnabled: Boolean = false,
    val lockTimeoutMs: Long = 60_000L,
    val defaultExpiry: String = "none",
    val defaultReadOnce: Boolean = false,
    val biometricEnabled: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val searchQuery: String = "",
    val activeLabel: String? = null,
    val attachmentPreviews: Map<String, Bitmap> = emptyMap(),
    val wrongAttempts: Int = 0,
    val lockoutUntil: Long = 0L,
    val isAdFree: Boolean = false,
    /** Epoch millis when the ad-free window expires (0 = not active). */
    val adFreeUntil: Long = 0L,
    /** Accumulated share credits earned by watching rewarded ads. */
    val shareCredits: Int = 0
)

sealed class Screen {
    data object Onboarding : Screen()
    data object Setup : Screen()
    data object Unlock : Screen()
    data object Vault : Screen()
    data object NewNote : Screen()
    data object NoteDetail : Screen()
    data object Settings : Screen()
}

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val authController = VaultServiceLocator.vaultAuthController()
    private val vaultService = VaultServiceLocator.vaultService()
    private val panicWipeService = VaultServiceLocator.panicWipeService()
    private val appPreferences = VaultServiceLocator.appPreferences()
    private val adPreferences = VaultServiceLocator.adPreferences()
    private var inactivityJob: Job? = null

    var uiState = androidx.compose.runtime.mutableStateOf(
        UiState(
            screen = when {
                !appPreferences.hasSeenOnboarding() -> Screen.Onboarding
                authController.isSetup() -> Screen.Unlock
                else -> Screen.Setup
            },
            isSetup = authController.isSetup(),
            isDecoyEnabled = authController.isDecoyEnabled(),
            lockTimeoutMs = appPreferences.getLockTimeoutMs(),
            defaultExpiry = appPreferences.getDefaultExpiry(),
            defaultReadOnce = appPreferences.getDefaultReadOnce(),
            biometricEnabled = appPreferences.isBiometricEnabled(),
            themeMode = ThemeMode.fromId(appPreferences.getThemeMode()),
            wrongAttempts = appPreferences.getWrongAttempts(),
            lockoutUntil = appPreferences.getLockoutUntil(),
            isAdFree = adPreferences.isAdFree(),
            adFreeUntil = adPreferences.getAdFreeUntil(),
            shareCredits = adPreferences.getShareCredits()
        )
    )
        private set

    fun completeOnboarding() {
        appPreferences.setHasSeenOnboarding(true)
        uiState.value = uiState.value.copy(
            screen = if (authController.isSetup()) Screen.Unlock else Screen.Setup
        )
    }

    /** Re-checks the ad-free timer and share credits; call from onResume. */
    fun refreshAdFreeState() {
        uiState.value = uiState.value.copy(
            isAdFree = adPreferences.isAdFree(),
            adFreeUntil = adPreferences.getAdFreeUntil(),
            shareCredits = adPreferences.getShareCredits()
        )
    }

    /**
     * Called when the user earns a "no_ads" reward.
     * [rewardAmount] comes from AdMob (10 units per view = 10 minutes).
     * Repeated watches stack on top of any remaining time.
     */
    fun grantAdFree(rewardAmount: Int) {
        adPreferences.extendAdFreeBy(rewardAmount.toLong() * AdManager.AD_FREE_MILLIS_PER_UNIT)
        uiState.value = uiState.value.copy(
            isAdFree = true,
            adFreeUntil = adPreferences.getAdFreeUntil()
        )
    }

    /**
     * Called when the user earns a "share" reward.
     * [rewardAmount] comes from AdMob (1 unit per view = 1 share credit).
     */
    fun grantShareCredits(rewardAmount: Int) {
        adPreferences.addShareCredits(rewardAmount)
        uiState.value = uiState.value.copy(
            shareCredits = adPreferences.getShareCredits()
        )
    }

    fun setupPins(realPin: String, decoyPin: String?, onComplete: (() -> Unit)? = null) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            authController.setupRealPin(realPin.toCharArray())
            if (!decoyPin.isNullOrBlank()) {
                authController.setupDecoyPin(decoyPin.toCharArray())
            } else {
                authController.clearDecoyPin()
            }
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    isSetup = true,
                    screen = Screen.Unlock,
                    error = null,
                    isBusy = false,
                    isDecoyEnabled = authController.isDecoyEnabled(),
                    lockTimeoutMs = appPreferences.getLockTimeoutMs(),
                    defaultExpiry = appPreferences.getDefaultExpiry(),
                    defaultReadOnce = appPreferences.getDefaultReadOnce(),
                    biometricEnabled = appPreferences.isBiometricEnabled(),
                    themeMode = ThemeMode.fromId(appPreferences.getThemeMode())
                )
                onComplete?.invoke()
            }
        }
    }

    fun unlock(pin: String) {
        val now = System.currentTimeMillis()
        val current = uiState.value
        if (now < current.lockoutUntil) {
            val secs = (current.lockoutUntil - now) / 1000L
            uiState.value = current.copy(error = "Too many attempts. Try again in ${secs}s")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            val profile = authController.unlockWithPin(pin.toCharArray())
            if (profile == null) {
                withContext(Dispatchers.Main) {
                    val attempts = uiState.value.wrongAttempts + 1
                    val lockoutMs = when {
                        attempts >= 10 -> 10 * 60_000L
                        attempts >= 7  -> 2 * 60_000L
                        attempts >= 5  -> 30_000L
                        else           -> 0L
                    }
                    val newLockoutUntil = if (lockoutMs > 0L) System.currentTimeMillis() + lockoutMs else 0L
                    val errorMsg = if (newLockoutUntil > 0L)
                        "Too many attempts. Try again in ${lockoutMs / 1000}s"
                    else
                        "Invalid PIN"
                    appPreferences.setWrongAttempts(attempts)
                    appPreferences.setLockoutUntil(newLockoutUntil)
                    uiState.value = uiState.value.copy(
                        error = errorMsg,
                        isBusy = false,
                        wrongAttempts = attempts,
                        lockoutUntil = newLockoutUntil
                    )
                }
                return@launch
            }
            val notes = vaultService.listNotes()
            withContext(Dispatchers.Main) {
                appPreferences.setWrongAttempts(0)
                appPreferences.setLockoutUntil(0L)
                uiState.value = uiState.value.copy(
                    screen = Screen.Vault,
                    lastProfile = profile,
                    notes = notes,
                    error = null,
                    isBusy = false,
                    wrongAttempts = 0,
                    lockoutUntil = 0L,
                    isDecoyEnabled = authController.isDecoyEnabled(),
                    lockTimeoutMs = appPreferences.getLockTimeoutMs(),
                    defaultExpiry = appPreferences.getDefaultExpiry(),
                    defaultReadOnce = appPreferences.getDefaultReadOnce(),
                    biometricEnabled = appPreferences.isBiometricEnabled(),
                    themeMode = ThemeMode.fromId(appPreferences.getThemeMode())
                )
                resetInactivityTimer()
            }
        }
    }

    fun updateNoteText(noteId: String, newText: String) {
        val note = uiState.value.selectedNote ?: return
        val updated = note.copy(text = newText)
        viewModelScope.launch(Dispatchers.IO) {
            vaultService.updateNote(updated)
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(selectedNote = updated)
            }
        }
    }

    fun lock() {
        authController.lock()
        clearInactivityTimer()
        uiState.value = uiState.value.copy(
            screen = Screen.Unlock,
            notes = emptyList(),
            selectedNote = null,
            attachmentPreviews = emptyMap()
        )
    }

    fun openNewNote() {
        uiState.value = uiState.value.copy(screen = Screen.NewNote, error = null)
        resetInactivityTimer()
    }

    fun openSettings() {
        uiState.value = uiState.value.copy(
            screen = Screen.Settings,
            error = null,
            isDecoyEnabled = authController.isDecoyEnabled(),
            lockTimeoutMs = appPreferences.getLockTimeoutMs(),
            defaultExpiry = appPreferences.getDefaultExpiry(),
            defaultReadOnce = appPreferences.getDefaultReadOnce(),
            biometricEnabled = appPreferences.isBiometricEnabled(),
            themeMode = ThemeMode.fromId(appPreferences.getThemeMode())
        )
        resetInactivityTimer()
    }

    fun closeSettings() {
        refreshNotes(Screen.Vault)
    }

    fun updateDecoyPin(pin: String, confirm: String) {
        if (pin.isBlank()) {
            uiState.value = uiState.value.copy(error = "Decoy PIN cannot be empty")
            return
        }
        if (pin != confirm) {
            uiState.value = uiState.value.copy(error = "Decoy PINs do not match")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            if (authController.isDecoyEnabled()) {
                panicWipeService.wipeDecoyOnly()
            }
            authController.setupDecoyPin(pin.toCharArray())
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    isBusy = false,
                    error = null,
                    isDecoyEnabled = authController.isDecoyEnabled()
                )
            }
        }
    }

    fun disableDecoyPin() {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            panicWipeService.wipeDecoyOnly()
            authController.clearDecoyPin()
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    isBusy = false,
                    error = null,
                    isDecoyEnabled = authController.isDecoyEnabled()
                )
            }
        }
    }

    fun updateLockTimeout(timeoutMs: Long) {
        appPreferences.setLockTimeoutMs(timeoutMs)
        uiState.value = uiState.value.copy(lockTimeoutMs = timeoutMs)
        resetInactivityTimer()
    }

    fun updateDefaultExpiry(choice: String) {
        appPreferences.setDefaultExpiry(choice)
        uiState.value = uiState.value.copy(defaultExpiry = choice)
    }

    fun updateDefaultReadOnce(enabled: Boolean) {
        appPreferences.setDefaultReadOnce(enabled)
        uiState.value = uiState.value.copy(defaultReadOnce = enabled)
    }

    fun updateThemeMode(mode: ThemeMode) {
        appPreferences.setThemeMode(mode.id)
        uiState.value = uiState.value.copy(themeMode = mode)
    }

    fun enableBiometric() {
        appPreferences.setBiometricEnabled(true)
        uiState.value = uiState.value.copy(biometricEnabled = true)
    }

    fun disableBiometric() {
        appPreferences.setBiometricEnabled(false)
        uiState.value = uiState.value.copy(biometricEnabled = false)
    }

    fun unlockWithMasterKey(masterKey: ByteArray) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                vaultService.unlockWithMasterKey(masterKey, VaultProfile.REAL)
                val notes = vaultService.listNotes()
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        screen = Screen.Vault,
                        lastProfile = VaultProfile.REAL,
                        notes = notes,
                        error = null,
                        isBusy = false
                    )
                    resetInactivityTimer()
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        error = "Fingerprint unlock failed",
                        isBusy = false
                    )
                }
            }
        }
    }

    fun changeRealPin(oldPin: String, newPin: String, confirm: String) {
        if (oldPin.isBlank() || newPin.isBlank()) {
            uiState.value = uiState.value.copy(error = "PIN fields cannot be empty")
            return
        }
        if (newPin != confirm) {
            uiState.value = uiState.value.copy(error = "New PINs do not match")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            val ok = authController.changeRealPin(oldPin.toCharArray(), newPin.toCharArray())
            withContext(Dispatchers.Main) {
                uiState.value = if (ok) {
                    uiState.value.copy(
                        isBusy = false,
                        error = null
                    )
                } else {
                    uiState.value.copy(
                        isBusy = false,
                        error = "Current PIN is incorrect"
                    )
                }
            }
        }
    }

    fun openNote(id: String) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            val note = vaultService.readNote(id)
            withContext(Dispatchers.Main) {
                if (note == null) {
                    uiState.value = uiState.value.copy(
                        error = "Note not found",
                        isBusy = false
                    )
                } else {
                    uiState.value = uiState.value.copy(
                        selectedNote = note,
                        screen = Screen.NoteDetail,
                        error = null,
                        isBusy = false,
                        attachmentPreviews = emptyMap()
                    )
                    resetInactivityTimer()
                }
            }
        }
    }

    fun closeNoteDetail() {
        val note = uiState.value.selectedNote
        if (note?.readOnce == true) {
            deleteNote(note.id)
        } else {
            refreshNotes(Screen.Vault)
        }
    }

    fun createNote(
        text: String,
        checklist: List<ChecklistItem>,
        labels: List<String>,
        pinned: Boolean,
        attachments: List<Uri>,
        expiresAt: Long?,
        readOnce: Boolean
    ) {
        val hasContent = text.isNotBlank() || checklist.any { it.text.isNotBlank() } || attachments.isNotEmpty()
        if (!hasContent) {
            uiState.value = uiState.value.copy(error = "Note cannot be empty")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            val noteId = java.util.UUID.randomUUID().toString()
            val storedAttachments = vaultService.storeAttachments(noteId, attachments)
            vaultService.createNote(
                id = noteId,
                text = text,
                checklist = checklist,
                labels = labels,
                attachments = storedAttachments,
                pinned = pinned,
                expiresAt = expiresAt,
                readOnce = readOnce
            )
            val notes = vaultService.listNotes()
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    screen = Screen.Vault,
                    notes = notes,
                    error = null,
                    isBusy = false,
                    attachmentPreviews = emptyMap()
                )
                resetInactivityTimer()
            }
        }
    }

    fun deleteNote(id: String) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            vaultService.deleteNote(id)
            val notes = vaultService.listNotes()
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    screen = Screen.Vault,
                    notes = notes,
                    selectedNote = null,
                    error = null,
                    isBusy = false,
                    attachmentPreviews = emptyMap()
                )
                resetInactivityTimer()
            }
        }
    }

    fun togglePinned(noteId: String) {
        val note = findNote(noteId) ?: return
        persistNoteUpdate(note.copy(pinned = !note.pinned))
    }

    fun toggleChecklistItem(noteId: String, itemId: String) {
        val note = findNote(noteId) ?: return
        val updatedChecklist = note.checklist.map { item ->
            if (item.id == itemId) item.copy(checked = !item.checked) else item
        }
        persistNoteUpdate(note.copy(checklist = updatedChecklist))
    }

    fun updateChecklistText(noteId: String, itemId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        val note = findNote(noteId) ?: return
        val updatedChecklist = note.checklist.map { item ->
            if (item.id == itemId) item.copy(text = trimmed) else item
        }
        persistNoteUpdate(note.copy(checklist = updatedChecklist))
    }

    fun moveChecklistItem(noteId: String, itemId: String, direction: Int) {
        val note = findNote(noteId) ?: return
        val index = note.checklist.indexOfFirst { it.id == itemId }
        if (index < 0) return
        val newIndex = index + direction
        if (newIndex < 0 || newIndex >= note.checklist.size) return
        val mutable = note.checklist.toMutableList()
        val item = mutable.removeAt(index)
        mutable.add(newIndex, item)
        persistNoteUpdate(note.copy(checklist = mutable))
    }

    fun addChecklistItem(noteId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        val note = findNote(noteId) ?: return
        val updatedChecklist = note.checklist + ChecklistItem(
            id = java.util.UUID.randomUUID().toString(),
            text = trimmed,
            checked = false
        )
        persistNoteUpdate(note.copy(checklist = updatedChecklist))
    }

    fun removeChecklistItem(noteId: String, itemId: String) {
        val note = findNote(noteId) ?: return
        val updatedChecklist = note.checklist.filterNot { it.id == itemId }
        persistNoteUpdate(note.copy(checklist = updatedChecklist))
    }

    fun removeAttachment(noteId: String, attachmentId: String) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            val ok = vaultService.removeAttachment(noteId, attachmentId)
            val notes = vaultService.listNotes()
            withContext(Dispatchers.Main) {
                val selected = notes.firstOrNull { it.id == noteId }
                uiState.value = uiState.value.copy(
                    notes = notes,
                    selectedNote = selected,
                    error = if (ok) null else "Attachment removal failed",
                    isBusy = false,
                    attachmentPreviews = uiState.value.attachmentPreviews - attachmentId
                )
                resetInactivityTimer()
            }
        }
    }

    fun addLabel(noteId: String, label: String) {
        val trimmed = label.trim()
        if (trimmed.isBlank()) return
        val note = findNote(noteId) ?: return
        val updatedLabels = (note.labels + trimmed).distinct()
        persistNoteUpdate(note.copy(labels = updatedLabels))
    }

    fun removeLabel(noteId: String, label: String) {
        val note = findNote(noteId) ?: return
        val updatedLabels = note.labels.filterNot { it == label }
        persistNoteUpdate(note.copy(labels = updatedLabels))
    }

    fun updateSearchQuery(query: String) {
        uiState.value = uiState.value.copy(searchQuery = query)
    }

    fun updateActiveLabel(label: String?) {
        uiState.value = uiState.value.copy(activeLabel = label)
    }

    fun loadAttachmentPreview(noteId: String, attachmentId: String) {
        val current = uiState.value.attachmentPreviews
        if (current.containsKey(attachmentId)) return
        viewModelScope.launch(Dispatchers.IO) {
            val bytes = vaultService.loadAttachment(noteId, attachmentId) ?: return@launch
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@launch
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    attachmentPreviews = uiState.value.attachmentPreviews + (attachmentId to bitmap)
                )
            }
        }
    }

    fun clearError() {
        uiState.value = uiState.value.copy(error = null)
    }

    fun showError(message: String) {
        uiState.value = uiState.value.copy(error = message, isBusy = false)
    }

    fun panicWipe() {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            panicWipeService.wipeAll()
            withContext(Dispatchers.Main) {
                clearInactivityTimer()
                uiState.value = UiState(
                    screen = Screen.Setup,
                    isSetup = false,
                    lockTimeoutMs = appPreferences.getLockTimeoutMs(),
                    defaultExpiry = appPreferences.getDefaultExpiry(),
                    defaultReadOnce = appPreferences.getDefaultReadOnce(),
                    biometricEnabled = appPreferences.isBiometricEnabled(),
                    themeMode = ThemeMode.fromId(appPreferences.getThemeMode())
                )
            }
        }
    }

    fun onUserInteraction() {
        resetInactivityTimer()
    }

    fun onAppBackgrounded() {
        if (shouldAutoLock()) {
            lock()
        } else {
            clearInactivityTimer()
        }
    }

    private fun refreshNotes(targetScreen: Screen) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            val notes = vaultService.listNotes()
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    screen = targetScreen,
                    notes = notes,
                    selectedNote = null,
                    error = null,
                    isBusy = false,
                    isDecoyEnabled = authController.isDecoyEnabled(),
                    lockTimeoutMs = appPreferences.getLockTimeoutMs(),
                    defaultExpiry = appPreferences.getDefaultExpiry(),
                    defaultReadOnce = appPreferences.getDefaultReadOnce(),
                    biometricEnabled = appPreferences.isBiometricEnabled(),
                    themeMode = ThemeMode.fromId(appPreferences.getThemeMode()),
                    attachmentPreviews = emptyMap()
                )
                resetInactivityTimer()
            }
        }
    }

    private fun persistNoteUpdate(note: Note) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            val ok = vaultService.updateNote(note)
            val notes = vaultService.listNotes()
            withContext(Dispatchers.Main) {
                val selected = if (uiState.value.selectedNote?.id == note.id) note else uiState.value.selectedNote
                uiState.value = uiState.value.copy(
                    notes = notes,
                    selectedNote = selected,
                    error = if (ok) null else "Note update failed",
                    isBusy = false
                )
                resetInactivityTimer()
            }
        }
    }

    private fun findNote(noteId: String): Note? {
        val state = uiState.value
        return state.selectedNote?.takeIf { it.id == noteId } ?: state.notes.firstOrNull { it.id == noteId }
    }

    private fun setBusy(value: Boolean) {
        uiState.value = uiState.value.copy(isBusy = value, error = null)
    }

    private fun shouldAutoLock(): Boolean {
        return when (uiState.value.screen) {
            Screen.Vault, Screen.NewNote, Screen.NoteDetail -> true
            Screen.Onboarding, Screen.Setup, Screen.Unlock, Screen.Settings -> false
        }
    }

    private fun resetInactivityTimer() {
        if (!shouldAutoLock()) return
        val timeout = uiState.value.lockTimeoutMs
        if (timeout <= 0L) return
        inactivityJob?.cancel()
        inactivityJob = viewModelScope.launch {
            delay(timeout)
            if (shouldAutoLock()) {
                lock()
            }
        }
    }

    private fun clearInactivityTimer() {
        inactivityJob?.cancel()
        inactivityJob = null
    }
}
