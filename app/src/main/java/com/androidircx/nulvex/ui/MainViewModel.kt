package com.androidircx.nulvex.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.androidircx.nulvex.VaultServiceLocator
import com.androidircx.nulvex.data.Note
import com.androidircx.nulvex.security.VaultProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

data class UiState(
    val screen: Screen = Screen.Unlock,
    val notes: List<Note> = emptyList(),
    val selectedNote: Note? = null,
    val lastProfile: VaultProfile? = null,
    val error: String? = null,
    val isBusy: Boolean = false,
    val isSetup: Boolean = false
)

sealed class Screen {
    data object Setup : Screen()
    data object Unlock : Screen()
    data object Vault : Screen()
    data object NewNote : Screen()
    data object NoteDetail : Screen()
}

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val authController = VaultServiceLocator.vaultAuthController()
    private val vaultService = VaultServiceLocator.vaultService()

    var uiState = androidx.compose.runtime.mutableStateOf(
        UiState(
            screen = if (authController.isSetup()) Screen.Unlock else Screen.Setup,
            isSetup = authController.isSetup()
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
                    isBusy = false
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
                    isBusy = false
                )
            }
        }
    }

    fun lock() {
        authController.lock()
        uiState.value = uiState.value.copy(
            screen = Screen.Unlock,
            notes = emptyList(),
            selectedNote = null
        )
    }

    fun openNewNote() {
        uiState.value = uiState.value.copy(screen = Screen.NewNote, error = null)
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
            }
        }
    }

    fun clearError() {
        uiState.value = uiState.value.copy(error = null)
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
                    isBusy = false
                )
            }
        }
    }

    private fun setBusy(value: Boolean) {
        uiState.value = uiState.value.copy(isBusy = value, error = null)
    }
}
