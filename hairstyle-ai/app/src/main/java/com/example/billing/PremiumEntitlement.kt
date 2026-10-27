package com.example.billing

/**
 * Pure entitlement rules, independent of the Play Billing library so they can be unit tested.
 */
object PremiumEntitlement {

    enum class State { PURCHASED, PENDING, OTHER }

    data class PurchaseInfo(
        val products: List<String>,
        val state: State,
        val isAcknowledged: Boolean,
        val originalJson: String,
        val signature: String,
        val purchaseToken: String
    )

    /** Purchases that grant Premium: the right product, completed, and authentic. */
    fun grantingPurchases(
        purchases: List<PurchaseInfo>,
        productId: String,
        isAuthentic: (PurchaseInfo) -> Boolean
    ): List<PurchaseInfo> = purchases.filter {
        productId in it.products && it.state == State.PURCHASED && isAuthentic(it)
    }

    fun hasPendingPurchase(purchases: List<PurchaseInfo>, productId: String): Boolean =
        purchases.any { productId in it.products && it.state == State.PENDING }

    /**
     * ISO-8601 billing period from Play (e.g. "P1M", "P1Y", "P1W") to a short label for "/ month".
     */
    fun periodLabel(isoPeriod: String?): String? = when (isoPeriod) {
        null, "" -> null
        "P1W" -> "week"
        "P1M" -> "month"
        "P3M" -> "3 months"
        "P6M" -> "6 months"
        "P1Y" -> "year"
        else -> null
    }
}
