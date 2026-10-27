package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Dedicated REST API Data Models for Paid AI Hairstyle Try-On.
 * Completely independent and decoupled from Google Gemini.
 */
@JsonClass(generateAdapter = true)
data class PaidApiTryOnRequest(
    @Json(name = "user_image_base64") val userImageBase64: String,
    @Json(name = "hairstyle_id") val hairstyleId: String,
    @Json(name = "hairstyle_name") val hairstyleName: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "hair_color") val hairColor: String? = "natural",
    @Json(name = "hairline_offset_y") val hairlineOffsetY: Float = 0f,
    @Json(name = "hair_volume_scale") val hairVolumeScale: Float = 1f,
    @Json(name = "preserve_face_identity") val preserveFaceIdentity: Boolean = true,
    @Json(name = "output_format") val outputFormat: String = "png"
)

@JsonClass(generateAdapter = true)
data class PaidApiTryOnResponse(
    @Json(name = "status") val status: String? = "success",
    @Json(name = "result_image_base64") val resultImageBase64: String? = null,
    @Json(name = "result_image_url") val resultImageUrl: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "processing_time_ms") val processingTimeMs: Long? = 540L,
    @Json(name = "engine_version") val engineVersion: String? = "HairAI-REST-API-v2.6"
)

@JsonClass(generateAdapter = true)
data class PaidApiHealthResponse(
    @Json(name = "status") val status: String? = "ok",
    @Json(name = "service") val service: String? = "HairAI Paid REST API Service",
    @Json(name = "version") val version: String? = "2.6.0"
)

/**
 * Retrofit Interface for invoking external Paid AI Hairstyle API endpoints.
 */
interface PaidHairstyleRetrofitApi {

    @POST
    suspend fun generateHairstyle(
        @Url endpointUrl: String,
        @Header("Authorization") authorization: String?,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: PaidApiTryOnRequest
    ): Response<PaidApiTryOnResponse>

    @POST
    suspend fun pingHealth(
        @Url endpointUrl: String,
        @Header("Authorization") authorization: String?
    ): Response<PaidApiHealthResponse>
}
