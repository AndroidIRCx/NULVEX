package com.androidircx.nulvex.billing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayBillingCoordinatorTest {

    private class FakeGateway : PlayBillingGateway {
        var productsOk: Boolean = true
        var products: List<BillingProductInfo> = emptyList()
        var purchasesOk: Boolean = true
        var purchases: List<BillingPurchaseInfo> = emptyList()
        var launchResult: Pair<Boolean, String> = true to ""
        var acknowledgedTokens = mutableListOf<String>()
        var queryProductsCalls = 0
        var queryPurchasesCalls = 0
        var launchedProductId: String? = null
        var launchedOfferToken: String? = null

        override fun queryProductDetails(callback: (ok: Boolean, products: List<BillingProductInfo>) -> Unit) {
            queryProductsCalls++
            callback(productsOk, products)
        }

        override fun queryPurchases(callback: (ok: Boolean, purchases: List<BillingPurchaseInfo>) -> Unit) {
            queryPurchasesCalls++
            callback(purchasesOk, purchases)
        }

        override fun launchPurchase(productId: String, offerToken: String): Pair<Boolean, String> {
            launchedProductId = productId
            launchedOfferToken = offerToken
            return launchResult
        }

        override fun acknowledgePurchase(purchaseToken: String, callback: (ok: Boolean) -> Unit) {
            acknowledgedTokens += purchaseToken
            callback(true)
        }
    }

    private class FakeSink : PlayBillingStateSink {
        var billingReadyState = false
        val prices = mutableMapOf<String, String>()
        var lastError: String? = null
        var removeAdsGranted = 0
        var proGranted = 0
        var refreshCalls = 0

        override fun setBillingReady(ready: Boolean) {
            billingReadyState = ready
        }

        override fun updateBillingPrice(productId: String, price: String) {
            prices[productId] = price
        }

        override fun showError(message: String) {
            lastError = message
        }

        override fun grantLifetimeRemoveAds() {
            removeAdsGranted++
        }

        override fun grantLifetimeProFeatures() {
            proGranted++
        }

        override fun refreshAdFreeState() {
            refreshCalls++
        }
    }

    @Test
    fun onBillingSetupFinished_success_setsReadyAndLoadsCatalogAndPurchases() {
        val gateway = FakeGateway().apply {
            products = listOf(
                BillingProductInfo(PlayBillingProducts.REMOVE_ADS_ONE_TIME, "$1.99", "offerA")
            )
        }
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)

        coordinator.onBillingSetupFinished(ok = true)

        assertTrue(sink.billingReadyState)
        assertEquals(1, gateway.queryProductsCalls)
        assertEquals(1, gateway.queryPurchasesCalls)
        assertEquals("$1.99", sink.prices[PlayBillingProducts.REMOVE_ADS_ONE_TIME])
    }

    @Test
    fun onBillingSetupFinished_failure_setsNotReadyOnly() {
        val gateway = FakeGateway()
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)

        coordinator.onBillingSetupFinished(ok = false)

        assertFalse(sink.billingReadyState)
        assertEquals(0, gateway.queryProductsCalls)
        assertEquals(0, gateway.queryPurchasesCalls)
    }

    @Test
    fun onBillingDisconnected_setsNotReady() {
        val gateway = FakeGateway()
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)

        coordinator.onBillingDisconnected()

        assertFalse(sink.billingReadyState)
    }

    @Test
    fun buy_withoutCatalog_showsErrorAndRefreshesCatalog() {
        val gateway = FakeGateway()
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)

        coordinator.buy(PlayBillingProducts.REMOVE_ADS_ONE_TIME)

        assertEquals("Product is not available yet. Try again in a moment.", sink.lastError)
        assertEquals(1, gateway.queryProductsCalls)
    }

    @Test
    fun buy_withMissingOfferToken_forOneTimeProduct_usesEmptyToken() {
        val gateway = FakeGateway().apply {
            products = listOf(
                BillingProductInfo(PlayBillingProducts.REMOVE_ADS_ONE_TIME, "$1.99", null)
            )
        }
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)
        coordinator.refreshCatalog()

        coordinator.buy(PlayBillingProducts.REMOVE_ADS_ONE_TIME)

        assertEquals(PlayBillingProducts.REMOVE_ADS_ONE_TIME, gateway.launchedProductId)
        assertEquals("", gateway.launchedOfferToken)
        assertEquals(null, sink.lastError)
    }

    @Test
    fun buy_launchFailure_surfacesError() {
        val gateway = FakeGateway().apply {
            products = listOf(
                BillingProductInfo(PlayBillingProducts.REMOVE_ADS_ONE_TIME, "$1.99", "offerA")
            )
            launchResult = false to "billing down"
        }
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)
        coordinator.refreshCatalog()

        coordinator.buy(PlayBillingProducts.REMOVE_ADS_ONE_TIME)

        assertEquals("Unable to start purchase flow (billing down)", sink.lastError)
    }

    @Test
    fun purchasesUpdated_ok_appliesAcknowledgedEntitlements() {
        val gateway = FakeGateway()
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)

        coordinator.onPurchasesUpdated(
            PurchaseUpdateResult(
                status = PurchaseUpdateStatus.OK,
                debugMessage = "",
                purchases = listOf(
                    BillingPurchaseInfo(
                        products = listOf(
                            PlayBillingProducts.REMOVE_ADS_ONE_TIME,
                            PlayBillingProducts.PRO_FEATURES_ONE_TIME
                        ),
                        state = BillingPurchaseState.PURCHASED,
                        isAcknowledged = true,
                        purchaseToken = "tok-1"
                    )
                )
            )
        )

        assertEquals(1, sink.removeAdsGranted)
        assertEquals(1, sink.proGranted)
        assertEquals(1, sink.refreshCalls)
    }

    @Test
    fun purchasesUpdated_ok_acknowledgesBeforeGrant() {
        val gateway = FakeGateway()
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)

        coordinator.onPurchasesUpdated(
            PurchaseUpdateResult(
                status = PurchaseUpdateStatus.OK,
                debugMessage = "",
                purchases = listOf(
                    BillingPurchaseInfo(
                        products = listOf(PlayBillingProducts.REMOVE_ADS_ONE_TIME),
                        state = BillingPurchaseState.PURCHASED,
                        isAcknowledged = false,
                        purchaseToken = "tok-2"
                    )
                )
            )
        )

        assertEquals(listOf("tok-2"), gateway.acknowledgedTokens)
        assertEquals(1, sink.removeAdsGranted)
        assertEquals(1, sink.refreshCalls)
    }

    @Test
    fun purchasesUpdated_ignoresNonPurchasedStates() {
        val gateway = FakeGateway()
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)

        coordinator.onPurchasesUpdated(
            PurchaseUpdateResult(
                status = PurchaseUpdateStatus.OK,
                debugMessage = "",
                purchases = listOf(
                    BillingPurchaseInfo(
                        products = listOf(PlayBillingProducts.REMOVE_ADS_ONE_TIME),
                        state = BillingPurchaseState.PENDING,
                        isAcknowledged = true,
                        purchaseToken = "tok-pending"
                    )
                )
            )
        )

        assertEquals(0, sink.removeAdsGranted)
        assertEquals(0, sink.refreshCalls)
    }

    @Test
    fun purchasesUpdated_userCanceled_doesNothing() {
        val gateway = FakeGateway()
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)

        coordinator.onPurchasesUpdated(
            PurchaseUpdateResult(
                status = PurchaseUpdateStatus.USER_CANCELED,
                debugMessage = "canceled",
                purchases = emptyList()
            )
        )

        assertEquals(null, sink.lastError)
        assertEquals(0, sink.refreshCalls)
    }

    @Test
    fun purchasesUpdated_error_surfacesMessage() {
        val gateway = FakeGateway()
        val sink = FakeSink()
        val coordinator = PlayBillingCoordinator(gateway, sink)

        coordinator.onPurchasesUpdated(
            PurchaseUpdateResult(
                status = PurchaseUpdateStatus.ERROR,
                debugMessage = "boom",
                purchases = emptyList()
            )
        )

        assertEquals("Purchase failed: boom", sink.lastError)
    }
}
