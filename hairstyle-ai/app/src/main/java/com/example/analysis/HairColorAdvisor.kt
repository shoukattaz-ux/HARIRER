package com.example.analysis

import android.graphics.Bitmap
import android.graphics.PointF
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.atan2
import kotlin.math.cbrt
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * On-device skin tone & undertone estimation and personalized hair color advice.
 *
 * Samples skin pixels from both cheeks (ML Kit landmarks), converts the trimmed average
 * to CIE L*a*b*, then derives:
 *  - Depth from the Individual Typology Angle (ITA)
 *  - Undertone from the a* and b* hue angle (more yellow = warm, more red/pink = cool)
 *
 * This is a styling estimate only: lighting, white balance and makeup affect the result.
 */
object HairColorAdvisor {

    enum class Undertone(val displayName: String) {
        WARM("Warm"),
        NEUTRAL("Neutral"),
        COOL("Cool")
    }

    enum class SkinDepth(val displayName: String) {
        LIGHT("Light"),
        MEDIUM("Medium"),
        DEEP("Deep")
    }

    data class SkinToneResult(
        val undertone: Undertone,
        val depth: SkinDepth,
        val averageColorArgb: Int,
        val lightness: Float,
        val hueAngle: Float,
        val ita: Float
    )

    data class HairColorSwatch(
        val name: String,
        val argb: Long
    )

    data class ColorAnalysis(
        val skinTone: SkinToneResult?,
        val swatches: List<HairColorSwatch>,
        val tips: List<String>
    ) {
        val summary: String
            get() = if (skinTone != null) {
                "${skinTone.depth.displayName} skin · ${skinTone.undertone.displayName} undertone"
            } else {
                "Universal palette"
            }
    }

    private const val WARM_HUE_MIN = 58f
    private const val COOL_HUE_MAX = 45f
    private const val ITA_LIGHT_MIN = 41f
    private const val ITA_MEDIUM_MIN = 10f

    /**
     * Estimates skin tone from cheek patches of the detected [face] inside [bitmap].
     * Returns null when not enough usable skin pixels are found.
     */
    fun estimateSkinTone(bitmap: Bitmap, face: Face): SkinToneResult? {
        val box = face.boundingBox
        val faceWidth = max(1, box.width())
        val leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK)?.position
            ?: PointF(box.left + faceWidth * 0.30f, box.top + box.height() * 0.62f)
        val rightCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK)?.position
            ?: PointF(box.left + faceWidth * 0.70f, box.top + box.height() * 0.62f)
        val radius = max(2, (faceWidth * 0.05f).roundToInt())

        val samples = ArrayList<Int>()
        for (center in listOf(leftCheek, rightCheek)) {
            samplePatch(bitmap, center, radius, samples)
        }
        if (samples.size < 12) return null

        // Trimmed mean by luminance: drop shadows and specular highlights
        val sorted = samples.sortedBy { luminance(it) }
        val trim = sorted.size / 6
        val kept = sorted.subList(trim, sorted.size - trim)
        var r = 0L
        var g = 0L
        var b = 0L
        for (c in kept) {
            r += (c shr 16) and 0xFF
            g += (c shr 8) and 0xFF
            b += c and 0xFF
        }
        val n = kept.size
        return classify((r / n).toInt(), (g / n).toInt(), (b / n).toInt())
    }

    /**
     * Classifies an average sRGB skin color into depth and undertone.
     */
    fun classify(r: Int, g: Int, b: Int): SkinToneResult {
        val (l, a, bb) = rgbToLab(r, g, b)
        val hue = Math.toDegrees(atan2(bb.toDouble(), a.toDouble())).toFloat()
        val ita = Math.toDegrees(atan2((l - 50f).toDouble(), bb.toDouble())).toFloat()

        val undertone = when {
            hue >= WARM_HUE_MIN -> Undertone.WARM
            hue < COOL_HUE_MAX -> Undertone.COOL
            else -> Undertone.NEUTRAL
        }
        val depth = when {
            ita > ITA_LIGHT_MIN -> SkinDepth.LIGHT
            ita > ITA_MEDIUM_MIN -> SkinDepth.MEDIUM
            else -> SkinDepth.DEEP
        }
        val argb = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        return SkinToneResult(undertone, depth, argb, l, hue, ita)
    }

    /**
     * Builds the five recommended hair color swatches and four advice bullets.
     * When [skinTone] is null a universally flattering neutral palette is returned.
     */
    fun recommend(skinTone: SkinToneResult?): ColorAnalysis {
        val undertone = skinTone?.undertone ?: Undertone.NEUTRAL
        val depth = skinTone?.depth ?: SkinDepth.MEDIUM
        return ColorAnalysis(
            skinTone = skinTone,
            swatches = palette(undertone, depth),
            tips = undertoneTips(undertone) + depthTips(depth)
        )
    }

    private fun palette(undertone: Undertone, depth: SkinDepth): List<HairColorSwatch> = when (undertone) {
        Undertone.WARM -> when (depth) {
            SkinDepth.LIGHT -> listOf(
                HairColorSwatch("Golden Blonde", 0xFFD8B26E),
                HairColorSwatch("Honey Blonde", 0xFFC99A5B),
                HairColorSwatch("Strawberry", 0xFFC77F52),
                HairColorSwatch("Caramel", 0xFFA86F3E),
                HairColorSwatch("Golden Brown", 0xFF8A5A33)
            )
            SkinDepth.MEDIUM -> listOf(
                HairColorSwatch("Warm Black", 0xFF2A1B14),
                HairColorSwatch("Chestnut", 0xFF6B3A22),
                HairColorSwatch("Golden Brown", 0xFF7B4A28),
                HairColorSwatch("Copper", 0xFFA0522D),
                HairColorSwatch("Caramel", 0xFF9A6236)
            )
            SkinDepth.DEEP -> listOf(
                HairColorSwatch("Espresso", 0xFF2E1C14),
                HairColorSwatch("Chocolate", 0xFF3B2418),
                HairColorSwatch("Mahogany", 0xFF5A2A22),
                HairColorSwatch("Copper Brown", 0xFF7A3E22),
                HairColorSwatch("Honey Brown", 0xFF8C5A2E)
            )
        }
        Undertone.COOL -> when (depth) {
            SkinDepth.LIGHT -> listOf(
                HairColorSwatch("Ash Brown", 0xFF6E5F52),
                HairColorSwatch("Mushroom", 0xFF776A5E),
                HairColorSwatch("Light Ash", 0xFF8C7B68),
                HairColorSwatch("Ash Blonde", 0xFFBFB39B),
                HairColorSwatch("Platinum", 0xFFE3DCCF)
            )
            SkinDepth.MEDIUM -> listOf(
                HairColorSwatch("Soft Black", 0xFF2A2522),
                HairColorSwatch("Dark Ash", 0xFF3F342D),
                HairColorSwatch("Cool Brown", 0xFF5A4637),
                HairColorSwatch("Burgundy", 0xFF5B2333),
                HairColorSwatch("Ash Brown", 0xFF6E5F52)
            )
            SkinDepth.DEEP -> listOf(
                HairColorSwatch("Jet Black", 0xFF121010),
                HairColorSwatch("Blue Black", 0xFF161A22),
                HairColorSwatch("Soft Black", 0xFF2A2522),
                HairColorSwatch("Cool Espresso", 0xFF2B211D),
                HairColorSwatch("Plum", 0xFF3E1A24)
            )
        }
        Undertone.NEUTRAL -> when (depth) {
            SkinDepth.LIGHT -> listOf(
                HairColorSwatch("Natural Brown", 0xFF6A4A35),
                HairColorSwatch("Light Brown", 0xFF8E6B4E),
                HairColorSwatch("Soft Caramel", 0xFFA57A52),
                HairColorSwatch("Beige Blonde", 0xFFC8B596),
                HairColorSwatch("Natural Blonde", 0xFFC7A97C)
            )
            SkinDepth.MEDIUM -> listOf(
                HairColorSwatch("Dark Brown", 0xFF3D2B20),
                HairColorSwatch("Chestnut", 0xFF6B3A22),
                HairColorSwatch("Mocha", 0xFF5C4033),
                HairColorSwatch("Natural Brown", 0xFF6A4A35),
                HairColorSwatch("Soft Caramel", 0xFFA57A52)
            )
            SkinDepth.DEEP -> listOf(
                HairColorSwatch("Natural Black", 0xFF1C1714),
                HairColorSwatch("Espresso", 0xFF2E1C14),
                HairColorSwatch("Dark Chocolate", 0xFF2F1F17),
                HairColorSwatch("Dark Cherry", 0xFF3F1D1F),
                HairColorSwatch("Mocha", 0xFF5C4033)
            )
        }
    }

    private fun undertoneTips(undertone: Undertone): List<String> = when (undertone) {
        Undertone.WARM -> listOf(
            "Golden, honey and copper tones echo the warmth in your skin for a healthy glow.",
            "Avoid very ashy or icy shades — they can make warm skin look dull."
        )
        Undertone.COOL -> listOf(
            "Ash, beige and cool-toned shades complement the rosy notes in your skin.",
            "Avoid brassy orange or heavy golden tones that can clash with cool undertones."
        )
        Undertone.NEUTRAL -> listOf(
            "Both warm and cool shades suit you — balanced, natural tones look most polished.",
            "Avoid extremes like icy platinum or bright copper unless you want a bold statement."
        )
    }

    private fun depthTips(depth: SkinDepth): List<String> = when (depth) {
        SkinDepth.LIGHT -> listOf(
            "Stay within 2–3 shades of your natural color for a soft, natural finish.",
            "Subtle highlights add dimension without looking harsh."
        )
        SkinDepth.MEDIUM -> listOf(
            "Rich mid-tones and soft balayage add depth and movement.",
            "Slightly lighter face-framing pieces brighten your complexion."
        )
        SkinDepth.DEEP -> listOf(
            "Deep, glossy shades look rich and luxurious against your skin.",
            "Caramel or burgundy highlights add dimension without heavy bleaching."
        )
    }

    private fun samplePatch(bitmap: Bitmap, center: PointF, radius: Int, out: MutableList<Int>) {
        val cx = center.x.roundToInt()
        val cy = center.y.roundToInt()
        val left = max(0, cx - radius)
        val top = max(0, cy - radius)
        val right = min(bitmap.width - 1, cx + radius)
        val bottom = min(bitmap.height - 1, cy + radius)
        if (right <= left || bottom <= top) return
        for (y in top..bottom) {
            for (x in left..right) {
                val c = bitmap.getPixel(x, y)
                val lum = luminance(c)
                if (lum in 25f..245f) out.add(c)
            }
        }
    }

    private fun luminance(c: Int): Float {
        val r = (c shr 16) and 0xFF
        val g = (c shr 8) and 0xFF
        val b = c and 0xFF
        return 0.299f * r + 0.587f * g + 0.114f * b
    }

    /**
     * sRGB (D65) to CIE L*a*b*.
     */
    internal fun rgbToLab(r: Int, g: Int, b: Int): Triple<Float, Float, Float> {
        fun lin(c: Int): Double {
            val v = c / 255.0
            return if (v <= 0.04045) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        }
        val rl = lin(r)
        val gl = lin(g)
        val bl = lin(b)
        val x = (0.4124564 * rl + 0.3575761 * gl + 0.1804375 * bl) / 0.95047
        val y = (0.2126729 * rl + 0.7151522 * gl + 0.0721750 * bl) / 1.0
        val z = (0.0193339 * rl + 0.1191920 * gl + 0.9503041 * bl) / 1.08883
        fun f(t: Double): Double = if (t > 0.008856) cbrt(t) else 7.787 * t + 16.0 / 116.0
        val fx = f(x)
        val fy = f(y)
        val fz = f(z)
        val l = 116.0 * fy - 16.0
        val a = 500.0 * (fx - fy)
        val bb = 200.0 * (fy - fz)
        return Triple(l.toFloat(), a.toFloat(), bb.toFloat())
    }
}
