package com.example.data.model

import android.graphics.Bitmap
import android.net.Uri
import androidx.annotation.DrawableRes
import com.example.R

sealed class UserPhotoSource {
    data class Captured(val bitmap: Bitmap) : UserPhotoSource()
    data class SelectedUri(val uri: Uri) : UserPhotoSource()
    data class PresetModel(@DrawableRes val drawableRes: Int, val name: String) : UserPhotoSource()
}

sealed class TryOnError {
    object CameraPermissionDenied : TryOnError()
    object CameraUnavailable : TryOnError()
    object PhotoUploadFailed : TryOnError()
    object InvalidImageFormat : TryOnError()
    object UnsupportedImage : TryOnError()
    object ProcessingFailed : TryOnError()
    object NoPhotoSelected : TryOnError()
    object MissingApiKey : TryOnError()
    object QuotaExceeded : TryOnError()
    object RateLimitExceeded : TryOnError()
    object NetworkFailure : TryOnError()
    object AiGenerationFailed : TryOnError()

    val title: String
        get() = when (this) {
            CameraPermissionDenied -> "Camera Permission Needed"
            CameraUnavailable -> "Camera Not Available"
            PhotoUploadFailed -> "Photo Upload Failed"
            InvalidImageFormat -> "Invalid Image"
            UnsupportedImage -> "Unsupported Image"
            ProcessingFailed -> "Generation Error"
            NoPhotoSelected -> "No Photo Selected"
            MissingApiKey -> "API Configuration Required"
            QuotaExceeded -> "Free Limit Reached"
            RateLimitExceeded -> "Free Limit Reached"
            NetworkFailure -> "Network Problem"
            AiGenerationFailed -> "Preview Unavailable"
        }

    val message: String
        get() = when (this) {
            CameraPermissionDenied -> "HairStyle AI needs camera access so you can take a selfie and try on hairstyles."
            CameraUnavailable -> "Unable to connect to the camera on this device. You can upload a photo from your gallery instead."
            PhotoUploadFailed -> "Please choose a clear face photo and try again."
            InvalidImageFormat -> "Please choose a clear face photo and try again."
            UnsupportedImage -> "Please choose a clear face photo and try again."
            ProcessingFailed -> "Unable to create the preview right now. Please try again."
            NoPhotoSelected -> "Please take a photo or select an image from your device to continue."
            MissingApiKey -> "Gemini API key is required. Please configure GEMINI_API_KEY in the AI Studio Secrets panel."
            QuotaExceeded -> "Free AI preview limit has been reached. Please try again later."
            RateLimitExceeded -> "Free AI preview limit reached. Please try again later."
            NetworkFailure -> "Connection problem. Please check your internet and try again."
            AiGenerationFailed -> "Unable to create the preview right now. Please try again."
        }
}

enum class ProcessingStage(val description: String) {
    ANALYZING_FACE("Analyzing facial geometry & head shape..."),
    MAPPING_HAIRLINE("Mapping natural hairline & forehead bounds..."),
    SCULPTING_VOLUME("Sculpting hairstyle volume & strand direction..."),
    LIGHTING_BLEND("Harmonizing salon highlights & ambient lighting..."),
    AI_RENDERING("AI generating photorealistic transformation..."),
    FINALIZING("Finalizing hairstyle transformation...")
}
