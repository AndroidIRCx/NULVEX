package com.androidircx.nulvex.billing

interface PlayBillingGateway {
    fun queryProductDetails(callback: (ok: Boolean, products: List<BillingProductInfo>) -> Unit)
    fun queryPurchases(callback: (ok: Boolean, purchases: List<BillingPurchaseInfo>) -> Unit)
    fun launchPurchase(productId: String, offerToken: String): Pair<Boolean, String>
    fun acknowledgePurchase(purchaseToken: String, callback: (ok: Boolean) -> Unit)
}

interface PlayBillingStateSink {
    fun setBillingReady(ready: Boolean)
    fun updateBillingPrice(productId: String, price: String)
    fun showError(message: String)
    fun grantLifetimeRemoveAds()
    fun grantLifetimeProFeatures()
    fun revokeLifetimeRemoveAds()
    fun revokeLifetimeProFeatures()
    fun onProSyncTokenAvailable(purchaseToken: String)
    fun refreshAdFreeState()
}

class PlayBillingCoordinator(
    private val gateway: PlayBillingGateway,
    private val sink: PlayBillingStateSink
) {
    private val productsById = mutableMapOf<String, BillingProductInfo>()

    fun onBillingSetupFinished(ok: Boolean) {
        if (!ok) {
            sink.setBillingReady(false)
            return
        }
        sink.setBillingReady(true)
        refreshCatalog()
        restorePurchases()
    }

    fun onBillingDisconnected() {
        sink.setBillingReady(false)
    }

    fun refreshCatalog() {
        gateway.queryProductDetails { ok, products ->
            if (!ok) return@queryProductDetails
            productsById.clear()
            products.forEach { product ->
                productsById[product.productId] = product
                sink.updateBillingPrice(product.productId, product.formattedPrice)
            }
        }
    }

    fun restorePurchases() {
        gateway.queryPurchases { ok, purchases ->
            if (!ok) return@queryPurchases
            reconcileEntitlements(purchases)
        }
    }

    /**
     * queryPurchases returns the authoritative set of currently-owned purchases, so use it
     * to both grant new entitlements AND revoke ones no longer backed by a purchase (refund
     * / chargeback). Only called from the full restore path — never from the incremental
     * onPurchasesUpdated, which carries only the just-changed purchases.
     */
    private fun reconcileEntitlements(purchases: List<BillingPurchaseInfo>) {
        processPurchases(purchases)
        val ownedProducts = purchases
            .filter { it.state == BillingPurchaseState.PURCHASED }
            .flatMap { it.products }
            .toSet()
        if (!ownedProducts.contains(PlayBillingProducts.REMOVE_ADS_ONE_TIME)) {
            sink.revokeLifetimeRemoveAds()
        }
        if (!ownedProducts.contains(PlayBillingProducts.PRO_FEATURES_ONE_TIME)) {
            sink.revokeLifetimeProFeatures()
        }
        sink.refreshAdFreeState()
    }

    fun buy(productId: String) {
        val product = productsById[productId]
        if (product == null) {
            sink.showError("Product is not available yet. Try again in a moment.")
            refreshCatalog()
            return
        }
        // offerToken is only required for subscriptions; INAPP one-time products may have null token.
        val offerToken = product.offerToken ?: ""
        val (ok, debugMessage) = gateway.launchPurchase(productId, offerToken)
        if (!ok) {
            sink.showError("Unable to start purchase flow ($debugMessage)")
        }
    }

    fun onPurchasesUpdated(result: PurchaseUpdateResult) {
        when (result.status) {
            PurchaseUpdateStatus.OK -> processPurchases(result.purchases)
            PurchaseUpdateStatus.USER_CANCELED -> Unit
            PurchaseUpdateStatus.ERROR -> sink.showError("Purchase failed: ${result.debugMessage}")
        }
    }

    private fun processPurchases(purchases: List<BillingPurchaseInfo>) {
        purchases.forEach { purchase ->
            if (purchase.state != BillingPurchaseState.PURCHASED) return@forEach
            if (purchase.isAcknowledged) {
                applyEntitlements(purchase)
            } else {
                gateway.acknowledgePurchase(purchase.purchaseToken) { ok ->
                    if (ok) applyEntitlements(purchase)
                }
            }
        }
    }

    private fun applyEntitlements(purchase: BillingPurchaseInfo) {
        if (purchase.products.contains(PlayBillingProducts.PRO_FEATURES_ONE_TIME)) {
            sink.onProSyncTokenAvailable(purchase.purchaseToken)
        }
        purchase.products.forEach { productId ->
            when (productId) {
                PlayBillingProducts.REMOVE_ADS_ONE_TIME -> sink.grantLifetimeRemoveAds()
                PlayBillingProducts.PRO_FEATURES_ONE_TIME -> sink.grantLifetimeProFeatures()
            }
        }
        sink.refreshAdFreeState()
    }
}
