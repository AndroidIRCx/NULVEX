package com.androidircx.nulvex

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.androidircx.nulvex.ui.MainScreen
import com.androidircx.nulvex.ui.MainViewModel
import com.androidircx.nulvex.ui.theme.NULVEXTheme
import com.androidircx.nulvex.security.BiometricKeyStore
import com.androidircx.nulvex.security.VaultKeyManager
import com.androidircx.nulvex.ads.AdManager
import com.androidircx.nulvex.VaultServiceLocator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    private val vm: MainViewModel by viewModels()
    private val biometricStore by lazy { BiometricKeyStore(applicationContext) }
    private val adManager by lazy { VaultServiceLocator.adManager() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                    onSetup = vm::setupPins,
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
                    onChangeRealPin = vm::changeRealPin,
                    onUpdateThemeMode = vm::updateThemeMode,
                    onOpenNew = vm::openNewNote,
                    onCreate = vm::createNote,
                    onOpenNote = vm::openNote,
                    onCloseNote = vm::closeNoteDetail,
                    onUpdateNoteText = vm::updateNoteText,
                    onDelete = vm::deleteNote,
                    onTogglePinned = vm::togglePinned,
                    onToggleChecklistItem = vm::toggleChecklistItem,
                    onAddChecklistItem = vm::addChecklistItem,
                    onRemoveChecklistItem = vm::removeChecklistItem,
                    onUpdateChecklistText = vm::updateChecklistText,
                    onMoveChecklistItem = vm::moveChecklistItem,
                    onAddLabel = vm::addLabel,
                    onRemoveLabel = vm::removeLabel,
                    onSearchQueryChange = vm::updateSearchQuery,
                    onSelectLabel = vm::updateActiveLabel,
                    onLoadAttachmentPreview = vm::loadAttachmentPreview,
                    onRemoveAttachment = vm::removeAttachment,
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
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.refreshAdFreeState()
    }

    override fun onStop() {
        super.onStop()
        vm.onAppBackgrounded()
    }

    override fun dispatchTouchEvent(ev: android.view.MotionEvent?): Boolean {
        vm.onUserInteraction()
        return super.dispatchTouchEvent(ev)
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
                    .setTitle("Enable fingerprint")
                    .setSubtitle("Authenticate to enable biometric unlock")
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
            .setTitle("Unlock")
            .setSubtitle("Authenticate to unlock your vault")
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
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    NULVEXTheme {
        MainScreen(
            state = com.androidircx.nulvex.ui.UiState(screen = com.androidircx.nulvex.ui.Screen.Setup),
            onCompleteOnboarding = {},
            onSetup = { _, _ -> },
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
            onCreate = { _, _, _, _, _, _, _ -> },
            onOpenNote = {},
            onCloseNote = {},
            onUpdateNoteText = { _, _ -> },
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
            onLoadAttachmentPreview = { _, _ -> },
            onRemoveAttachment = { _, _ -> },
            onClearError = {}
        )
    }
}
