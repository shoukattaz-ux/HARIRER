package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.api.AiHairstyleService
import com.example.data.api.AiImageEditService
import com.example.data.api.AiTryOnConfig
import com.example.data.api.GeminiApiService
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiGenerateRequest
import com.example.data.api.GeminiGenerationConfig
import com.example.data.api.GeminiInlineData
import com.example.data.api.GeminiPart
import com.example.data.api.HairstylePromptBuilder
import com.example.data.model.HairstyleItem
import com.example.data.model.TryOnError
import com.example.data.model.UserPhotoSource
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

sealed class AiTryOnResult {
    data class Success(
        val resultBitmap: Bitmap,
        val isAiGenerated: Boolean,
        val note: String = "AI Hairstyle Preview"
    ) : AiTryOnResult()

    data class Error(val error: TryOnError, val detailedMessage: String? = null) : AiTryOnResult()
}

/**
 * Concrete implementation of AiHairstyleService backed by Google Gemini API.
 * Configured for Free Tier development and testing with strict cost protection safeguards:
 * - No automatic repeated retries on rate limits or quota depletion
 * - Clean error mapping according to Free Tier specifications
 * - Modular design enabling future transition to production backends
 */
object GeminiTryOnRepository : AiHairstyleService, AiImageEditService {

    private const val TAG = "GeminiTryOn"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    override val currentModelName: String
        get() = AiTryOnConfig.PRIMARY_MODEL

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val apiService: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    override fun hasValidCredentials(): Boolean = hasValidApiKey()

    /**
     * Checks if a non-placeholder Gemini API key is configured in BuildConfig.
     */
    fun hasValidApiKey(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY"
    }

    /**
     * Executes real AI hairstyle transformation with Google Gemini Image models.
     * Controlled execution: No repeated retry loops on failures or rate limits.
     */
    override suspend fun transformHairstyle(
        context: Context,
        userPhoto: UserPhotoSource,
        hairstyle: HairstyleItem,
        colorTint: String?
    ): AiTryOnResult {
        // Build detailed hairstyle instruction prompt preserving identity & facial features
        val prompt = HairstylePromptBuilder.buildTransformationPrompt(hairstyle, colorTint)
        return editImage(context, userPhoto, prompt)
    }

    /**
     * Sends one photo plus an instruction prompt to the Gemini image model and returns the edited image.
     * Shared by hairstyle try-on and Style Score lifestyle scenes; same single-request, no-retry rules.
     */
    override suspend fun editImage(
        context: Context,
        photo: UserPhotoSource,
        prompt: String
    ): AiTryOnResult = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        // 1. Validate API Key
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "No valid Gemini API key configured.")
            return@withContext AiTryOnResult.Error(
                TryOnError.MissingApiKey,
                "Gemini API key is required. Please add your GEMINI_API_KEY in the AI Studio Secrets panel."
            )
        }

        // 2. Decode & normalize user photo
        val photoBitmap = resolveToBitmap(context, photo)
        if (photoBitmap == null) {
            return@withContext AiTryOnResult.Error(
                TryOnError.InvalidImageFormat,
                "Please choose a clear face photo and try again."
            )
        }

        val base64Jpeg = bitmapToBase64(photoBitmap)
        if (base64Jpeg.isEmpty()) {
            return@withContext AiTryOnResult.Error(
                TryOnError.PhotoUploadFailed,
                "Please choose a clear face photo and try again."
            )
        }

        val request = GeminiGenerateRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = prompt),
                        GeminiPart(inlineData = GeminiInlineData(mimeType = "image/jpeg", data = base64Jpeg))
                    )
                )
            ),
            generationConfig = GeminiGenerationConfig(
                responseModalities = listOf("TEXT", "IMAGE"),
                temperature = 0.35f
            )
        )

        // Single controlled request to primary model (gemini-2.5-flash-image)
        try {
            Log.d(TAG, "Executing single AI hairstyle request with $currentModelName")
            delay(1500)
            val response = apiService.generateHairstyleImage(
                model = currentModelName,
                apiKey = apiKey,
                request = request
            )

            if (response.isSuccessful) {
                val body = response.body()

                // Check for safety blocks
                val safetyBlock = body?.promptFeedback?.blockReason
                if (safetyBlock != null) {
                    Log.w(TAG, "Prompt blocked by safety filter: $safetyBlock")
                    return@withContext AiTryOnResult.Error(
                        TryOnError.AiGenerationFailed,
                        "Please choose a clear face photo and try again."
                    )
                }

                val candidate = body?.candidates?.firstOrNull()
                if (candidate?.finishReason == "SAFETY") {
                    return@withContext AiTryOnResult.Error(
                        TryOnError.AiGenerationFailed,
                        "Please choose a clear face photo and try again."
                    )
                }

                val parts = candidate?.content?.parts ?: emptyList()

                // Look for generated image part
                val imagePart = parts.firstOrNull { it.resolvedInlineData != null }
                val inlineData = imagePart?.resolvedInlineData
                if (inlineData != null && inlineData.data.isNotEmpty()) {
                    val imageBytes = Base64.decode(inlineData.data, Base64.DEFAULT)
                    val generatedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    if (generatedBitmap != null) {
                        Log.d(TAG, "Successfully generated AI hairstyle image (${generatedBitmap.width}x${generatedBitmap.height})")
                        return@withContext AiTryOnResult.Success(
                            resultBitmap = generatedBitmap,
                            isAiGenerated = true,
                            note = "AI Hairstyle Preview"
                        )
                    }
                }

                // If model returned text commentary instead of an image
                val textPart = parts.firstOrNull { it.text != null }?.text
                Log.w(TAG, "Model returned text response instead of image: $textPart")
                return@withContext AiTryOnResult.Error(
                    TryOnError.AiGenerationFailed,
                    "Unable to create the preview right now. Please try again."
                )
            } else {
                val code = response.code()
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e(TAG, "API HTTP error code $code: $errorBody")

                // Handle rate limit and quota errors without automated repeated retries
                when (code) {
                    429 -> return@withContext AiTryOnResult.Error(
                        TryOnError.RateLimitExceeded,
                        "Free AI preview limit reached. Please try again later."
                    )
                    401, 403 -> return@withContext AiTryOnResult.Error(
                        TryOnError.MissingApiKey,
                        "Gemini API authorization failed ($code). Please check your GEMINI_API_KEY in the Secrets panel."
                    )
                    else -> return@withContext AiTryOnResult.Error(
                        TryOnError.ProcessingFailed,
                        "Unable to create the preview right now. Please try again."
                    )
                }
            }
        } catch (e: UnknownHostException) {
            Log.e(TAG, "Network connection failure", e)
            return@withContext AiTryOnResult.Error(
                TryOnError.NetworkFailure,
                "Connection problem. Please check your internet and try again."
            )
        } catch (e: SocketTimeoutException) {
            Log.e(TAG, "Network timeout during image generation", e)
            return@withContext AiTryOnResult.Error(
                TryOnError.NetworkFailure,
                "Connection problem. Please check your internet and try again."
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during AI transformation", e)
            return@withContext AiTryOnResult.Error(
                TryOnError.ProcessingFailed,
                "Unable to create the preview right now. Please try again."
            )
        }
    }

    private fun resolveToBitmap(context: Context, source: UserPhotoSource): Bitmap? {
        return try {
            when (source) {
                is UserPhotoSource.Captured -> scaleDownIfNeeded(source.bitmap)
                is UserPhotoSource.SelectedUri -> {
                    val stream: InputStream? = context.contentResolver.openInputStream(source.uri)
                    val original = BitmapFactory.decodeStream(stream)
                    stream?.close()
                    if (original != null) {
                        val corrected = correctExifOrientation(context, source.uri, original)
                        scaleDownIfNeeded(corrected)
                    } else null
                }
                is UserPhotoSource.PresetModel -> {
                    val original = BitmapFactory.decodeResource(context.resources, source.drawableRes)
                    original?.let { scaleDownIfNeeded(it) }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed resolving photo to bitmap", e)
            null
        }
    }

    private fun correctExifOrientation(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val exif = ExifInterface(stream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
                val matrix = Matrix()
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
                    else -> return bitmap
                }
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } ?: bitmap
        } catch (e: Exception) {
            bitmap
        }
    }

    private fun scaleDownIfNeeded(bitmap: Bitmap, maxDim: Int = 1024): Bitmap {
        if (bitmap.width <= maxDim && bitmap.height <= maxDim) return bitmap
        val ratio = maxDim.toFloat() / Math.max(bitmap.width, bitmap.height)
        val newWidth = (bitmap.width * ratio).toInt()
        val newHeight = (bitmap.height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
