package com.example.analysis

import com.example.data.model.HairstyleItem
import kotlin.math.roundToInt

/**
 * Computes the "Style Score" for a hairstyle on the current user.
 *
 * The score is deterministic and explainable (no random numbers): it blends how well the cut suits
 * the detected face shape, how well the chosen hair color suits the skin undertone, how popular the
 * style is, and how easy it is to maintain. The result is mapped onto a 70–99 scale.
 */
object StyleScoreCalculator {

    const val MIN_SCORE = 70
    const val MAX_SCORE = 99

    private const val WEIGHT_FACE_SHAPE = 0.45f
    private const val WEIGHT_COLOR = 0.25f
    private const val WEIGHT_TREND = 0.20f
    private const val WEIGHT_MAINTENANCE = 0.10f

    private val warmColorWords = listOf("golden", "honey", "copper", "caramel", "auburn", "strawberry", "chestnut", "mahogany", "warm")
    private val coolColorWords = listOf("ash", "platinum", "icy", "silver", "grey", "gray", "burgundy", "plum", "blue", "cool")
    private val naturalColorNames = listOf("natural black", "natural", "black")

    data class ScoreFactor(
        val label: String,
        /** 0..1 */
        val value: Float,
        val detail: String
    )

    data class StyleScore(
        val score: Int,
        val headline: String,
        val factors: List<ScoreFactor>
    )

    fun calculate(
        style: HairstyleItem,
        faceShape: String?,
        undertone: HairColorAdvisor.Undertone?,
        colorTint: String?
    ): StyleScore {
        val faceFit = faceShapeFactor(style, faceShape)
        val color = colorFactor(undertone, colorTint)
        val trend = trendFactor(style)
        val maintenance = maintenanceFactor(style)

        val raw = faceFit.value * WEIGHT_FACE_SHAPE +
                color.value * WEIGHT_COLOR +
                trend.value * WEIGHT_TREND +
                maintenance.value * WEIGHT_MAINTENANCE
        val score = (MIN_SCORE + raw.coerceIn(0f, 1f) * (MAX_SCORE - MIN_SCORE)).roundToInt()

        return StyleScore(
            score = score,
            headline = headline(score),
            factors = listOf(faceFit, color, trend, maintenance)
        )
    }

    fun headline(score: Int): String = when {
        score >= 95 -> "Iconic look"
        score >= 90 -> "Stunning match"
        score >= 85 -> "Great match"
        score >= 80 -> "Good match"
        else -> "Bold choice"
    }

    private fun faceShapeFactor(style: HairstyleItem, faceShape: String?): ScoreFactor {
        if (faceShape.isNullOrBlank()) {
            return ScoreFactor("Face shape fit", 0.75f, "Run an AI Face Scan for a personal fit score")
        }
        val fits = style.faceShapes.any { it.equals(faceShape, ignoreCase = true) }
        return if (fits) {
            ScoreFactor("Face shape fit", 1f, "Made for $faceShape faces")
        } else {
            ScoreFactor("Face shape fit", 0.55f, "Not a classic pick for $faceShape faces, but it can work")
        }
    }

    private fun colorFactor(undertone: HairColorAdvisor.Undertone?, colorTint: String?): ScoreFactor {
        val tint = colorTint?.trim().orEmpty()
        val tintLower = tint.lowercase()
        if (tint.isEmpty() || naturalColorNames.any { tintLower == it }) {
            return ScoreFactor("Color harmony", 0.95f, "Natural color always looks authentic")
        }
        if (undertone == null) {
            return ScoreFactor("Color harmony", 0.85f, "$tint looks great on most skin tones")
        }
        val tintTone = when {
            warmColorWords.any { tintLower.contains(it) } -> HairColorAdvisor.Undertone.WARM
            coolColorWords.any { tintLower.contains(it) } -> HairColorAdvisor.Undertone.COOL
            else -> HairColorAdvisor.Undertone.NEUTRAL
        }
        return when {
            tintTone == undertone -> ScoreFactor("Color harmony", 1f, "$tint flatters your ${undertone.displayName.lowercase()} undertone")
            tintTone == HairColorAdvisor.Undertone.NEUTRAL || undertone == HairColorAdvisor.Undertone.NEUTRAL ->
                ScoreFactor("Color harmony", 0.85f, "$tint suits your ${undertone.displayName.lowercase()} undertone")
            else -> ScoreFactor("Color harmony", 0.6f, "$tint is bold for a ${undertone.displayName.lowercase()} undertone")
        }
    }

    private fun trendFactor(style: HairstyleItem): ScoreFactor {
        val ratingPart = ((style.rating - 4f) / 1f).coerceIn(0f, 1f) * 0.7f
        val value = (ratingPart + (if (style.isTrending) 0.2f else 0f) + (if (style.isPopular) 0.1f else 0f))
            .coerceIn(0f, 1f)
        val detail = when {
            style.isTrending -> "Trending now · rated ${"%.1f".format(style.rating)}"
            style.isPopular -> "Popular pick · rated ${"%.1f".format(style.rating)}"
            else -> "Rated ${"%.1f".format(style.rating)}"
        }
        return ScoreFactor("Trend factor", value, detail)
    }

    private fun maintenanceFactor(style: HairstyleItem): ScoreFactor = when (style.maintenance.lowercase()) {
        "low" -> ScoreFactor("Easy upkeep", 1f, "Low maintenance")
        "medium" -> ScoreFactor("Easy upkeep", 0.85f, "Medium maintenance")
        else -> ScoreFactor("Easy upkeep", 0.7f, "High maintenance")
    }
}
