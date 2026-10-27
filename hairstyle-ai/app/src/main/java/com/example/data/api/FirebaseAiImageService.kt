package com.example.data.api

import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.TryOnError
import com.example.data.repository.AiTryOnResult
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FirebaseAIException
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.PromptBlockedException
import com.google.firebase.ai.type.QuotaExceededException
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.ResponseStoppedException
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import java.io.IOException

/**
 * Gemini image editing through Firebase AI Logic.
 * No API key is shipped in the app; Firebase App Check protects the endpoint from abuse.
 */
object FirebaseAiImageService {

    private const val TAG = "FirebaseAiImage"

    suspend fun editImage(photo: Bitmap, prompt: String, modelName: String): AiTryOnResult {
        return try {
            val model = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
                modelName = modelName,
                generationConfig = generationConfig {
                    responseModalities = listOf(ResponseModality.TEXT, ResponseModality.IMAGE)
                    temperature = 0.35f
                }
            )
            val response = model.generateContent(
                content {
                    text(prompt)
                    image(photo)
                }
            )
            val image = response.candidates.firstOrNull()?.content?.parts
                ?.filterIsInstance<ImagePart>()
                ?.firstOrNull()
                ?.image
            if (image != null) {
                AiTryOnResult.Success(resultBitmap = image, isAiGenerated = true, note = "AI Hairstyle Preview")
            } else {
                Log.w(TAG, "Model returned no image part")
                AiTryOnResult.Error(TryOnError.AiGenerationFailed, "Unable to create the preview right now. Please try again.")
            }
        } catch (e: QuotaExceededException) {
            AiTryOnResult.Error(TryOnError.RateLimitExceeded, "AI preview limit reached. Please try again later.")
        } catch (e: PromptBlockedException) {
            AiTryOnResult.Error(TryOnError.AiGenerationFailed, "Please choose a clear face photo and try again.")
        } catch (e: ResponseStoppedException) {
            AiTryOnResult.Error(TryOnError.AiGenerationFailed, "Please choose a clear face photo and try again.")
        } catch (e: FirebaseAIException) {
            Log.e(TAG, "Firebase AI request failed", e)
            AiTryOnResult.Error(TryOnError.ProcessingFailed, "Unable to create the preview right now. Please try again.")
        } catch (e: IOException) {
            AiTryOnResult.Error(TryOnError.NetworkFailure, "Connection problem. Please check your internet and try again.")
        }
    }
}
