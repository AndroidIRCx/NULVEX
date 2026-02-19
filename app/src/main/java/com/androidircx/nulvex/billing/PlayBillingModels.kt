package com.androidircx.nulvex.billing

data class BillingProductInfo(
    val productId: String,
    val formattedPrice: String,
    val offerToken: String?
)

enum class BillingPurchaseState {
    PURCHASED,
    PENDING,
    UNSPECIFIED
}

data class BillingPurchaseInfo(
    val products: List<String>,
    val state: BillingPurchaseState,
    val isAcknowledged: Boolean,
    val purchaseToken: String
)

enum class PurchaseUpdateStatus {
    OK,
    USER_CANCELED,
    ERROR
}

data class PurchaseUpdateResult(
    val status: PurchaseUpdateStatus,
    val debugMessage: String,
    val purchases: List<BillingPurchaseInfo>
)
