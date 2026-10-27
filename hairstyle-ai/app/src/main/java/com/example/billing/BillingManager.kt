package com.example.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.example.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Google Play Billing for HairLook Premium.
 *
 * Premium is never stored as a local flag: it is re-derived from Google Play's purchase records on
 * every app start and resume, so it ends automatically when a subscription is canceled or refunded.
 */
object BillingManager {

    private const val TAG = "BillingManager"
    private const val MAX_RECONNECT_DELAY_MS = 60_000L

    data class BillingState(
        val isPremium: Boolean = false,
        val isReady: Boolean = false,
        /** Localized price from Play, e.g. "$1.99" or "Rs 550". */
        val priceLabel: String? = null,
        /** "month", "year"… for subscriptions; null for one-time purchases. */
        val periodLabel: String? = null,
        val isPurchasePending: Boolean = false
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(BillingState())
    val state: StateFlow<BillingState> = _state.asStateFlow()

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    /** One-off user-facing messages (purchase complete, canceled, errors). */
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    private var billingClient: BillingClient? = null
    private var productDetails: ProductDetails? = null
    private var reconnectDelayMs = 1_000L

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> scope.launch {
                handlePurchases(purchases.orEmpty(), fromPurchaseFlow = true)
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> Unit
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                scope.launch { refreshPurchases() }
            }
            else -> {
                Log.w(TAG, "Purchase failed: ${result.responseCode} ${result.debugMessage}")
                _messages.tryEmit("Purchase didn't go through. Please try again.")
            }
        }
    }

    fun init(context: Context) {
        if (billingClient != null) return
        billingClient = BillingClient.newBuilder(context.applicationContext)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            )
            .build()
        connect()
    }

    private fun connect() {
        val client = billingClient ?: return
        if (client.isReady) return
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    reconnectDelayMs = 1_000L
                    _state.update { it.copy(isReady = true) }
                    scope.launch {
                        loadProductDetails()
                        refreshPurchases()
                    }
                } else {
                    Log.w(TAG, "Billing setup failed: ${result.responseCode} ${result.debugMessage}")
                    _state.update { it.copy(isReady = false) }
                }
            }

            override fun onBillingServiceDisconnected() {
                _state.update { it.copy(isReady = false) }
                scope.launch {
                    delay(reconnectDelayMs)
                    reconnectDelayMs = (reconnectDelayMs * 2).coerceAtMost(MAX_RECONNECT_DELAY_MS)
                    connect()
                }
            }
        })
    }

    private suspend fun loadProductDetails() {
        val client = billingClient ?: return
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(BillingConfig.PREMIUM_PRODUCT_ID)
                        .setProductType(BillingConfig.PREMIUM_PRODUCT_TYPE)
                        .build()
                )
            )
            .build()
        val result = client.queryProductDetails(params)
        val details = result.productDetailsList?.firstOrNull()
        if (result.billingResult.responseCode != BillingClient.BillingResponseCode.OK || details == null) {
            Log.w(TAG, "Premium product not available: ${result.billingResult.debugMessage}")
            return
        }
        productDetails = details
        val subscriptionPhase = details.subscriptionOfferDetails
            ?.firstOrNull()
            ?.pricingPhases
            ?.pricingPhaseList
            ?.lastOrNull() // the recurring base price after any intro/free-trial phases
        @Suppress("DEPRECATION")
        val price = subscriptionPhase?.formattedPrice ?: details.oneTimePurchaseOfferDetails?.formattedPrice
        _state.update {
            it.copy(
                priceLabel = price,
                periodLabel = PremiumEntitlement.periodLabel(subscriptionPhase?.billingPeriod)
            )
        }
    }

    /** Re-reads purchases from Google Play. Call on start and whenever the app returns to the foreground. */
    fun refreshPurchases() {
        val client = billingClient ?: return
        if (!client.isReady) {
            connect()
            return
        }
        scope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingConfig.PREMIUM_PRODUCT_TYPE)
                .build()
            val result = client.queryPurchasesAsync(params)
            if (result.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                handlePurchases(result.purchasesList, fromPurchaseFlow = false)
            } else {
                Log.w(TAG, "queryPurchases failed: ${result.billingResult.debugMessage}")
            }
        }
    }

    /** Google Play page where the user can manage or cancel the Premium subscription. */
    fun manageSubscriptionUrl(packageName: String): String =
        "https://play.google.com/store/account/subscriptions?sku=${BillingConfig.PREMIUM_PRODUCT_ID}&package=$packageName"

    /** Opens the Google Play purchase sheet. Returns false if billing isn't ready yet. */
    fun launchPurchase(activity: Activity): Boolean {
        val client = billingClient
        val details = productDetails
        if (client == null || !client.isReady || details == null) {
            _messages.tryEmit("Google Play billing isn't available right now. Please try again in a moment.")
            connect()
            return false
        }
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(details)
            .apply {
                details.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let { setOfferToken(it) }
            }
            .build()
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams))
            .build()
        val result = client.launchBillingFlow(activity, flowParams)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            Log.w(TAG, "launchBillingFlow failed: ${result.responseCode} ${result.debugMessage}")
            _messages.tryEmit("Couldn't open Google Play. Please try again.")
            return false
        }
        return true
    }

    private suspend fun handlePurchases(purchases: List<Purchase>, fromPurchaseFlow: Boolean) {
        val infos = purchases.map { it.toInfo() }
        val granting = PremiumEntitlement.grantingPurchases(infos, BillingConfig.PREMIUM_PRODUCT_ID, ::isAuthentic)
        val wasPremium = _state.value.isPremium

        // Purchases must be acknowledged within 3 days or Google refunds them automatically
        granting.filterNot { it.isAcknowledged }.forEach { purchase ->
            val ackResult = billingClient?.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
            )
            if (ackResult?.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.w(TAG, "Acknowledge failed: ${ackResult?.debugMessage}")
            }
        }

        val isPremium = granting.isNotEmpty()
        val isPending = PremiumEntitlement.hasPendingPurchase(infos, BillingConfig.PREMIUM_PRODUCT_ID)
        _state.update { it.copy(isPremium = isPremium, isPurchasePending = isPending) }

        when {
            isPremium && !wasPremium && fromPurchaseFlow -> _messages.tryEmit("Welcome to Premium! 🎉")
            isPending && fromPurchaseFlow -> _messages.tryEmit("Your payment is pending. Premium unlocks as soon as it completes.")
            !isPremium && fromPurchaseFlow && infos.isNotEmpty() && !isPending ->
                _messages.tryEmit("We couldn't verify this purchase. Please contact support.")
        }
    }

    private fun isAuthentic(purchase: PremiumEntitlement.PurchaseInfo): Boolean {
        val licenseKey = BuildConfig.PLAY_LICENSE_KEY
        if (licenseKey.isBlank() || licenseKey == "MY_PLAY_LICENSE_KEY") {
            // Not configured: rely on Google Play's own checks. Configure PLAY_LICENSE_KEY before release.
            Log.w(TAG, "PLAY_LICENSE_KEY not set; skipping purchase signature verification")
            return true
        }
        return PurchaseSignatureVerifier.verify(licenseKey, purchase.originalJson, purchase.signature)
    }

    private fun Purchase.toInfo() = PremiumEntitlement.PurchaseInfo(
        products = products,
        state = when (purchaseState) {
            Purchase.PurchaseState.PURCHASED -> PremiumEntitlement.State.PURCHASED
            Purchase.PurchaseState.PENDING -> PremiumEntitlement.State.PENDING
            else -> PremiumEntitlement.State.OTHER
        },
        isAcknowledged = isAcknowledged,
        originalJson = originalJson,
        signature = signature,
        purchaseToken = purchaseToken
    )
}
