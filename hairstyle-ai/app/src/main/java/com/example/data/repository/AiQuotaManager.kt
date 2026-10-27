package com.example.data.repository

import com.example.data.api.AiTryOnConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Modular usage and quota manager for development/testing on Google Gemini Free Tier.
 * Tracks remaining free generation attempts, preventing excessive or unnecessary API requests.
 */
object AiQuotaManager {

    val maxDailyLimit: Int = AiTryOnConfig.FREE_DAILY_TRY_LIMIT

    private val _remainingAttempts = MutableStateFlow(maxDailyLimit)
    val remainingAttempts: StateFlow<Int> = _remainingAttempts.asStateFlow()

    /**
     * Checks if the user has available free attempts remaining.
     */
    fun canGenerate(): Boolean = _remainingAttempts.value > 0

    /**
     * Returns formatted counter text, e.g., "Free previews remaining: 2/3"
     */
    fun getQuotaStatusText(): String {
        return "Free previews remaining: ${_remainingAttempts.value}/$maxDailyLimit"
    }

    /**
     * Records a successful AI generation.
     * Decrements the remaining counter ONLY on success.
     * Failed API requests do NOT decrement the counter.
     */
    fun recordSuccess() {
        _remainingAttempts.update { current ->
            if (current > 0) current - 1 else 0
        }
    }

    /**
     * Helper to reset quota during testing or when a new day arrives.
     */
    fun resetQuotaForTesting() {
        _remainingAttempts.value = maxDailyLimit
    }
}
