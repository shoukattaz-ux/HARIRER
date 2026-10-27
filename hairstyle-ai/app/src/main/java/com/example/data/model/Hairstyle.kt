package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

enum class Gender(val displayName: String) {
    MEN("Men"),
    WOMEN("Women")
}

data class HairstyleCategory(
    val id: String,
    val name: String,
    val gender: Gender,
    val styleCount: Int = 0
)

data class HairstyleVariation(
    val id: String,
    val name: String,
    val hairLength: String,
    val hairTexture: String,
    val maintenance: String,
    val stylingDifficulty: String,
    val description: String,
    val stylingTips: String,
    val faceShapes: List<String>,
    val fadeLevel: String,
    @DrawableRes val imageRes: Int,
    @DrawableRes val sideImageRes: Int? = null,
    @DrawableRes val backImageRes: Int? = null,
    val overlayAssetId: String? = null
)

data class HairstyleItem(
    val id: String,
    val name: String,
    val category: String,
    val gender: Gender,
    val description: String,
    val stylingTips: String,
    val faceShapes: List<String>,
    val hairLength: String, // "Short", "Medium", "Long"
    val maintenance: String, // "Low", "Medium", "High"
    @DrawableRes val imageRes: Int = R.drawable.img_hero_hairstyles,
    @DrawableRes val sideImageRes: Int? = null,
    @DrawableRes val backImageRes: Int? = null,
    val rating: Float = 4.8f,
    val tryOnCount: String = "15.2k",
    val isTrending: Boolean = false,
    val isPopular: Boolean = false,
    val tags: List<String> = emptyList(),
    val variations: List<HairstyleVariation> = emptyList(),
    val overlayAssetId: String? = null
)

fun HairstyleItem.toVariationItem(variation: HairstyleVariation): HairstyleItem {
    return this.copy(
        id = "${this.id}_${variation.id}",
        name = variation.name,
        hairLength = variation.hairLength,
        maintenance = variation.maintenance,
        description = variation.description,
        stylingTips = variation.stylingTips,
        faceShapes = variation.faceShapes,
        imageRes = variation.imageRes,
        sideImageRes = variation.sideImageRes,
        backImageRes = variation.backImageRes,
        tags = this.tags + listOf(variation.hairTexture, variation.hairLength, variation.fadeLevel)
    )
}

val HairstyleItem.hairTexture: String
    get() = when {
        tags.any { it.contains("Curl", ignoreCase = true) } || name.contains("Curl", ignoreCase = true) -> "Curly / Textured"
        tags.any { it.contains("Wave", ignoreCase = true) } || name.contains("Wave", ignoreCase = true) -> "Wavy / Tousled"
        tags.any { it.contains("Fade", ignoreCase = true) } || tags.any { it.contains("Crop", ignoreCase = true) } -> "Straight to Textured"
        hairLength.equals("Long", ignoreCase = true) -> "Natural / Flowing"
        else -> "Straight to Wavy"
    }

val HairstyleItem.stylingDifficulty: String
    get() = when (maintenance) {
        "Low" -> "Easy"
        "Medium" -> "Medium"
        "High" -> "Advanced"
        else -> "Medium"
    }

data class TryOnHistoryItem(
    val id: String,
    val hairstyleName: String,
    val category: String,
    val date: String,
    val matchScore: Int,
    @DrawableRes val imageRes: Int
)

data class UserHairProfile(
    val userName: String = "HairStyle Studio",
    val memberSince: String = "October 2026",
    val faceShape: String = "Oval",
    val hairType: String = "Wavy",
    val hairLength: String = "Medium",
    val primaryGoal: String = "Modern & Low Maintenance"
)
