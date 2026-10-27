package com.example.data.api

import com.example.BuildConfig

/**
 * Decides which AI backend this build may use.
 *
 * - Firebase AI Logic (preferred): the Gemini key stays on Google's servers and every request
 *   carries a Firebase App Check token, so only the genuine app can call it.
 * - Direct Gemini REST with a client-side key: development only. Release builds strip the key
 *   and disable this path unless `hairlook.allowClientGeminiKeyInRelease=true` is set.
 */
object AiBackendConfig {

    /** Set once at startup by [com.example.HairLookApplication]. */
    @Volatile
    var isFirebaseConfigured: Boolean = false
        internal set

    val isClientKeyAllowed: Boolean
        get() = BuildConfig.ALLOW_CLIENT_GEMINI_KEY
}
