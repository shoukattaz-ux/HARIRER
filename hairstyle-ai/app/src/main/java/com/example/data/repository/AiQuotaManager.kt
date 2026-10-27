package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.api.AiTryOnConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import java.util.Locale

/**
 * Daily free AI preview quota.
 *
 * Usage is persisted per calendar day, so the limit survives app restarts and resets at local
 * midnight. This is a client-side guard against accidental overuse; hard limits must also be
 * enforced server-side (Firebase AI Logic quotas / App Check), since a device's storage and clock
 * can be tampered with.
 */
object AiQuotaManager {

    private const val PREFS_NAME = "ai_quota_prefs"
    private const val KEY_DAY = "quota_day"
    private const val KEY_USED = "quota_used"

    /** Today's limit: the free limit, or the Premium limit while a verified purchase is active. */
    @Volatile
    var maxDailyLimit: Int = AiTryOnConfig.FREE_DAILY_TRY_LIMIT
        private set

    private var prefs: SharedPreferences? = null
    private var day: String = ""
    private var used: Int = 0

    /** Returns the current calendar day key; replaceable in tests. */
    internal var dayProvider: () -> String = ::currentDayKey

    private val _remainingAttempts = MutableStateFlow(maxDailyLimit)
    val remainingAttempts: StateFlow<Int> = _remainingAttempts.asStateFlow()

    /** Loads persisted usage. Called once from the Application. */
    @Synchronized
    fun init(context: Context) {
        val store = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = store
        day = store.getString(KEY_DAY, "").orEmpty()
        used = store.getInt(KEY_USED, 0).coerceAtLeast(0)
        refreshDay()
    }

    /**
     * Checks if the user has available free attempts remaining today.
     */
    @Synchronized
    fun canGenerate(): Boolean {
        refreshDay()
        return used < maxDailyLimit
    }

    /**
     * Returns formatted counter text, e.g., "Free previews remaining: 2/3"
     */
    fun getQuotaStatusText(): String {
        return "Free previews remaining: ${remainingAttempts.value}/$maxDailyLimit"
    }

    /**
     * Records a successful AI generation.
     * Decrements the remaining counter ONLY on success.
     * Failed API requests do NOT decrement the counter.
     */
    @Synchronized
    fun recordSuccess() {
        refreshDay()
        used = (used + 1).coerceAtMost(AiTryOnConfig.PREMIUM_DAILY_TRY_LIMIT)
        persist()
        publish()
    }

    /**
     * Switches between the free and Premium daily limits. Usage already counted today is kept,
     * so upgrading adds the difference instead of starting over.
     */
    @Synchronized
    fun setPremium(isPremium: Boolean) {
        maxDailyLimit = if (isPremium) AiTryOnConfig.PREMIUM_DAILY_TRY_LIMIT else AiTryOnConfig.FREE_DAILY_TRY_LIMIT
        refreshDay()
    }

    /** Re-checks the date so a new day's quota shows up without restarting the app. */
    @Synchronized
    fun refreshDay() {
        val today = dayProvider()
        if (today != day) {
            day = today
            used = 0
            persist()
        }
        publish()
    }

    /**
     * Helper to reset quota during development/testing.
     */
    @Synchronized
    fun resetQuotaForTesting() {
        used = 0
        persist()
        publish()
    }

    private fun publish() {
        _remainingAttempts.value = (maxDailyLimit - used).coerceAtLeast(0)
    }

    private fun persist() {
        prefs?.edit()?.putString(KEY_DAY, day)?.putInt(KEY_USED, used)?.apply()
    }

    private fun currentDayKey(): String {
        val now = Calendar.getInstance()
        return String.format(
            Locale.US,
            "%04d-%02d-%02d",
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH) + 1,
            now.get(Calendar.DAY_OF_MONTH)
        )
    }
}
