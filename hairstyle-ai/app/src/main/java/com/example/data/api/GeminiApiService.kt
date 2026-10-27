package com.example.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface GeminiApiService {

    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateHairstyleImage(
        @Path("model") model: String,
        // Sent as a header so the key never appears in URLs, logs or proxies
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GeminiGenerateRequest
    ): Response<GeminiGenerateResponse>
}
