package com.androidircx.nulvex.billing

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams

object PlayBillingProducts {
    // Keep these IDs in sync with one-time products in Google Play Console.
    const val REMOVE_ADS_ONE_TIME = "remove_ads_lifetime"
    const val PRO_FEATURES_ONE_TIME = "pro_features_lifetime"

    val oneTimeProducts: List<String> = listOf(
        REMOVE_ADS_ONE_TIME,
        PRO_FEATURES_ONE_TIME
    )

    fun asQueryProducts(): List<QueryProductDetailsParams.Product> {
        return oneTimeProducts.map { productId ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
    }
}

object PlayBillingFactory {
    fun createClient(
        context: Context,
        purchasesUpdatedListener: PurchasesUpdatedListener
    ): BillingClient {
        return BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder()
                    .enableOneTimeProducts()
                    .build()
            )
            .build()
    }
}
