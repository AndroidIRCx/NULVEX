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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    private val vm: MainViewModel by viewModels()
    private val biometricStore by lazy { BiometricKeyStore(applicationContext) }
    private val adManager by lazy { VaultServiceLocator.adManager() }
    private var billingClient: BillingClient? = null
    private val productDetailsById = mutableMapOf<String, ProductDetails>()
    private lateinit var billingCoordinator: PlayBillingCoordinator
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
                    },
                    onOpenPurchases = vm::openPurchases,
                    onClosePurchases = vm::closePurchases,
                    onBuyRemoveAds = { billingCoordinator.buy(PlayBillingProducts.REMOVE_ADS_ONE_TIME) },
                    onBuyProFeatures = { billingCoordinator.buy(PlayBillingProducts.PRO_FEATURES_ONE_TIME) },
                    onRestorePurchases = { billingCoordinator.restorePurchases() }
                )
            }
        }
        billingCoordinator = PlayBillingCoordinator(billingGateway, billingStateSink)
        initBilling()
    }

    override fun onResume() {
        super.onResume()
        vm.refreshAdFreeState()
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
                    BillingProductInfo(
                        productId = details.productId,
                        formattedPrice = details.oneTimePurchaseOfferDetails?.formattedPrice ?: "Unavailable",
                        offerToken = details.oneTimePurchaseOfferDetails?.offerToken
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
            val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .setOfferToken(offerToken)
                .build()
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(listOf(productParams))
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
