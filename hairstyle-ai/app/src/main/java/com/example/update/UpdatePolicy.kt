package com.example.update

/**
 * Pure rules for when and how to update. No Android or Play dependencies, so they are unit tested.
 */
object UpdatePolicy {

    /** Updates older than this many days become an immediate (blocking) Play update. */
    const val IMMEDIATE_AFTER_STALENESS_DAYS = 14

    /** Play update priority (0–5, set when uploading the release) at which an update is immediate. */
    const val IMMEDIATE_PRIORITY = 4

    enum class PlayUpdateType { NONE, FLEXIBLE, IMMEDIATE }

    /**
     * Chooses a Google Play in-app update type: critical or long-ignored updates block the app
     * (IMMEDIATE); everything else downloads in the background (FLEXIBLE).
     */
    fun choosePlayUpdate(
        isUpdateAvailable: Boolean,
        priority: Int,
        stalenessDays: Int?,
        isImmediateAllowed: Boolean,
        isFlexibleAllowed: Boolean
    ): PlayUpdateType {
        if (!isUpdateAvailable) return PlayUpdateType.NONE
        val isUrgent = priority >= IMMEDIATE_PRIORITY || (stalenessDays ?: 0) >= IMMEDIATE_AFTER_STALENESS_DAYS
        return when {
            isUrgent && isImmediateAllowed -> PlayUpdateType.IMMEDIATE
            isFlexibleAllowed -> PlayUpdateType.FLEXIBLE
            isImmediateAllowed -> PlayUpdateType.IMMEDIATE
            else -> PlayUpdateType.NONE
        }
    }

    /** Contents of the public `update.json` used by installs that did not come from Google Play. */
    data class UpdateManifest(
        val versionCode: Int,
        val versionName: String,
        val apkUrl: String,
        val minSupportedVersionCode: Int,
        val releaseNotes: String
    )

    sealed class SelfHostedDecision {
        object None : SelfHostedDecision()
        data class Optional(val manifest: UpdateManifest) : SelfHostedDecision()
        /** The installed version is below the minimum supported one: the user must update. */
        data class Required(val manifest: UpdateManifest) : SelfHostedDecision()
    }

    fun evaluate(
        manifest: UpdateManifest?,
        currentVersionCode: Int,
        dismissedVersionCode: Int
    ): SelfHostedDecision {
        if (manifest == null || manifest.versionCode <= currentVersionCode) return SelfHostedDecision.None
        if (!isSecureUrl(manifest.apkUrl)) return SelfHostedDecision.None
        if (currentVersionCode < manifest.minSupportedVersionCode) return SelfHostedDecision.Required(manifest)
        if (dismissedVersionCode == manifest.versionCode) return SelfHostedDecision.None
        return SelfHostedDecision.Optional(manifest)
    }

    fun isSecureUrl(url: String?): Boolean {
        val trimmed = url?.trim().orEmpty()
        return trimmed.startsWith("https://", ignoreCase = true) && trimmed.length > "https://".length
    }

    /** True when the manifest URL is configured (not blank and not the .env.example placeholder). */
    fun isManifestUrlConfigured(url: String?): Boolean =
        isSecureUrl(url) && !url.orEmpty().contains("MY_UPDATE_MANIFEST_URL")
}
