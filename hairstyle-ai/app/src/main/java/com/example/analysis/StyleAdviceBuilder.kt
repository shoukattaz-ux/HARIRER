package com.example.analysis

import com.example.data.model.Gender
import com.example.data.model.HairstyleItem

/**
 * Combines face shape analysis, the hairstyle catalog and hair color analysis into a single
 * "Personalized Style Advice" report (Hair Analysis + Color Analysis).
 */
object StyleAdviceBuilder {

    const val RECOMMENDATION_COUNT = 5

    data class StyleAdviceReport(
        val faceShape: String,
        val confidencePercent: Int,
        val gender: Gender,
        val recommendedStyles: List<HairstyleItem>,
        val hairTips: List<String>,
        val colorAnalysis: HairColorAdvisor.ColorAnalysis
    )

    fun build(
        faceAnalysis: FaceShapeAnalyzer.FaceShapeAnalysisResult,
        skinTone: HairColorAdvisor.SkinToneResult?,
        gender: Gender,
        catalog: List<HairstyleItem>
    ): StyleAdviceReport {
        val styles = recommendStyles(
            faceShape = faceAnalysis.detectedShape,
            suitableTypes = faceAnalysis.suitableHairstyleTypes,
            gender = gender,
            catalog = catalog
        )
        val hairTips = buildList {
            addAll(faceAnalysis.complementaryAdvice.take(3))
            if (styles.isNotEmpty()) {
                add("Top picks for your ${faceAnalysis.detectedShape.lowercase()} face: " +
                        styles.take(3).joinToString(", ") { it.name } + ".")
            }
        }
        return StyleAdviceReport(
            faceShape = faceAnalysis.detectedShape,
            confidencePercent = faceAnalysis.confidencePercent,
            gender = gender,
            recommendedStyles = styles,
            hairTips = hairTips,
            colorAnalysis = HairColorAdvisor.recommend(skinTone)
        )
    }

    /**
     * Ranks catalog styles for the given face shape: styles tagged for the face shape first,
     * then by how many of the analyzer's suggested style keywords they match, trending and rating.
     * Falls back to the best-rated styles of the same gender if fewer than five match.
     */
    fun recommendStyles(
        faceShape: String,
        suitableTypes: List<String>,
        gender: Gender,
        catalog: List<HairstyleItem>
    ): List<HairstyleItem> {
        val keywords = suitableTypes
            .flatMap { it.lowercase().split(" ", "-") }
            .filter { it.length > 3 }
            .toSet()

        fun keywordHits(item: HairstyleItem): Int {
            val haystack = (listOf(item.name, item.category) + item.tags).joinToString(" ").lowercase()
            return keywords.count { haystack.contains(it) }
        }

        val sameGender = catalog.filter { it.gender == gender }
        val ranked = sameGender
            .filter { item -> item.faceShapes.any { it.equals(faceShape, ignoreCase = true) } }
            .sortedWith(
                compareByDescending<HairstyleItem> { keywordHits(it) }
                    .thenByDescending { it.isTrending }
                    .thenByDescending { it.rating }
            )
        val fallback = sameGender
            .filter { it !in ranked }
            .sortedByDescending { it.rating }

        return (ranked + fallback)
            .distinctBy { it.imageRes }
            .take(RECOMMENDATION_COUNT)
    }
}
