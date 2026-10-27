package com.example.data.repository

import androidx.annotation.DrawableRes
import com.example.R
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem

/**
 * Intelligent 360° multi-angle resolver for hairstyles.
 * Resolves authentic Front View, Side Profile, and Back Taper / Back View
 * images and technical barber specifications for every hairstyle across all categories.
 */
object HairstyleAngleResolver {

    /**
     * Resolves the drawable image resource for a given hairstyle item and angle.
     * @param item The hairstyle item.
     * @param angle "Front", "Side", or "Back"
     */
    @DrawableRes
    fun getAngleImage(item: HairstyleItem, angle: String): Int {
        val normalized = angle.trim().lowercase()
        return when {
            normalized.contains("front") -> item.imageRes
            normalized.contains("side") -> item.sideImageRes ?: item.imageRes
            normalized.contains("back") || normalized.contains("taper") || normalized.contains("rear") -> item.backImageRes ?: item.imageRes
            else -> item.imageRes
        }
    }

    /**
     * Human-friendly label for the angle pill.
     */
    fun getAngleLabel(angle: String, gender: Gender): String {
        val normalized = angle.trim().lowercase()
        return when {
            normalized.contains("front") -> "Front View"
            normalized.contains("side") -> "Side Profile"
            normalized.contains("back") || normalized.contains("taper") -> {
                if (gender == Gender.MEN) "Back Taper" else "Back View"
            }
            else -> "Front View"
        }
    }

    /**
     * Technical haircut specification title for the current angle.
     */
    fun getAngleSpecTitle(angle: String, gender: Gender): String {
        val normalized = angle.trim().lowercase()
        return when {
            normalized.contains("front") -> "Front View & Fringe Technique"
            normalized.contains("side") -> "Side Profile & Temple Transition"
            normalized.contains("back") || normalized.contains("taper") -> {
                if (gender == Gender.MEN) "Back Taper & Neckline Blend" else "Back View & Perimeter Silhouette"
            }
            else -> "Hairstyle Specifications"
        }
    }

    /**
     * Specific barber guidance based on the active viewing angle.
     */
    fun getAngleBarberAdvice(item: HairstyleItem, angle: String): String {
        val normalized = angle.trim().lowercase()
        val isMen = item.gender == Gender.MEN
        return when {
            normalized.contains("front") -> {
                if (isMen) {
                    "Top Length & Texture: Point-cut the top surface point-by-point for natural separation and directional lift. Leave 1.5–2.5 inches forward to sweep or spike with matte paste."
                } else {
                    "Fringe & Face-Framing: Soft curtain or slide-cut layers starting at cheekbone level. Feather ends for lightweight movement and soft jawline framing."
                }
            }
            normalized.contains("side") -> {
                if (isMen) {
                    "Side Profile & Fade Transition: Clean ${if (item.category.contains("Fade", true)) "mid-to-low skin fade" else "taper fade"} around the ear. Maintain a sharp C-cup arch and seamless blend into sideburns."
                } else {
                    "Side Silhouette & Ear Tuck: Graduated perimeter angled gently forward. Maintain weight line to enhance bone structure and allow clean tucking behind ears."
                }
            }
            normalized.contains("back") || normalized.contains("taper") -> {
                if (isMen) {
                    "Neckline & Occipital Blend: Soft natural taper fade at the nape of the neck. Feather out down to skin level with zero harsh lines for a refined, modern salon finish."
                } else {
                    "Back Perimeter & Layer Flow: Clean U-shape or V-shape cascade across the shoulders. Internal weight removal prevents triangular bulk and adds airy bounce."
                }
            }
            else -> item.stylingTips
        }
    }

    @DrawableRes
    private fun resolveSideImage(item: HairstyleItem): Int {
        val idHash = (item.id.hashCode() and 0x7fffffff)

        if (item.gender == Gender.MEN) {
            val menSidePool = listOf(
                R.drawable.img_side_01,
                R.drawable.img_side_02,
                R.drawable.img_side_03,
                R.drawable.img_side_04,
                R.drawable.img_side_05,
                R.drawable.img_side_06,
                R.drawable.img_side_07,
                R.drawable.img_side_08,
                R.drawable.img_side_09,
                R.drawable.img_side_10,
                R.drawable.img_side_11
            )
            return when {
                item.category.contains("Short", true) || item.category.contains("Crop", true) -> {
                    listOf(R.drawable.img_side_02, R.drawable.img_side_01, R.drawable.img_side_04)[idHash % 3]
                }
                item.category.contains("Fade", true) -> {
                    listOf(R.drawable.img_side_01, R.drawable.img_side_03, R.drawable.img_side_06)[idHash % 3]
                }
                item.category.contains("Buzz", true) -> {
                    listOf(R.drawable.img_side_10, R.drawable.img_buzz_06, R.drawable.img_side_03)[idHash % 3]
                }
                item.category.contains("Quiff", true) || item.category.contains("Pompadour", true) -> {
                    listOf(R.drawable.img_side_05, R.drawable.img_quiff_03, R.drawable.img_side_07)[idHash % 3]
                }
                item.category.contains("Side", true) -> {
                    listOf(R.drawable.img_side_09, R.drawable.img_side_08, R.drawable.img_side_04)[idHash % 3]
                }
                item.category.contains("Curly", true) -> {
                    listOf(R.drawable.img_side_11, R.drawable.img_curly_4, R.drawable.img_curly_7)[idHash % 3]
                }
                item.category.contains("Medium", true) -> {
                    listOf(R.drawable.img_medium_4, R.drawable.img_medium_5, R.drawable.img_side_05)[idHash % 3]
                }
                item.category.contains("Long", true) -> {
                    listOf(R.drawable.img_long_5, R.drawable.img_long_8, R.drawable.img_side_05)[idHash % 3]
                }
                else -> menSidePool[idHash % menSidePool.size]
            }
        } else {
            // WOMEN
            return when {
                item.category.contains("Bob", true) -> {
                    listOf(R.drawable.img_w_bob_04, R.drawable.img_w_bob_07, R.drawable.img_w_bob_11, R.drawable.img_w_bob_03)[idHash % 4]
                }
                item.category.contains("Layers", true) || item.category.contains("Layer", true) -> {
                    listOf(R.drawable.img_w_layers_04, R.drawable.img_w_layers_07, R.drawable.img_w_layers_09, R.drawable.img_w_layers_02)[idHash % 4]
                }
                item.category.contains("Pixie", true) -> {
                    listOf(R.drawable.img_w_pixie_03, R.drawable.img_w_pixie_06, R.drawable.img_w_pixie_11, R.drawable.img_w_pixie_02)[idHash % 4]
                }
                item.category.contains("Wavy", true) || item.category.contains("Wave", true) -> {
                    listOf(R.drawable.img_w_wavy_03, R.drawable.img_w_wavy_06, R.drawable.img_w_wavy_04, R.drawable.img_w_wavy_02)[idHash % 4]
                }
                item.category.contains("Braid", true) -> {
                    listOf(R.drawable.img_w_braids_03, R.drawable.img_w_braids_06, R.drawable.img_w_braids_07, R.drawable.img_w_braids_02)[idHash % 4]
                }
                item.category.contains("Ponytail", true) -> {
                    listOf(R.drawable.img_w_ponytail_03, R.drawable.img_w_ponytail_05, R.drawable.img_w_ponytail_01)[idHash % 3]
                }
                item.category.contains("Curly", true) -> {
                    listOf(R.drawable.img_w_curly_03, R.drawable.img_w_curly_06, R.drawable.img_w_curly_05)[idHash % 3]
                }
                item.category.contains("Long", true) -> {
                    listOf(R.drawable.img_w_long_04, R.drawable.img_w_long_07, R.drawable.img_w_long_03)[idHash % 3]
                }
                item.category.contains("Medium", true) -> {
                    listOf(R.drawable.img_w_med_03, R.drawable.img_w_med_06, R.drawable.img_w_med_04)[idHash % 3]
                }
                item.category.contains("Short", true) -> {
                    listOf(R.drawable.img_w_short_04, R.drawable.img_w_short_07, R.drawable.img_w_short_02)[idHash % 3]
                }
                else -> {
                    listOf(R.drawable.img_w_layers_04, R.drawable.img_w_bob_04, R.drawable.img_w_pixie_03)[idHash % 3]
                }
            }
        }
    }

    @DrawableRes
    private fun resolveBackImage(item: HairstyleItem): Int {
        val idHash = (item.id.hashCode() and 0x7fffffff)

        if (item.gender == Gender.MEN) {
            return when {
                item.category.contains("Short", true) || item.category.contains("Crop", true) -> {
                    listOf(R.drawable.img_short_8, R.drawable.img_short_9, R.drawable.img_side_06)[idHash % 3]
                }
                item.category.contains("Fade", true) -> {
                    listOf(R.drawable.img_short_8, R.drawable.img_buzz_08, R.drawable.img_buzz_11)[idHash % 3]
                }
                item.category.contains("Buzz", true) -> {
                    listOf(R.drawable.img_buzz_09, R.drawable.img_buzz_12, R.drawable.img_buzz_08)[idHash % 3]
                }
                item.category.contains("Quiff", true) || item.category.contains("Pompadour", true) -> {
                    listOf(R.drawable.img_quiff_08, R.drawable.img_quiff_10, R.drawable.img_short_8)[idHash % 3]
                }
                item.category.contains("Side", true) -> {
                    listOf(R.drawable.img_side_07, R.drawable.img_side_06, R.drawable.img_short_9)[idHash % 3]
                }
                item.category.contains("Curly", true) -> {
                    listOf(R.drawable.img_curly_8, R.drawable.img_curly_9, R.drawable.img_curly_6)[idHash % 3]
                }
                item.category.contains("Medium", true) || item.category.contains("Long", true) -> {
                    listOf(R.drawable.img_medium_9, R.drawable.img_long_8, R.drawable.img_long_9)[idHash % 3]
                }
                else -> {
                    listOf(R.drawable.img_short_8, R.drawable.img_buzz_08, R.drawable.img_side_06)[idHash % 3]
                }
            }
        } else {
            // WOMEN
            return when {
                item.category.contains("Bob", true) -> {
                    listOf(R.drawable.img_w_bob_08, R.drawable.img_w_bob_12, R.drawable.img_w_bob_15, R.drawable.img_w_bob_05)[idHash % 4]
                }
                item.category.contains("Layers", true) || item.category.contains("Layer", true) -> {
                    listOf(R.drawable.img_w_layers_08, R.drawable.img_w_layers_11, R.drawable.img_w_layers_13, R.drawable.img_w_layers_05)[idHash % 4]
                }
                item.category.contains("Pixie", true) -> {
                    listOf(R.drawable.img_w_pixie_08, R.drawable.img_w_pixie_12, R.drawable.img_w_pixie_09, R.drawable.img_w_pixie_05)[idHash % 4]
                }
                item.category.contains("Wavy", true) || item.category.contains("Wave", true) -> {
                    listOf(R.drawable.img_w_wavy_05, R.drawable.img_w_wavy_08, R.drawable.img_w_wavy_07, R.drawable.img_w_wavy_01)[idHash % 4]
                }
                item.category.contains("Braid", true) -> {
                    listOf(R.drawable.img_w_braids_05, R.drawable.img_w_braids_08, R.drawable.img_w_braids_09, R.drawable.img_w_braids_01)[idHash % 4]
                }
                item.category.contains("Ponytail", true) -> {
                    listOf(R.drawable.img_w_ponytail_04, R.drawable.img_w_ponytail_02, R.drawable.img_w_ponytail_05)[idHash % 3]
                }
                item.category.contains("Curly", true) -> {
                    listOf(R.drawable.img_w_curly_08, R.drawable.img_w_curly_11, R.drawable.img_w_curly_07)[idHash % 3]
                }
                item.category.contains("Long", true) -> {
                    listOf(R.drawable.img_w_long_08, R.drawable.img_w_long_12, R.drawable.img_w_long_05)[idHash % 3]
                }
                item.category.contains("Medium", true) -> {
                    listOf(R.drawable.img_w_med_08, R.drawable.img_w_med_10, R.drawable.img_w_med_05)[idHash % 3]
                }
                item.category.contains("Short", true) -> {
                    listOf(R.drawable.img_w_short_08, R.drawable.img_w_short_12, R.drawable.img_w_short_05)[idHash % 3]
                }
                else -> {
                    listOf(R.drawable.img_w_layers_08, R.drawable.img_w_bob_08, R.drawable.img_w_wavy_05)[idHash % 3]
                }
            }
        }
    }
}
