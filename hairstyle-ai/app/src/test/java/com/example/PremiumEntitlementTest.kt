package com.example

import com.example.billing.PremiumEntitlement
import com.example.billing.PremiumEntitlement.PurchaseInfo
import com.example.billing.PremiumEntitlement.State
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for which Google Play purchases grant HairLook Premium.
 */
class PremiumEntitlementTest {

    private val productId = "hairlook_premium"

    private fun purchase(
        products: List<String> = listOf(productId),
        state: State = State.PURCHASED,
        signature: String = "valid"
    ) = PurchaseInfo(
        products = products,
        state = state,
        isAcknowledged = false,
        originalJson = "{}",
        signature = signature,
        purchaseToken = "token-${products.joinToString()}-$state"
    )

    private val authentic: (PurchaseInfo) -> Boolean = { it.signature == "valid" }

    @Test
    fun completedAuthenticPurchase_grantsPremium() {
        val granting = PremiumEntitlement.grantingPurchases(listOf(purchase()), productId, authentic)
        assertEquals(1, granting.size)
    }

    @Test
    fun pendingPurchase_doesNotGrantButIsReported() {
        val purchases = listOf(purchase(state = State.PENDING))
        assertTrue(PremiumEntitlement.grantingPurchases(purchases, productId, authentic).isEmpty())
        assertTrue(PremiumEntitlement.hasPendingPurchase(purchases, productId))
    }

    @Test
    fun forgedSignature_doesNotGrant() {
        val purchases = listOf(purchase(signature = "forged"))
        assertTrue(PremiumEntitlement.grantingPurchases(purchases, productId, authentic).isEmpty())
    }

    @Test
    fun otherProduct_doesNotGrant() {
        val purchases = listOf(purchase(products = listOf("some_other_product")))
        assertTrue(PremiumEntitlement.grantingPurchases(purchases, productId, authentic).isEmpty())
        assertFalse(PremiumEntitlement.hasPendingPurchase(purchases, productId))
    }

    @Test
    fun noPurchases_meansNoPremium() {
        assertTrue(PremiumEntitlement.grantingPurchases(emptyList(), productId, authentic).isEmpty())
    }

    @Test
    fun billingPeriod_isReadable() {
        assertEquals("month", PremiumEntitlement.periodLabel("P1M"))
        assertEquals("year", PremiumEntitlement.periodLabel("P1Y"))
        assertNull(PremiumEntitlement.periodLabel(null))
    }
}
