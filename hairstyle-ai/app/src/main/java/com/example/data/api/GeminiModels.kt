package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiGenerateRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "generationConfig") val generationConfig: GeminiGenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>,
    @Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: GeminiInlineData? = null,
    @Json(name = "inline_data") val inlineDataSnake: GeminiInlineData? = null
) {
    val resolvedInlineData: GeminiInlineData?
        get() = inlineData ?: inlineDataSnake
}

@JsonClass(generateAdapter = true)
data class GeminiInlineData(
    @Json(name = "mimeType") val mimeType: String? = "image/jpeg",
    @Json(name = "mime_type") val mimeTypeSnake: String? = null,
    @Json(name = "data") val data: String
) {
    val resolvedMimeType: String
        get() = mimeType ?: mimeTypeSnake ?: "image/jpeg"
}

@JsonClass(generateAdapter = true)
data class GeminiGenerationConfig(
    @Json(name = "responseModalities") val responseModalities: List<String>? = listOf("TEXT", "IMAGE"),
    @Json(name = "temperature") val temperature: Float? = 0.35f
)

@JsonClass(generateAdapter = true)
data class GeminiGenerateResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null,
    @Json(name = "promptFeedback") val promptFeedback: GeminiPromptFeedback? = null,
    @Json(name = "error") val error: GeminiApiError? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null,
    @Json(name = "finishReason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiPromptFeedback(
    @Json(name = "blockReason") val blockReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiApiError(
    @Json(name = "code") val code: Int? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "status") val status: String? = null
)
