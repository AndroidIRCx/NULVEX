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
import com.androidircx.nulvex.pro.BackupRecord
import com.androidircx.nulvex.pro.SharedKeyInfo
import com.androidircx.nulvex.security.VaultProfile
import com.androidircx.nulvex.ui.theme.ThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

sealed class PendingImport {
    data class LocalFile(val bytes: ByteArray, val mimeType: String) : PendingImport()
    data class RemoteMedia(val mediaId: String) : PendingImport()
}

data class NewNoteDraft(
    val text: String = "",
    val checklist: List<ChecklistItem> = emptyList(),
    val labels: List<String> = emptyList(),
    val pinned: Boolean = false,
    val expiresAt: Long? = null,
    val readOnce: Boolean = false
)

data class NoteEditDraft(
    val noteId: String,
    val text: String,
    val expiresAt: Long?
)

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
    val decoyBiometricEnabled: Boolean = false,
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
    val shareCredits: Int = 0,
    val hasProFeatures: Boolean = false,
    val billingReady: Boolean = false,
    val removeAdsPrice: String = "Unavailable",
    val proFeaturesPrice: String = "Unavailable",
    val sharedKeys: List<SharedKeyInfo> = emptyList(),
    val backupRecords: List<BackupRecord> = emptyList(),
    val lastBackupMediaId: String = "",
    val backupStatus: String = "",
    val noteShareUrl: String = "",
    val pinScrambleEnabled: Boolean = false,
    val hidePinLengthEnabled: Boolean = false,
    val languageTag: String = "system",
    val savedLabels: List<String> = emptyList(),
    val pendingNoteEdit: NoteEditDraft? = null,
    val newNoteDraft: NewNoteDraft? = null,
    val pendingImport: PendingImport? = null
)

sealed class Screen {
    data object Onboarding : Screen()
    data object Setup : Screen()
    data object Unlock : Screen()
    data object Vault : Screen()
    data object NewNote : Screen()
    data object NoteDetail : Screen()
    data object Settings : Screen()
    data object Purchases : Screen()
}

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val authController = VaultServiceLocator.vaultAuthController()
    private val vaultService = VaultServiceLocator.vaultService()
    private val panicWipeService = VaultServiceLocator.panicWipeService()
    private val appPreferences = VaultServiceLocator.appPreferences()
    private val adPreferences = VaultServiceLocator.adPreferences()
    private val sharedKeyStore = VaultServiceLocator.sharedKeyStore()
    private val encryptedBackupService = VaultServiceLocator.encryptedBackupService()
    private var inactivityJob: Job? = null
    private var autoSaveEditJob: Job? = null

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
            decoyBiometricEnabled = appPreferences.isDecoyBiometricEnabled(),
            themeMode = ThemeMode.fromId(appPreferences.getThemeMode()),
            wrongAttempts = appPreferences.getWrongAttempts(),
            lockoutUntil = appPreferences.getLockoutUntil(),
            isAdFree = adPreferences.isAdFree(),
            adFreeUntil = adPreferences.getAdFreeUntil(),
            shareCredits = adPreferences.getShareCredits(),
            hasProFeatures = adPreferences.hasProFeaturesLifetime(),
            sharedKeys = sharedKeyStore.listKeys(),
            backupRecords = encryptedBackupService.listBackupRecords(),
            languageTag = appPreferences.getLanguageTag(),
            savedLabels = appPreferences.getCustomLabels(),
            pinScrambleEnabled = appPreferences.isPinScrambleEnabled(),
            hidePinLengthEnabled = appPreferences.isHidePinLengthEnabled()
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
            shareCredits = adPreferences.getShareCredits(),
            hasProFeatures = adPreferences.hasProFeaturesLifetime()
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
        if (adPreferences.hasUnlimitedShares()) {
            uiState.value = uiState.value.copy(hasProFeatures = true)
            return
        }
        adPreferences.addShareCredits(rewardAmount)
        uiState.value = uiState.value.copy(
            shareCredits = adPreferences.getShareCredits()
        )
    }

    fun openPurchases() {
        uiState.value = uiState.value.copy(screen = Screen.Purchases, error = null)
        resetInactivityTimer()
    }

    fun closePurchases() {
        uiState.value = uiState.value.copy(screen = Screen.Settings, error = null)
        resetInactivityTimer()
    }

    fun setBillingReady(ready: Boolean) {
        uiState.value = uiState.value.copy(billingReady = ready)
    }

    fun updateBillingPrice(productId: String, price: String) {
        uiState.value = when (productId) {
            com.androidircx.nulvex.billing.PlayBillingProducts.REMOVE_ADS_ONE_TIME ->
                uiState.value.copy(removeAdsPrice = price)
            com.androidircx.nulvex.billing.PlayBillingProducts.PRO_FEATURES_ONE_TIME ->
                uiState.value.copy(proFeaturesPrice = price)
            else -> uiState.value
        }
    }

    fun grantLifetimeRemoveAds() {
        adPreferences.enableRemoveAdsLifetime()
        uiState.value = uiState.value.copy(
            isAdFree = true,
            adFreeUntil = adPreferences.getAdFreeUntil()
        )
    }

    fun grantLifetimeProFeatures() {
        adPreferences.enableProFeaturesLifetime()
        uiState.value = uiState.value.copy(hasProFeatures = true)
    }

    fun refreshSharedKeys() {
        uiState.value = uiState.value.copy(
            sharedKeys = sharedKeyStore.listKeys(),
            backupRecords = encryptedBackupService.listBackupRecords()
        )
    }

    fun importSharedKey(label: String, source: String, rawKey: String) {
        if (rawKey.isBlank()) {
            uiState.value = uiState.value.copy(error = "Key input is empty")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sharedKeyStore.importKey(label, source, rawKey)
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = null,
                        backupStatus = "Key imported",
                        sharedKeys = sharedKeyStore.listKeys(),
                        backupRecords = encryptedBackupService.listBackupRecords()
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = "Failed to import key: ${e.message ?: "unknown error"}"
                    )
                }
            }
        }
    }

    fun deleteSharedKey(keyId: String) {
        sharedKeyStore.deleteKey(keyId)
        uiState.value = uiState.value.copy(
            sharedKeys = sharedKeyStore.listKeys(),
            backupRecords = encryptedBackupService.listBackupRecords()
        )
    }

    fun uploadEncryptedBackup(keyId: String) {
        if (!uiState.value.hasProFeatures) {
            uiState.value = uiState.value.copy(error = "Pro Features required")
            return
        }
        if (keyId.isBlank()) {
            uiState.value = uiState.value.copy(error = "Choose a key first")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = encryptedBackupService.uploadEncryptedBackup(keyId)
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = null,
                        backupStatus = "Backup uploaded (${result.sizeBytes} bytes)",
                        lastBackupMediaId = result.mediaId,
                        backupRecords = encryptedBackupService.listBackupRecords()
                    )
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = "Backup upload failed"
                    )
                }
            }
        }
    }

    fun restoreEncryptedBackup(
        mediaId: String,
        keyId: String,
        merge: Boolean,
        downloadToken: String? = null,
        downloadExpires: Long? = null
    ) {
        if (!uiState.value.hasProFeatures) {
            uiState.value = uiState.value.copy(error = "Pro Features required")
            return
        }
        if (mediaId.isBlank() || keyId.isBlank()) {
            uiState.value = uiState.value.copy(error = "Backup ID and key are required")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imported = encryptedBackupService.restoreFromRemote(
                    mediaId = mediaId,
                    keyId = keyId,
                    merge = merge,
                    downloadToken = downloadToken,
                    downloadExpires = downloadExpires
                )
                val notes = vaultService.listNotes()
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        notes = notes,
                        error = null,
                        backupStatus = "Restore complete ($imported notes)"
                    )
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = "Backup restore failed"
                    )
                }
            }
        }
    }

    fun restoreEncryptedBackupRecord(recordId: String, merge: Boolean) {
        if (!uiState.value.hasProFeatures) {
            uiState.value = uiState.value.copy(error = "Pro Features required")
            return
        }
        if (recordId.isBlank()) {
            uiState.value = uiState.value.copy(error = "Choose saved backup")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imported = encryptedBackupService.restoreFromStoredRecord(recordId, merge)
                val notes = vaultService.listNotes()
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        notes = notes,
                        error = null,
                        backupStatus = "Restore complete ($imported notes)"
                    )
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = "Backup restore failed"
                    )
                }
            }
        }
    }

    fun deleteBackupRecord(recordId: String) {
        encryptedBackupService.deleteBackupRecord(recordId)
        uiState.value = uiState.value.copy(backupRecords = encryptedBackupService.listBackupRecords())
    }

    suspend fun buildLocalEncryptedBackupPayload(keyId: String): ByteArray {
        return encryptedBackupService.buildEncryptedBackupWrapper(keyId)
    }

    suspend fun restoreLocalEncryptedBackupPayload(payload: ByteArray, keyId: String, merge: Boolean): Int {
        val imported = encryptedBackupService.restoreFromEncryptedBytes(payload, keyId, merge)
        val notes = vaultService.listNotes()
        withContext(Dispatchers.Main) {
            uiState.value = uiState.value.copy(
                notes = notes,
                backupStatus = "Restore complete ($imported notes)"
            )
        }
        return imported
    }

    suspend fun buildLocalEncryptedNoteSharePayload(noteId: String, keyId: String): ByteArray {
        val keyMaterial = sharedKeyStore.getKeyMaterial(keyId) ?: throw IllegalStateException("Key not found")
        keyMaterial.fill(0)
        return encryptedBackupService.buildEncryptedNoteShareWrapper(noteId, keyId)
    }

    fun uploadNoteShare(noteId: String) {
        val keyId = uiState.value.sharedKeys.firstOrNull()?.id
        if (keyId.isNullOrBlank()) {
            uiState.value = uiState.value.copy(error = "Import at least one key in Keys Manager before sharing")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = encryptedBackupService.uploadEncryptedNoteShare(noteId, keyId)
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = null,
                        noteShareUrl = result.url
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = "Note share upload failed: ${e.message?.take(120) ?: "unknown error"}"
                    )
                }
            }
        }
    }

    fun clearNoteShareUrl() {
        uiState.value = uiState.value.copy(noteShareUrl = "")
    }

    fun uploadKeyManagerToApi(encrypted: Boolean, password: String?) {
        if (!uiState.value.hasProFeatures) {
            uiState.value = uiState.value.copy(error = "Pro Features required")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = encryptedBackupService.uploadKeyManagerBackup(encrypted, password?.toCharArray())
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = null,
                        noteShareUrl = result.url
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = "Key manager upload failed: ${e.message?.take(120) ?: "unknown error"}"
                    )
                }
            }
        }
    }

    fun restoreKeyManagerFromApi(mediaId: String, password: String?) {
        if (!uiState.value.hasProFeatures) {
            uiState.value = uiState.value.copy(error = "Pro Features required")
            return
        }
        if (mediaId.isBlank()) {
            uiState.value = uiState.value.copy(error = "Media ID or URL is required")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bytes = encryptedBackupService.downloadKeyManagerBackup(mediaId)
                val imported = sharedKeyStore.importManagerBackup(bytes, password?.toCharArray())
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = null,
                        sharedKeys = sharedKeyStore.listKeys(),
                        backupStatus = "Key manager restored ($imported keys)"
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        error = "Key manager restore failed: ${e.message?.take(120) ?: "unknown error"}"
                    )
                }
            }
        }
    }

    fun exportKeyManagerStorage(encrypted: Boolean, password: String?): ByteArray {
        return sharedKeyStore.exportManagerBackup(encrypted, password?.toCharArray())
    }

    fun importKeyManagerStorage(payload: ByteArray, password: String?): Int {
        val imported = sharedKeyStore.importManagerBackup(payload, password?.toCharArray())
        uiState.value = uiState.value.copy(sharedKeys = sharedKeyStore.listKeys())
        return imported
    }

    fun generateXChaChaKey(label: String) {
        try {
            sharedKeyStore.generateXChaChaKey(label.ifBlank { "XChaCha key" })
            uiState.value = uiState.value.copy(backupStatus = "")
            uiState.value = uiState.value.copy(sharedKeys = sharedKeyStore.listKeys(), backupStatus = "XChaCha key generated")
        } catch (e: Exception) {
            uiState.value = uiState.value.copy(error = "Failed to generate XChaCha key: ${e.message ?: "unknown error"}")
        }
    }

    fun generatePgpKey(label: String) {
        try {
            sharedKeyStore.generatePgpKey(label.ifBlank { "OpenPGP key" })
            uiState.value = uiState.value.copy(backupStatus = "")
            uiState.value = uiState.value.copy(sharedKeys = sharedKeyStore.listKeys(), backupStatus = "OpenPGP key generated")
        } catch (e: Exception) {
            uiState.value = uiState.value.copy(error = "Failed to generate OpenPGP key: ${e.message ?: "unknown error"}")
        }
    }

    fun buildKeyTransferPayload(keyId: String): String? {
        return try {
            sharedKeyStore.buildTransferPayload(keyId)
        } catch (_: Exception) {
            null
        }
    }

    fun buildQrKeyTransferPayload(keyId: String): String? {
        return try {
            sharedKeyStore.buildQrTransferPayload(keyId)
        } catch (_: Exception) {
            null
        }
    }

    fun importSharedKeyPayload(payload: String, source: String): Boolean {
        return try {
            sharedKeyStore.importTransferPayload(payload, source)
            uiState.value = uiState.value.copy(sharedKeys = sharedKeyStore.listKeys(), backupStatus = "Key imported via $source")
            true
        } catch (_: Exception) {
            false
        }
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
                    decoyBiometricEnabled = appPreferences.isDecoyBiometricEnabled(),
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
                    decoyBiometricEnabled = appPreferences.isDecoyBiometricEnabled(),
                    themeMode = ThemeMode.fromId(appPreferences.getThemeMode())
                )
                resetInactivityTimer()
            }
        }
    }

    fun updateNoteText(noteId: String, newText: String, expiresAt: Long?) {
        val note = findNote(noteId) ?: return
        saveEditedNote(noteId, newText, note.labels, emptyList(), expiresAt)
    }

    fun saveEditedNote(
        noteId: String,
        newText: String,
        labels: List<String>,
        newAttachments: List<Uri>,
        expiresAt: Long?
    ) {
        autoSaveEditJob?.cancel()
        uiState.value = uiState.value.copy(pendingNoteEdit = null)
        val note = findNote(noteId) ?: return
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            val sanitizedLabels = labels.map { it.trim() }.filter { it.isNotBlank() }.distinct()
            sanitizedLabels.forEach { appPreferences.addCustomLabel(it) }
            val storedAttachments = vaultService.storeAttachments(noteId, newAttachments)
            val updated = note.copy(
                text = newText,
                labels = sanitizedLabels,
                attachments = note.attachments + storedAttachments,
                expiresAt = expiresAt
            )
            val ok = vaultService.updateNote(updated)
            val notes = vaultService.listNotes()
            withContext(Dispatchers.Main) {
                val selected = notes.firstOrNull { it.id == noteId }
                uiState.value = uiState.value.copy(
                    notes = notes,
                    selectedNote = selected,
                    savedLabels = appPreferences.getCustomLabels(),
                    error = if (ok) null else "Note update failed",
                    isBusy = false
                )
                resetInactivityTimer()
            }
        }
    }

    fun lock() {
        autoSaveEditJob?.cancel()
        viewModelScope.launch {
            runCatching { flushNoteEditDraft() }
            runCatching { flushNewNoteDraft() }
            authController.lock()
            clearInactivityTimer()
            uiState.value = uiState.value.copy(
                screen = Screen.Unlock,
                notes = emptyList(),
                selectedNote = null,
                pendingNoteEdit = null,
                newNoteDraft = null,
                attachmentPreviews = emptyMap()
            )
        }
    }

    fun notifyNoteEditDraft(noteId: String, text: String, expiresAt: Long?) {
        uiState.value = uiState.value.copy(
            pendingNoteEdit = NoteEditDraft(noteId = noteId, text = text, expiresAt = expiresAt)
        )
        autoSaveEditJob?.cancel()
        autoSaveEditJob = viewModelScope.launch {
            delay(1500L)
            runCatching { flushNoteEditDraft() }
        }
    }

    fun clearNoteEditDraft() {
        autoSaveEditJob?.cancel()
        uiState.value = uiState.value.copy(pendingNoteEdit = null)
    }

    fun notifyNewNoteDraft(draft: NewNoteDraft?) {
        uiState.value = uiState.value.copy(newNoteDraft = draft)
    }

    fun setPendingImport(import: PendingImport) {
        uiState.value = uiState.value.copy(pendingImport = import, error = null)
    }

    fun clearPendingImport() {
        uiState.value = uiState.value.copy(pendingImport = null)
    }

    fun importIncomingFile(bytes: ByteArray, mimeType: String, keyId: String, merge: Boolean) {
        if (keyId.isBlank()) {
            uiState.value = uiState.value.copy(error = "Choose a key first")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imported = encryptedBackupService.restoreFromEncryptedBytes(bytes, keyId, merge)
                val notes = vaultService.listNotes()
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        notes = notes,
                        pendingImport = null,
                        error = null,
                        backupStatus = when (mimeType) {
                            com.androidircx.nulvex.pro.NulvexFileTypes.NOTE_SHARE_MIME -> "Note imported"
                            else -> "Backup restored ($imported notes)"
                        }
                    )
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(isBusy = false, error = "Import failed — wrong key or corrupted file")
                }
            }
        }
    }

    fun importIncomingRemote(mediaId: String, keyId: String, merge: Boolean) {
        if (keyId.isBlank()) {
            uiState.value = uiState.value.copy(error = "Choose a key first")
            return
        }
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imported = encryptedBackupService.restoreFromRemote(mediaId, keyId, merge)
                val notes = vaultService.listNotes()
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        notes = notes,
                        pendingImport = null,
                        error = null,
                        backupStatus = "Remote import complete ($imported notes)"
                    )
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(isBusy = false, error = "Remote import failed")
                }
            }
        }
    }

    fun importIncomingKeyManager(bytes: ByteArray, password: String?) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val imported = sharedKeyStore.importManagerBackup(bytes, password?.toCharArray())
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        isBusy = false,
                        pendingImport = null,
                        sharedKeys = sharedKeyStore.listKeys(),
                        error = null,
                        backupStatus = "Imported $imported keys"
                    )
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(isBusy = false, error = "Key import failed — wrong password or corrupted file")
                }
            }
        }
    }

    private suspend fun flushNoteEditDraft() {
        val draft = uiState.value.pendingNoteEdit ?: return
        val note = findNote(draft.noteId) ?: return
        if (note.text == draft.text && note.expiresAt == draft.expiresAt) {
            uiState.value = uiState.value.copy(pendingNoteEdit = null)
            return
        }
        val updated = note.copy(text = draft.text, expiresAt = draft.expiresAt)
        withContext(Dispatchers.IO) { vaultService.updateNote(updated) }
        uiState.value = uiState.value.copy(
            selectedNote = if (uiState.value.selectedNote?.id == draft.noteId) updated else uiState.value.selectedNote,
            pendingNoteEdit = null
        )
    }

    private suspend fun flushNewNoteDraft() {
        if (uiState.value.screen != Screen.NewNote) return
        val draft = uiState.value.newNoteDraft ?: return
        val hasContent = draft.text.isNotBlank() || draft.checklist.any { it.text.isNotBlank() }
        if (!hasContent) return
        withContext(Dispatchers.IO) {
            val noteId = java.util.UUID.randomUUID().toString()
            vaultService.createNote(
                id = noteId,
                text = draft.text,
                checklist = draft.checklist,
                labels = draft.labels,
                attachments = emptyList(),
                pinned = draft.pinned,
                expiresAt = draft.expiresAt,
                readOnce = draft.readOnce
            )
        }
        uiState.value = uiState.value.copy(newNoteDraft = null)
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
            // Decoy biometric is invalidated when the decoy vault is wiped/changed.
            appPreferences.setDecoyBiometricEnabled(false)
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    isBusy = false,
                    error = null,
                    isDecoyEnabled = authController.isDecoyEnabled(),
                    decoyBiometricEnabled = false
                )
            }
        }
    }

    fun disableDecoyPin() {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            panicWipeService.wipeDecoyOnly()
            authController.clearDecoyPin()
            appPreferences.setDecoyBiometricEnabled(false)
            withContext(Dispatchers.Main) {
                uiState.value = uiState.value.copy(
                    isBusy = false,
                    error = null,
                    isDecoyEnabled = authController.isDecoyEnabled(),
                    decoyBiometricEnabled = false
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

    fun updateLanguage(languageTag: String) {
        appPreferences.setLanguageTag(languageTag)
        uiState.value = uiState.value.copy(languageTag = languageTag)
    }

    fun enableBiometric() {
        appPreferences.setBiometricEnabled(true)
        uiState.value = uiState.value.copy(biometricEnabled = true)
    }

    fun disableBiometric() {
        appPreferences.setBiometricEnabled(false)
        uiState.value = uiState.value.copy(biometricEnabled = false)
    }

    fun setPinScramble(enabled: Boolean) {
        appPreferences.setPinScrambleEnabled(enabled)
        uiState.value = uiState.value.copy(pinScrambleEnabled = enabled)
    }

    fun setHidePinLength(enabled: Boolean) {
        appPreferences.setHidePinLengthEnabled(enabled)
        uiState.value = uiState.value.copy(hidePinLengthEnabled = enabled)
    }

    fun enableDecoyBiometric() {
        appPreferences.setDecoyBiometricEnabled(true)
        uiState.value = uiState.value.copy(decoyBiometricEnabled = true)
    }

    fun disableDecoyBiometric() {
        appPreferences.setDecoyBiometricEnabled(false)
        uiState.value = uiState.value.copy(decoyBiometricEnabled = false)
    }

    fun unlockWithDecoyMasterKey(masterKey: ByteArray) {
        setBusy(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                vaultService.unlockWithMasterKey(masterKey, VaultProfile.DECOY)
                val notes = vaultService.listNotes()
                withContext(Dispatchers.Main) {
                    uiState.value = uiState.value.copy(
                        screen = Screen.Vault,
                        lastProfile = VaultProfile.DECOY,
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
            } finally {
                masterKey.fill(0)
            }
        }
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
                    newNoteDraft = null,
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
        val saved = appPreferences.addCustomLabel(trimmed)
        uiState.value = uiState.value.copy(savedLabels = saved)
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

    fun createStandaloneLabel(label: String) {
        val trimmed = label.trim()
        if (trimmed.isBlank()) return
        val saved = appPreferences.addCustomLabel(trimmed)
        uiState.value = uiState.value.copy(savedLabels = saved)
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

    fun setBackupStatus(message: String) {
        uiState.value = uiState.value.copy(backupStatus = message)
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
                    decoyBiometricEnabled = appPreferences.isDecoyBiometricEnabled(),
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
                    decoyBiometricEnabled = appPreferences.isDecoyBiometricEnabled(),
                    themeMode = ThemeMode.fromId(appPreferences.getThemeMode()),
                    savedLabels = appPreferences.getCustomLabels(),
                    pendingNoteEdit = null,
                    newNoteDraft = null,
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
            Screen.Onboarding, Screen.Setup, Screen.Unlock, Screen.Settings, Screen.Purchases -> false
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
