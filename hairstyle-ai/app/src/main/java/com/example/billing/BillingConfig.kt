package com.example.billing

import com.android.billingclient.api.BillingClient

/**
 * Google Play product configuration for HairLook Premium.
 * The product must exist in Play Console (Monetize → Products) with this exact ID.
 */
object BillingConfig {

    /** Product ID created in Play Console. */
    const val PREMIUM_PRODUCT_ID = "hairlook_premium"

    /**
     * SUBS for a recurring subscription (recommended: AI previews cost money every day),
     * or INAPP for a one-time purchase.
     */
    const val PREMIUM_PRODUCT_TYPE = BillingClient.ProductType.SUBS
}
