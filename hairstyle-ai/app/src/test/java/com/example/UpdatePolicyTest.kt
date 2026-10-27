package com.example

import com.example.update.UpdatePolicy
import com.example.update.UpdatePolicy.PlayUpdateType
import com.example.update.UpdatePolicy.SelfHostedDecision
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for when and how HairLook updates itself.
 */
class UpdatePolicyTest {

    private fun manifest(versionCode: Int = 5, minSupported: Int = 0, apkUrl: String = "https://updates.example.com/a.apk") =
        UpdatePolicy.UpdateManifest(
            versionCode = versionCode,
            versionName = "1.$versionCode",
            apkUrl = apkUrl,
            minSupportedVersionCode = minSupported,
            releaseNotes = "Notes"
        )

    @Test
    fun play_noUpdate_meansNone() {
        assertEquals(PlayUpdateType.NONE, UpdatePolicy.choosePlayUpdate(false, 5, 30, true, true))
    }

    @Test
    fun play_normalUpdate_isFlexible() {
        assertEquals(PlayUpdateType.FLEXIBLE, UpdatePolicy.choosePlayUpdate(true, 0, 2, true, true))
    }

    @Test
    fun play_highPriority_isImmediate() {
        assertEquals(PlayUpdateType.IMMEDIATE, UpdatePolicy.choosePlayUpdate(true, UpdatePolicy.IMMEDIATE_PRIORITY, 0, true, true))
    }

    @Test
    fun play_longIgnoredUpdate_isImmediate() {
        assertEquals(
            PlayUpdateType.IMMEDIATE,
            UpdatePolicy.choosePlayUpdate(true, 0, UpdatePolicy.IMMEDIATE_AFTER_STALENESS_DAYS, true, true)
        )
    }

    @Test
    fun play_urgentButImmediateNotAllowed_fallsBackToFlexible() {
        assertEquals(PlayUpdateType.FLEXIBLE, UpdatePolicy.choosePlayUpdate(true, 5, 30, false, true))
    }

    @Test
    fun selfHosted_newerVersion_isOptional() {
        val decision = UpdatePolicy.evaluate(manifest(versionCode = 5), currentVersionCode = 3, dismissedVersionCode = -1)
        assertTrue(decision is SelfHostedDecision.Optional)
    }

    @Test
    fun selfHosted_sameOrOlderVersion_isNone() {
        assertEquals(SelfHostedDecision.None, UpdatePolicy.evaluate(manifest(versionCode = 3), 3, -1))
        assertEquals(SelfHostedDecision.None, UpdatePolicy.evaluate(manifest(versionCode = 2), 3, -1))
        assertEquals(SelfHostedDecision.None, UpdatePolicy.evaluate(null, 3, -1))
    }

    @Test
    fun selfHosted_belowMinimum_isRequired_evenIfDismissed() {
        val decision = UpdatePolicy.evaluate(manifest(versionCode = 5, minSupported = 4), 3, dismissedVersionCode = 5)
        assertTrue(decision is SelfHostedDecision.Required)
    }

    @Test
    fun selfHosted_dismissedOptionalVersion_isNotShownAgain() {
        assertEquals(SelfHostedDecision.None, UpdatePolicy.evaluate(manifest(versionCode = 5), 3, dismissedVersionCode = 5))
    }

    @Test
    fun selfHosted_insecureDownloadUrl_isIgnored() {
        val decision = UpdatePolicy.evaluate(manifest(apkUrl = "http://updates.example.com/a.apk"), 3, -1)
        assertEquals(SelfHostedDecision.None, decision)
    }

    @Test
    fun manifestUrl_placeholderOrInsecure_isNotConfigured() {
        assertFalse(UpdatePolicy.isManifestUrlConfigured("MY_UPDATE_MANIFEST_URL"))
        assertFalse(UpdatePolicy.isManifestUrlConfigured("http://updates.example.com/update.json"))
        assertFalse(UpdatePolicy.isManifestUrlConfigured(""))
        assertTrue(UpdatePolicy.isManifestUrlConfigured("https://updates.example.com/update.json"))
    }
}
