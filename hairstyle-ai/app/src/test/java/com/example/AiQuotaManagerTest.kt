package com.example

import com.example.data.api.AiTryOnConfig
import com.example.data.repository.AiQuotaManager
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the daily free AI preview quota.
 */
class AiQuotaManagerTest {

    private var today = "2026-10-27"
    private lateinit var originalDayProvider: () -> String

    @Before
    fun setUp() {
        originalDayProvider = AiQuotaManager.dayProvider
        AiQuotaManager.dayProvider = { today }
        AiQuotaManager.refreshDay()
        AiQuotaManager.resetQuotaForTesting()
    }

    @After
    fun tearDown() {
        AiQuotaManager.setPremium(false)
        AiQuotaManager.dayProvider = originalDayProvider
    }

    @Test
    fun premium_raisesDailyLimit_andKeepsTodaysUsage() {
        repeat(AiTryOnConfig.FREE_DAILY_TRY_LIMIT) { AiQuotaManager.recordSuccess() }
        assertFalse(AiQuotaManager.canGenerate())

        AiQuotaManager.setPremium(true)
        assertTrue(AiQuotaManager.canGenerate())
        assertEquals(
            AiTryOnConfig.PREMIUM_DAILY_TRY_LIMIT - AiTryOnConfig.FREE_DAILY_TRY_LIMIT,
            AiQuotaManager.remainingAttempts.value
        )
    }

    @Test
    fun premiumEnding_restoresFreeLimit() {
        AiQuotaManager.setPremium(true)
        repeat(5) { AiQuotaManager.recordSuccess() }
        AiQuotaManager.setPremium(false)
        assertFalse(AiQuotaManager.canGenerate())
        assertEquals(0, AiQuotaManager.remainingAttempts.value)
    }

    @Test
    fun quota_isUsedUpAfterDailyLimit() {
        repeat(AiQuotaManager.maxDailyLimit) {
            assertTrue(AiQuotaManager.canGenerate())
            AiQuotaManager.recordSuccess()
        }
        assertFalse(AiQuotaManager.canGenerate())
        assertEquals(0, AiQuotaManager.remainingAttempts.value)
    }

    @Test
    fun quota_neverGoesBelowZero() {
        repeat(AiQuotaManager.maxDailyLimit + 3) { AiQuotaManager.recordSuccess() }
        assertEquals(0, AiQuotaManager.remainingAttempts.value)
    }

    @Test
    fun quota_resetsOnNewDay() {
        repeat(AiQuotaManager.maxDailyLimit) { AiQuotaManager.recordSuccess() }
        assertFalse(AiQuotaManager.canGenerate())

        today = "2026-10-28"
        assertTrue(AiQuotaManager.canGenerate())
        assertEquals(AiQuotaManager.maxDailyLimit, AiQuotaManager.remainingAttempts.value)
    }

    @Test
    fun quota_doesNotResetWithinSameDay() {
        AiQuotaManager.recordSuccess()
        AiQuotaManager.refreshDay()
        assertEquals(AiQuotaManager.maxDailyLimit - 1, AiQuotaManager.remainingAttempts.value)
    }
}
