package com.androidircx.nulvex.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidircx.nulvex.VaultServiceLocator
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
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

sealed class Screen {
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
    private var inactivityJob: Job? = null

    var uiState = androidx.compose.runtime.mutableStateOf(
        UiState(
            screen = if (authController.isSetup()) Screen.Unlock else Screen.Setup,
            isSetup = authController.isSetup(),
            isDecoyEnabled = authController.isDecoyEnabled(),
            lockTimeoutMs = appPreferences.getLockTimeoutMs(),
            defaultExpiry = appPreferences.getDefaultExpiry(),
            defaultReadOnce = appPreferences.getDefaultReadOnce(),
            biometricEnabled = appPreferences.isBiometricEnabled(),
            themeMode = ThemeMode.fromId(appPreferences.getThemeMode())
        )
    )
        private set

    fun setupPins(realPin: String, decoyPin: String?) {
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
            }
        }
    }

    fun unlock(pin: String) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            val profile = authController.unlockWithPin(pin.toCharArray())
            if (profile == null) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        error = "Invalid PIN",
                        isBusy = false
                    )
                }
                return@launch
            }
            val notes = vaultService.listNotes()
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    screen = Screen.Vault,
                    lastProfile = profile,
                    notes = notes,
                    error = null,
                    isBusy = false,
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

    fun lock() {
        authController.lock()
        clearInactivityTimer()
        uiState.value = uiState.value.copy(
            screen = Screen.Unlock,
            notes = emptyList(),
            selectedNote = null
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
                        isBusy = false
                    )
                    resetInactivityTimer()
                }
            }
        }
    }

    fun closeNoteDetail() {
        refreshNotes(Screen.Vault)
    }

    fun createNote(content: String, expiresAt: Long?, readOnce: Boolean) {
        if (content.isBlank()) {
            uiState.value = uiState.value.copy(error = "Note cannot be empty")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            vaultService.createNote(content, expiresAt, readOnce)
            val notes = vaultService.listNotes()
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    screen = Screen.Vault,
                    notes = notes,
                    error = null,
                    isBusy = false
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
                    isBusy = false
                )
                resetInactivityTimer()
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
                    themeMode = ThemeMode.fromId(appPreferences.getThemeMode())
                )
                resetInactivityTimer()
            }
        }
    }

    private fun setBusy(value: Boolean) {
        uiState.value = uiState.value.copy(isBusy = value, error = null)
    }

    private fun shouldAutoLock(): Boolean {
        return when (uiState.value.screen) {
            Screen.Vault, Screen.NewNote, Screen.NoteDetail -> true
            Screen.Setup, Screen.Unlock, Screen.Settings -> false
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
