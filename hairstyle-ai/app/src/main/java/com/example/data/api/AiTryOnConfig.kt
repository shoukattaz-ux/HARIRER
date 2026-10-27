package com.example.data.api

/**
 * Global configuration for AI Hairstyle Try-On.
 * Configured in one central location for development, free-tier testing, and cost protection.
 */
object AiTryOnConfig {

    /**
     * Daily free preview limit per user for development/testing.
     * Prevents unexpected API consumption on the Free Tier.
     */
    const val FREE_DAILY_TRY_LIMIT = 3

    /**
     * Google Gemini Image-to-Image / Multimodal model selected for free testing.
     * Current officially supported model: gemini-2.5-flash-image
     */
    const val PRIMARY_MODEL = "gemini-2.5-flash-image"

    /**
     * Fallback model in case of regional preview constraints.
     */
    const val FALLBACK_MODEL = "gemini-3.1-flash-image-preview"

    /**
     * Human-readable model label for UI/logging.
     */
    const val MODEL_DISPLAY_NAME = "Google Gemini 2.5 Flash Image"

    /**
     * Daily AI preview limit for Premium members (verified Google Play purchase).
     * A cap rather than "unlimited": every Gemini image has a cost, so heavy use must stay
     * below what a subscription pays.
     */
    const val PREMIUM_DAILY_TRY_LIMIT = 30

    /**
     * Free Tier optimization flag.
     */
    const val IS_FREE_TIER = true

    /**
     * Realistic hairstyle styling disclaimer text.
     */
    const val HAIRSTYLE_DISCLAIMER =
        "Virtual AI hairstyle preview for styling inspiration. Real-world hair texture, density, and growth patterns may vary."
}
