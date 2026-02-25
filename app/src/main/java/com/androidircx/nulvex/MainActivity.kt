package com.androidircx.nulvex

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.androidircx.nulvex.ui.MainScreen
import com.androidircx.nulvex.ui.MainViewModel
import com.androidircx.nulvex.ui.theme.NULVEXTheme
import com.androidircx.nulvex.security.BiometricKeyStore
import com.androidircx.nulvex.security.VaultKeyManager
import com.androidircx.nulvex.security.VaultProfile
import com.androidircx.nulvex.ads.AdManager
import com.androidircx.nulvex.VaultServiceLocator
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.androidircx.nulvex.billing.PlayBillingFactory
import com.androidircx.nulvex.billing.PlayBillingCoordinator
import com.androidircx.nulvex.billing.PlayBillingGateway
import com.androidircx.nulvex.billing.PlayBillingProducts
import com.androidircx.nulvex.billing.PlayBillingStateSink
import com.androidircx.nulvex.billing.BillingProductInfo
import com.androidircx.nulvex.billing.BillingPurchaseInfo
import com.androidircx.nulvex.billing.BillingPurchaseState
import com.androidircx.nulvex.billing.PurchaseUpdateResult
import com.androidircx.nulvex.billing.PurchaseUpdateStatus
import com.androidircx.nulvex.i18n.sanitizeLanguageTag
import com.androidircx.nulvex.reminder.ReminderConstants
import com.androidircx.nulvex.i18n.tx
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : AppCompatActivity() {
    private val vm: MainViewModel by viewModels()
    private val biometricStore by lazy { BiometricKeyStore(applicationContext) }
    private val decoyBiometricStore by lazy {
        BiometricKeyStore(applicationContext, "nulvex_biometric_decoy", "nulvex_biometric_decoy_key")
    }
    private val adManager by lazy { VaultServiceLocator.adManager() }
    private var billingClient: BillingClient? = null
    private var nfcAdapter: NfcAdapter? = null
    private val productDetailsById = mutableMapOf<String, ProductDetails>()
    private lateinit var billingCoordinator: PlayBillingCoordinator
    private val codeScanner by lazy { GmsBarcodeScanning.getClient(this) }
    private var pendingLocalBackupExportKeyId: String? = null
    private var pendingLocalBackupImportKeyId: String? = null
    private var pendingLocalBackupImportMerge: Boolean = true
    private var pendingKeyManagerExportEncrypted: Boolean = true
    private var pendingKeyManagerExportPassword: String? = null
    private var pendingKeyManagerImportPassword: String? = null
    private var pendingNfcSharePayload: String? = null

    private val exportLocalBackupLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")) { uri ->
            val keyId = pendingLocalBackupExportKeyId
            if (uri == null || keyId.isNullOrBlank()) return@registerForActivityResult
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val bytes = vm.buildLocalEncryptedBackupPayload(keyId)
                    contentResolver.openOutputStream(uri)?.use { out -> out.write(bytes) }
                    withContext(Dispatchers.Main) { vm.setBackupStatus("Local encrypted backup exported") }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) { vm.showError("Failed to export local backup") }
                }
            }
        }

    private val importLocalBackupLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            val keyId = pendingLocalBackupImportKeyId
            if (uri == null || keyId.isNullOrBlank()) return@registerForActivityResult
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        ?: throw IllegalStateException("Unable to read file")
                    vm.restoreLocalEncryptedBackupPayload(bytes, keyId, pendingLocalBackupImportMerge)
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) { vm.showError("Failed to import local backup") }
                }
            }
        }

    private val exportKeyManagerLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
            if (uri == null) return@registerForActivityResult
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val bytes = vm.exportKeyManagerStorage(
                        encrypted = pendingKeyManagerExportEncrypted,
                        password = pendingKeyManagerExportPassword
                    )
                    contentResolver.openOutputStream(uri)?.use { out -> out.write(bytes) }
                    withContext(Dispatchers.Main) { vm.setBackupStatus("Key manager exported") }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) { vm.showError("Failed to export key manager") }
                }
            }
        }

    private val importKeyManagerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) return@registerForActivityResult
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        ?: throw IllegalStateException("Unable to read file")
                    val imported = vm.importKeyManagerStorage(bytes, pendingKeyManagerImportPassword)
                    withContext(Dispatchers.Main) { vm.setBackupStatus("Imported $imported keys") }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) { vm.showError("Failed to import key manager") }
                }
            }
        }
    private val purchasesListener = PurchasesUpdatedListener { billingResult, purchases ->
        billingCoordinator.onPurchasesUpdated(
            PurchaseUpdateResult(
                status = when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> PurchaseUpdateStatus.OK
                    BillingClient.BillingResponseCode.USER_CANCELED -> PurchaseUpdateStatus.USER_CANCELED
                    else -> PurchaseUpdateStatus.ERROR
                },
                debugMessage = billingResult.debugMessage,
                purchases = purchases.orEmpty().map { it.toPurchaseInfo() }
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_SECURE,
            android.view.WindowManager.LayoutParams.FLAG_SECURE
        )
        enableEdgeToEdge()
        setContent {
            val state = vm.uiState.value
            NULVEXTheme(themeMode = state.themeMode) {
                MainScreen(
                    state = state,
                    onCompleteOnboarding = vm::completeOnboarding,
                    onSetup = { realPin, decoyPin, enableBiometric ->
                        vm.setupPins(realPin, decoyPin) {
                            if (enableBiometric) {
                                startBiometricEnrollment(realPin)
                            }
                        }
                    },
                    onUnlock = vm::unlock,
                    onLock = vm::lock,
                    onPanic = vm::panicWipe,
                    onOpenSettings = vm::openSettings,
                    onCloseSettings = vm::closeSettings,
                    onUpdateDecoyPin = vm::updateDecoyPin,
                    onDisableDecoy = vm::disableDecoyPin,
                    onUpdateLockTimeout = vm::updateLockTimeout,
                    onUpdateDefaultExpiry = vm::updateDefaultExpiry,
                    onUpdateDefaultReadOnce = vm::updateDefaultReadOnce,
                    onRequestBiometricEnroll = ::startBiometricEnrollment,
                    onRequestBiometricUnlock = ::startBiometricUnlock,
                    onDisableBiometric = ::disableBiometric,
                    onRequestDecoyBiometricEnroll = ::startDecoyBiometricEnrollment,
                    onRequestDecoyBiometricUnlock = ::startDecoyBiometricUnlock,
                    onDisableDecoyBiometric = ::disableDecoyBiometric,
                    onTogglePinScramble = vm::setPinScramble,
                    onToggleHidePinLength = vm::setHidePinLength,
                    onChangeRealPin = vm::changeRealPin,
                    onUpdateThemeMode = vm::updateThemeMode,
                    onUpdateLanguage = ::updateLanguage,
                    onOpenNew = vm::openNewNote,
                    onCreate = vm::createNote,
                    onOpenNote = vm::openNote,
                    onCloseNote = vm::closeNoteDetail,
                    onUpdateNoteText = vm::updateNoteText,
                    onSaveEditedNote = vm::saveEditedNote,
                    onShareNote = ::shareNoteFile,
                    onDelete = vm::deleteNote,
                    onTogglePinned = vm::togglePinned,
                    onToggleChecklistItem = vm::toggleChecklistItem,
                    onAddChecklistItem = vm::addChecklistItem,
                    onRemoveChecklistItem = vm::removeChecklistItem,
                    onUpdateChecklistText = vm::updateChecklistText,
                    onMoveChecklistItem = vm::moveChecklistItem,
                    onAddLabel = vm::addLabel,
                    onRemoveLabel = vm::removeLabel,
                    onCreateStandaloneLabel = vm::createStandaloneLabel,
                    onSearchQueryChange = vm::updateSearchQuery,
                    onSelectLabel = vm::updateActiveLabel,
                    onSetShowArchived = vm::setShowArchived,
                    onLoadAttachmentPreview = vm::loadAttachmentPreview,
                    onRemoveAttachment = vm::removeAttachment,
                    onToggleArchived = vm::toggleArchived,
                    onSetNoteReminder = vm::setNoteReminder,
                    onClearNoteReminder = vm::clearNoteReminder,
                    onClearError = vm::clearError,
                    onWatchAdToRemoveAds = {
                        adManager.showRewardedNoAds(this) { amount ->
                            vm.grantAdFree(amount)
                        }
                    },
                    onWatchAdForShares = {
                        adManager.showRewardedShare(this) { amount ->
                            vm.grantShareCredits(amount)
                            // TODO: when Laravel backend is ready, also POST credits to API
                        }
                    },
                    onOpenPurchases = vm::openPurchases,
                    onClosePurchases = vm::closePurchases,
                    onBuyRemoveAds = { billingCoordinator.buy(PlayBillingProducts.REMOVE_ADS_ONE_TIME) },
                    onBuyProFeatures = { billingCoordinator.buy(PlayBillingProducts.PRO_FEATURES_ONE_TIME) },
                    onRestorePurchases = { billingCoordinator.restorePurchases() },
                    onImportSharedKey = vm::importSharedKey,
                    onDeleteSharedKey = vm::deleteSharedKey,
                    onUploadBackup = vm::uploadEncryptedBackup,
                    onRestoreBackup = vm::restoreEncryptedBackup,
                    onRestoreSavedBackup = vm::restoreEncryptedBackupRecord,
                    onDeleteSavedBackup = vm::deleteBackupRecord,
                    onScanQrKey = ::scanQrKey,
                    onExportLocalBackup = ::exportLocalBackupFile,
                    onImportLocalBackup = ::importLocalBackupFile,
                    onExportKeyManager = ::exportKeyManagerFile,
                    onImportKeyManager = ::importKeyManagerFile,
                    onUploadKeyManagerToApi = vm::uploadKeyManagerToApi,
                    onRestoreKeyManagerFromApi = vm::restoreKeyManagerFromApi,
                    onGenerateXChaChaKey = vm::generateXChaChaKey,
                    onGeneratePgpKey = vm::generatePgpKey,
                    onBuildKeyTransferPayload = vm::buildKeyTransferPayload,
                    onBuildQrKeyTransferPayload = vm::buildQrKeyTransferPayload,
                    onStartNfcKeyShare = ::startNfcKeyShare,
                    onNoteEditDraftChanged = vm::notifyNoteEditDraft,
                    onClearNoteEditDraft = vm::clearNoteEditDraft,
                    onUndoNoteEdit = vm::requestUndoNoteEdit,
                    onRedoNoteEdit = vm::requestRedoNoteEdit,
                    onNewNoteDraftChanged = vm::notifyNewNoteDraft,
                    onImportIncomingFile = vm::importIncomingFile,
                    onImportIncomingKeyManager = vm::importIncomingKeyManager,
                    onImportIncomingRemote = vm::importIncomingRemote,
                    onImportIncomingRemoteKeyManager = vm::importIncomingRemoteKeyManager,
                    onClearPendingImport = vm::clearPendingImport,
                    onClearNoteShareUrl = vm::clearNoteShareUrl
                )
            }
        }
        billingCoordinator = PlayBillingCoordinator(billingGateway, billingStateSink)
        initBilling()
        handleIncomingIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        enableNfcForegroundDispatch()
        vm.refreshAdFreeState()
        vm.refreshSharedKeys()
    }

    override fun onPause() {
        disableNfcForegroundDispatch()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        vm.onAppBackgrounded()
    }

    override fun onDestroy() {
        billingClient?.endConnection()
        billingClient = null
        super.onDestroy()
    }

    override fun dispatchTouchEvent(ev: android.view.MotionEvent?): Boolean {
        vm.onUserInteraction()
        return super.dispatchTouchEvent(ev)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNfcIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun startBiometricEnrollment(pin: String) {
        if (pin.isBlank()) {
            vm.showError("PIN is required for fingerprint setup")
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val ok = VaultServiceLocator.vaultAuthService()
                .verifyRealPin(pin.toCharArray())
            withContext(Dispatchers.Main) {
                if (!ok) {
                    vm.showError("Current PIN is incorrect")
                    return@withContext
                }
                val canAuth = BiometricManager.from(this@MainActivity).canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
                    vm.showError("Fingerprint or device credential is not available")
                    return@withContext
                }
                val cipher = try {
                    biometricStore.getEncryptCipher()
                } catch (_: Exception) {
                    vm.showError("Unable to initialize fingerprint")
                    return@withContext
                }
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(tx("Enable fingerprint"))
                    .setSubtitle(tx("Authenticate to enable biometric unlock"))
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    .build()
                val executor = ContextCompat.getMainExecutor(this@MainActivity)
                val prompt = BiometricPrompt(
                    this@MainActivity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            val authCipher = result.cryptoObject?.cipher ?: run {
                                vm.showError("Fingerprint setup failed")
                                return
                            }
                            val masterKey = VaultKeyManager(applicationContext)
                                .deriveMasterKey(pin.toCharArray())
                            biometricStore.storeEncryptedMasterKey(authCipher, masterKey)
                            masterKey.fill(0)
                            vm.enableBiometric()
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            vm.showError(errString.toString())
                        }
                    }
                )
                prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
        }
    }

    private fun startBiometricUnlock() {
        if (!biometricStore.hasEncryptedKey()) {
            vm.showError("Fingerprint is not set up")
            return
        }
        val pair = biometricStore.getEncryptedMasterKey()
        if (pair == null) {
            vm.showError("Fingerprint data is missing")
            return
        }
        val (iv, ct) = pair
        val cipher = try {
            biometricStore.getDecryptCipher(iv)
        } catch (_: Exception) {
            vm.showError("Fingerprint data is invalid")
            return
        }
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(tx("Unlock"))
            .setSubtitle(tx("Authenticate to unlock your vault"))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val authCipher = result.cryptoObject?.cipher ?: run {
                    vm.showError("Fingerprint unlock failed")
                    return
                }
                val masterKey = authCipher.doFinal(ct)
                vm.unlockWithMasterKey(masterKey)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                vm.showError(errString.toString())
            }
        })
        prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun disableBiometric() {
        biometricStore.clear()
        vm.disableBiometric()
    }

    private fun startDecoyBiometricEnrollment(decoyPin: String) {
        if (decoyPin.isBlank()) {
            vm.showError("Enter your decoy PIN first")
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val profile = VaultServiceLocator.vaultAuthService().resolveProfile(decoyPin.toCharArray())
            withContext(Dispatchers.Main) {
                if (profile != VaultProfile.DECOY) {
                    vm.showError("Incorrect decoy PIN")
                    return@withContext
                }
                val canAuth = BiometricManager.from(this@MainActivity).canAuthenticate(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
                    vm.showError("Fingerprint or device credential is not available")
                    return@withContext
                }
                val cipher = try {
                    decoyBiometricStore.getEncryptCipher()
                } catch (_: Exception) {
                    vm.showError("Unable to initialize fingerprint for decoy")
                    return@withContext
                }
                val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(tx("Enable decoy fingerprint"))
                    .setSubtitle(tx("Authenticate to enable biometric unlock for decoy vault"))
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    .build()
                val executor = ContextCompat.getMainExecutor(this@MainActivity)
                val prompt = BiometricPrompt(
                    this@MainActivity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            val authCipher = result.cryptoObject?.cipher ?: run {
                                vm.showError("Decoy fingerprint setup failed")
                                return
                            }
                            val masterKey = VaultKeyManager(applicationContext, VaultProfile.DECOY)
                                .deriveMasterKey(decoyPin.toCharArray())
                            decoyBiometricStore.storeEncryptedMasterKey(authCipher, masterKey)
                            masterKey.fill(0)
                            vm.enableDecoyBiometric()
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            vm.showError(errString.toString())
                        }
                    }
                )
                prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
        }
    }

    private fun startDecoyBiometricUnlock() {
        if (!decoyBiometricStore.hasEncryptedKey()) {
            vm.showError("Decoy fingerprint is not set up")
            return
        }
        val pair = decoyBiometricStore.getEncryptedMasterKey()
        if (pair == null) {
            vm.showError("Decoy fingerprint data is missing")
            return
        }
        val (iv, ct) = pair
        val cipher = try {
            decoyBiometricStore.getDecryptCipher(iv)
        } catch (_: Exception) {
            vm.showError("Decoy fingerprint data is invalid")
            return
        }
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(tx("Unlock"))
            .setSubtitle(tx("Authenticate to unlock your vault"))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()
        val executor = ContextCompat.getMainExecutor(this)
        val prompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                val authCipher = result.cryptoObject?.cipher ?: run {
                    vm.showError("Decoy fingerprint unlock failed")
                    return
                }
                val masterKey = authCipher.doFinal(ct)
                vm.unlockWithDecoyMasterKey(masterKey)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                vm.showError(errString.toString())
            }
        })
        prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }

    private fun disableDecoyBiometric() {
        decoyBiometricStore.clear()
        vm.disableDecoyBiometric()
    }

    private fun updateLanguage(languageTag: String) {
        vm.updateLanguage(languageTag)
        val locales = if (languageTag == "system") {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(sanitizeLanguageTag(languageTag))
        }
        AppCompatDelegate.setApplicationLocales(locales)
        recreate()
    }

    private fun scanQrKey() {
        codeScanner.startScan()
            .addOnSuccessListener { barcode ->
                val raw = barcode.rawValue?.trim().orEmpty()
                if (raw.isBlank()) {
                    vm.showError("QR code is empty")
                    return@addOnSuccessListener
                }
                val importedPayload = vm.importSharedKeyPayload(raw, "qr")
                if (!importedPayload) {
                    vm.importSharedKey("QR imported key", "qr", raw)
                }
            }
            .addOnFailureListener {
                vm.showError("QR scan failed")
            }
    }

    private fun exportLocalBackupFile(keyId: String) {
        pendingLocalBackupExportKeyId = keyId
        exportLocalBackupLauncher.launch(
            "nulvex_backup_${System.currentTimeMillis()}${com.androidircx.nulvex.pro.NulvexFileTypes.BACKUP_EXT}"
        )
    }

    private fun importLocalBackupFile(keyId: String, merge: Boolean) {
        pendingLocalBackupImportKeyId = keyId
        pendingLocalBackupImportMerge = merge
        importLocalBackupLauncher.launch(arrayOf("application/octet-stream", "application/json", "*/*"))
    }

    private fun exportKeyManagerFile(encrypted: Boolean, password: String?) {
        pendingKeyManagerExportEncrypted = encrypted
        pendingKeyManagerExportPassword = password
        exportKeyManagerLauncher.launch(
            "nulvex_keys_${System.currentTimeMillis()}${com.androidircx.nulvex.pro.NulvexFileTypes.KEY_MANAGER_EXT}"
        )
    }

    private fun importKeyManagerFile(password: String?) {
        pendingKeyManagerImportPassword = password
        importKeyManagerLauncher.launch(arrayOf("application/json", "application/octet-stream", "*/*"))
    }

    private fun shareNoteFile(noteId: String) {
        val state = vm.uiState.value
        if (state.hasProFeatures) {
            vm.uploadNoteShare(noteId)
            return
        }
        val keyId = state.sharedKeys.firstOrNull()?.id
        if (keyId.isNullOrBlank()) {
            vm.showError("Import at least one key in Keys Manager before sharing")
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val payload = vm.buildLocalEncryptedNoteSharePayload(noteId, keyId)
                val outFile = File(cacheDir, "note_${System.currentTimeMillis()}${com.androidircx.nulvex.pro.NulvexFileTypes.NOTE_SHARE_EXT}")
                outFile.outputStream().use { it.write(payload) }
                val uri = FileProvider.getUriForFile(this@MainActivity, "${packageName}.fileprovider", outFile)
                withContext(Dispatchers.Main) {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = com.androidircx.nulvex.pro.NulvexFileTypes.NOTE_SHARE_MIME
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(send, tx("Share encrypted Nulvex note")))
                    vm.setBackupStatus("Encrypted note file ready for share")
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) {
                    vm.showError("Failed to share encrypted note file")
                }
            }
        }
    }

    private fun startNfcKeyShare(payload: String) {
        pendingNfcSharePayload = payload
        vm.setBackupStatus("NFC share armed. Touch another NFC tag to write the key.")
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val reminderNoteId = intent?.getStringExtra(ReminderConstants.EXTRA_NOTE_ID)?.trim().orEmpty()
        val reminderAction = intent?.getStringExtra(ReminderConstants.EXTRA_ACTION)?.trim().orEmpty()
        if (reminderNoteId.isNotBlank() && reminderAction.isNotBlank()) {
            vm.handleReminderAction(reminderAction, reminderNoteId)
            return
        }
        if (intent?.action != Intent.ACTION_VIEW) return
        val data = intent.data ?: return
        val scheme = data.scheme ?: return
        when {
            scheme == "https" && data.host == "androidircx.com" -> {
                val path = data.path ?: return
                val mediaId = path.substringAfterLast("/").takeIf { it.isNotBlank() } ?: return
                val typeHint = data.getQueryParameter("t")
                val mime = when (typeHint) {
                    "keys" -> com.androidircx.nulvex.pro.NulvexFileTypes.KEY_MANAGER_MIME
                    else -> null
                }
                vm.setPendingImport(com.androidircx.nulvex.ui.PendingImport.RemoteMedia(mediaId, mime))
            }
            scheme == "content" || scheme == "file" -> {
                val mimeType = intent.type?.takeIf { it.isNotBlank() }
                    ?: contentResolver.getType(data)?.takeIf { it.isNotBlank() }
                    ?: determineMimeFromUri(data)
                    ?: return
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val bytes = contentResolver.openInputStream(data)?.use { it.readBytes() }
                            ?: return@launch
                        withContext(Dispatchers.Main) {
                            vm.setPendingImport(com.androidircx.nulvex.ui.PendingImport.LocalFile(bytes, mimeType))
                        }
                    } catch (_: Exception) {
                        withContext(Dispatchers.Main) { vm.showError("Failed to read incoming file") }
                    }
                }
            }
        }
    }

    private fun determineMimeFromUri(uri: android.net.Uri): String? {
        val path = uri.lastPathSegment ?: uri.path ?: return null
        return when {
            path.endsWith(".nulvex", ignoreCase = true) -> com.androidircx.nulvex.pro.NulvexFileTypes.NOTE_SHARE_MIME
            path.endsWith(".nulvxbk", ignoreCase = true) -> com.androidircx.nulvex.pro.NulvexFileTypes.BACKUP_MIME
            path.endsWith(".nulvxkeys", ignoreCase = true) -> com.androidircx.nulvex.pro.NulvexFileTypes.KEY_MANAGER_MIME
            else -> null
        }
    }

    private fun enableNfcForegroundDispatch() {
        val adapter = nfcAdapter ?: return
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")
            } catch (_: IntentFilter.MalformedMimeTypeException) {
                // Ignore and continue with broad dispatch.
            }
        })
        adapter.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    private fun disableNfcForegroundDispatch() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    private fun handleNfcIntent(intent: Intent) {
        val pendingShare = pendingNfcSharePayload
        if (!pendingShare.isNullOrBlank()) {
            val wrote = writePayloadToNfcTag(intent, pendingShare)
            if (wrote) {
                pendingNfcSharePayload = null
                vm.setBackupStatus("Key payload written to NFC tag")
            }
            return
        }
        val action = intent.action ?: return
        if (action != NfcAdapter.ACTION_NDEF_DISCOVERED &&
            action != NfcAdapter.ACTION_TECH_DISCOVERED &&
            action != NfcAdapter.ACTION_TAG_DISCOVERED
        ) {
            return
        }
        val raw = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) ?: return
        val messages = raw.mapNotNull { it as? NdefMessage }
        for (message in messages) {
            for (record in message.records) {
                val text = parseNdefText(record) ?: continue
                if (text.isNotBlank()) {
                    vm.importSharedKey("NFC imported key", "nfc", text)
                    return
                }
            }
        }
    }

    private fun writePayloadToNfcTag(intent: Intent, payload: String): Boolean {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return false
        val message = NdefMessage(arrayOf(createTextRecord(payload)))
        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) {
                    ndef.close()
                    vm.showError("NFC tag is read-only")
                    return false
                }
                if (ndef.maxSize < message.toByteArray().size) {
                    ndef.close()
                    vm.showError("NFC tag capacity is too small")
                    return false
                }
                ndef.writeNdefMessage(message)
                ndef.close()
                true
            } else {
                val formatable = NdefFormatable.get(tag) ?: run {
                    vm.showError("Tag does not support NDEF")
                    return false
                }
                formatable.connect()
                formatable.format(message)
                formatable.close()
                true
            }
        } catch (_: Exception) {
            vm.showError("Failed to write NFC tag")
            false
        }
    }

    private fun createTextRecord(text: String): NdefRecord {
        val lang = "en".toByteArray(Charsets.US_ASCII)
        val txt = text.toByteArray(Charsets.UTF_8)
        val payload = ByteArray(1 + lang.size + txt.size)
        payload[0] = lang.size.toByte()
        System.arraycopy(lang, 0, payload, 1, lang.size)
        System.arraycopy(txt, 0, payload, 1 + lang.size, txt.size)
        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
    }

    private fun parseNdefText(record: NdefRecord): String? {
        if (record.tnf != NdefRecord.TNF_WELL_KNOWN) return null
        if (!record.type.contentEquals(NdefRecord.RTD_TEXT)) return null
        val payload = record.payload ?: return null
        if (payload.isEmpty()) return null
        val status = payload[0].toInt()
        val isUtf16 = (status and 0x80) != 0
        val langLength = status and 0x3F
        if (payload.size <= langLength + 1) return null
        val encoding = if (isUtf16) Charsets.UTF_16 else Charsets.UTF_8
        return payload.copyOfRange(langLength + 1, payload.size).toString(encoding).trim()
    }

    private val billingStateSink = object : PlayBillingStateSink {
        override fun setBillingReady(ready: Boolean) = vm.setBillingReady(ready)
        override fun updateBillingPrice(productId: String, price: String) = vm.updateBillingPrice(productId, price)
        override fun showError(message: String) = vm.showError(message)
        override fun grantLifetimeRemoveAds() = vm.grantLifetimeRemoveAds()
        override fun grantLifetimeProFeatures() = vm.grantLifetimeProFeatures()
        override fun refreshAdFreeState() = vm.refreshAdFreeState()
    }

    private val billingGateway = object : PlayBillingGateway {
        override fun queryProductDetails(callback: (ok: Boolean, products: List<BillingProductInfo>) -> Unit) {
            val client = billingClient ?: run {
                callback(false, emptyList())
                return
            }
            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(PlayBillingProducts.asQueryProducts())
                .build()
            client.queryProductDetailsAsync(params) { billingResult, productDetailsResult ->
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    callback(false, emptyList())
                    return@queryProductDetailsAsync
                }
                productDetailsById.clear()
                val mapped = productDetailsResult.productDetailsList.map { details ->
                    productDetailsById[details.productId] = details
                    // oneTimePurchaseOfferDetailsList (plural) is required for billing library 7+
                    // one-time products with purchase options. The singular oneTimePurchaseOfferDetails
                    // is deprecated and returns null for products created with the new purchase options model.
                    val offer = details.oneTimePurchaseOfferDetailsList?.firstOrNull()
                    BillingProductInfo(
                        productId = details.productId,
                        formattedPrice = offer?.formattedPrice ?: "Unavailable",
                        offerToken = offer?.offerToken
                    )
                }
                callback(true, mapped)
            }
        }

        override fun queryPurchases(callback: (ok: Boolean, purchases: List<BillingPurchaseInfo>) -> Unit) {
            val client = billingClient ?: run {
                callback(false, emptyList())
                return
            }
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
            client.queryPurchasesAsync(params) { billingResult, purchases ->
                if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                    callback(false, emptyList())
                    return@queryPurchasesAsync
                }
                callback(true, purchases.map { it.toPurchaseInfo() })
            }
        }

        override fun launchPurchase(productId: String, offerToken: String): Pair<Boolean, String> {
            val client = billingClient ?: return false to "Google Play Billing is not ready"
            val details = productDetailsById[productId] ?: return false to "Missing product details"
            val productParamsBuilder = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
            // setOfferToken is required for INAPP products with purchase options (billing library 7+)
            // and for subscriptions. Skip only for legacy INAPP products that have no offer token.
            if (offerToken.isNotBlank()) {
                productParamsBuilder.setOfferToken(offerToken)
            }
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productParamsBuilder.build()))
                .build()
            val result = client.launchBillingFlow(this@MainActivity, flowParams)
            return (result.responseCode == BillingClient.BillingResponseCode.OK) to result.debugMessage
        }

        override fun acknowledgePurchase(purchaseToken: String, callback: (ok: Boolean) -> Unit) {
            val client = billingClient ?: run {
                callback(false)
                return
            }
            val acknowledgeParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()
            client.acknowledgePurchase(acknowledgeParams) { ackResult ->
                callback(ackResult.responseCode == BillingClient.BillingResponseCode.OK)
            }
        }
    }

    private fun initBilling() {
        billingClient = PlayBillingFactory.createClient(this, purchasesListener).also { client ->
            client.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    billingCoordinator.onBillingDisconnected()
                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    billingCoordinator.onBillingSetupFinished(
                        billingResult.responseCode == BillingClient.BillingResponseCode.OK
                    )
                }
            })
        }
    }

    private fun Purchase.toPurchaseInfo(): BillingPurchaseInfo {
        val state = when (purchaseState) {
            Purchase.PurchaseState.PURCHASED -> BillingPurchaseState.PURCHASED
            Purchase.PurchaseState.PENDING -> BillingPurchaseState.PENDING
            else -> BillingPurchaseState.UNSPECIFIED
        }
        return BillingPurchaseInfo(
            products = products,
            state = state,
            isAcknowledged = isAcknowledged,
            purchaseToken = purchaseToken
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NULVEXTheme {
        MainScreen(
            state = com.androidircx.nulvex.ui.UiState(screen = com.androidircx.nulvex.ui.Screen.Setup),
            onCompleteOnboarding = {},
            onSetup = { _, _, _ -> },
            onUnlock = {},
            onLock = {},
            onPanic = {},
            onOpenSettings = {},
            onCloseSettings = {},
            onUpdateDecoyPin = { _, _ -> },
            onDisableDecoy = {},
            onUpdateLockTimeout = {},
            onUpdateDefaultExpiry = {},
            onUpdateDefaultReadOnce = {},
            onRequestBiometricEnroll = {},
            onRequestBiometricUnlock = {},
            onDisableBiometric = {},
            onChangeRealPin = { _, _, _ -> },
            onUpdateThemeMode = {},
            onOpenNew = {},
            onCreate = { _, _, _, _, _, _, _, _ -> },
            onOpenNote = {},
            onCloseNote = {},
            onUpdateNoteText = { _, _, _ -> },
            onShareNote = {},
            onDelete = {},
            onTogglePinned = {},
            onToggleChecklistItem = { _, _ -> },
            onAddChecklistItem = { _, _ -> },
            onRemoveChecklistItem = { _, _ -> },
            onUpdateChecklistText = { _, _, _ -> },
            onMoveChecklistItem = { _, _, _ -> },
            onAddLabel = { _, _ -> },
            onRemoveLabel = { _, _ -> },
            onSearchQueryChange = {},
            onSelectLabel = {},
            onSetShowArchived = {},
            onLoadAttachmentPreview = { _, _ -> },
            onRemoveAttachment = { _, _ -> },
            onToggleArchived = {},
            onClearError = {},
            onImportSharedKey = { _, _, _ -> },
            onDeleteSharedKey = {},
            onUploadBackup = {},
            onRestoreBackup = { _, _, _, _, _ -> },
            onRestoreSavedBackup = { _, _ -> },
            onDeleteSavedBackup = {},
            onScanQrKey = {},
            onExportLocalBackup = {},
            onImportLocalBackup = { _, _ -> },
            onExportKeyManager = { _, _ -> },
            onImportKeyManager = {},
            onGenerateXChaChaKey = {},
            onGeneratePgpKey = {},
            onBuildKeyTransferPayload = { null },
            onStartNfcKeyShare = {}
        )
    }
}
