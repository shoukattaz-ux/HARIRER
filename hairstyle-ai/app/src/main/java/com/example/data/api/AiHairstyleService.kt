package com.example.data.api

import android.content.Context
import com.example.data.model.HairstyleItem
import com.example.data.model.UserPhotoSource
import com.example.data.repository.AiTryOnResult

/**
 * Reusable, model-agnostic AI Hairstyle Transformation service interface.
 * Decouples the application UI and ViewModel from specific Gemini models
 * to facilitate switching between Free Tier and production setups in the future.
 */
interface AiHairstyleService {

    /**
     * Name of the model currently powering the transformation service.
     */
    val currentModelName: String

    /**
     * Checks if the service has valid API credentials configured.
     */
    fun hasValidCredentials(): Boolean

    /**
     * Performs hairstyle transformation with strict identity preservation.
     */
    suspend fun transformHairstyle(
        context: Context,
        userPhoto: UserPhotoSource,
        hairstyle: HairstyleItem,
        colorTint: String?
    ): AiTryOnResult
}
