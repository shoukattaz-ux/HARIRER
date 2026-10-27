package com.example.data.api

import android.content.Context
import com.example.data.model.UserPhotoSource
import com.example.data.repository.AiTryOnResult

/**
 * Generic AI image editing: one photo plus an instruction prompt in, one edited image out.
 * Used for Style Score lifestyle scenes.
 */
interface AiImageEditService {

    suspend fun editImage(
        context: Context,
        photo: UserPhotoSource,
        prompt: String
    ): AiTryOnResult
}
