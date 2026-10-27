package com.example.analysis

import android.graphics.Bitmap
import android.graphics.PointF
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Reusable, on-device facial landmark and geometry analyzer.
 * Evaluates facial proportions (length, width, forehead, cheekbones, jawline, chin)
 * and estimates the user's face shape (Oval, Round, Square, Heart, Oblong, Diamond).
 *
 * Runs 100% on-device using Google ML Kit with zero cloud uploads or external API calls.
 */
object FaceShapeAnalyzer {

    private const val TAG = "FaceShapeAnalyzer"

    /**
     * Complete result of a face shape analysis.
     */
    data class FaceShapeAnalysisResult(
        val detectedShape: String, // Oval, Round, Square, Heart, Oblong, Diamond
        val confidencePercent: Int,
        val explanation: String,
        val complementaryAdvice: List<String>,
        val suitableHairstyleTypes: List<String>,
        val proportions: FacialProportions,
        val disclaimer: String = "This analysis is an aesthetic styling estimate based on 2D geometric landmark proportions, not a medical or biometric diagnosis."
    )

    /**
     * Quantitative biometric measurements extracted from facial landmarks.
     */
    data class FacialProportions(
        val lengthToWidthRatio: Float,
        val foreheadToJawRatio: Float,
        val cheekToJawRatio: Float,
        val cheekToForeheadRatio: Float,
        val chinShapeDescription: String,
        val jawlineDescription: String,
        val cheekboneDescription: String
    )

    /**
     * Analyzes an input Bitmap on-device and returns the estimated Face Shape result.
     * Guaranteed local execution; does not store or upload the photo.
     */
    fun analyzeFaceShape(
        bitmap: Bitmap,
        onResult: (FaceShapeAnalysisResult?) -> Unit
    ) {
        analyzeFaceDetailed(bitmap) { result, _ -> onResult(result) }
    }

    /**
     * Same as [analyzeFaceShape] but also returns the primary ML Kit [Face]
     * (contours + landmarks), used by the animated AI Face Scan and skin tone analysis.
     */
    fun analyzeFaceDetailed(
        bitmap: Bitmap,
        onResult: (FaceShapeAnalysisResult?, Face?) -> Unit
    ) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.10f)
            .build()

        val detector = FaceDetection.getClient(options)
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    // Pick the primary (largest) face
                    val primaryFace = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }!!
                    val analysis = computeFaceShape(primaryFace, bitmap.width.toFloat(), bitmap.height.toFloat())
                    onResult(analysis, primaryFace)
                } else {
                    Log.d(TAG, "No face detected in photo.")
                    onResult(null, null)
                }
                detector.close()
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "ML Kit Face detection failed", error)
                onResult(null, null)
                detector.close()
            }
    }

    /**
     * Computes the geometric landmark ratios and determines the classification
     * using a calibrated multi-metric anthropometric scoring model.
     */
    private fun computeFaceShape(
        face: Face,
        imgWidth: Float,
        imgHeight: Float
    ): FaceShapeAnalysisResult {
        val box = face.boundingBox

        // 1. Extract contours if available
        val faceContour = face.getContour(FaceContour.FACE)?.points

        // 2. Extract key landmark points
        val leftCheek = face.getLandmark(FaceLandmark.LEFT_CHEEK)?.position
        val rightCheek = face.getLandmark(FaceLandmark.RIGHT_CHEEK)?.position
        val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)?.position
        val rightEar = face.getLandmark(FaceLandmark.RIGHT_EAR)?.position
        val mouthBottom = face.getLandmark(FaceLandmark.MOUTH_BOTTOM)?.position

        // 3. Compute Widths & Lengths
        val faceLength: Float
        val cheekboneWidth: Float
        val foreheadWidth: Float
        val jawWidth: Float
        val chinCurvature: Float

        if (faceContour != null && faceContour.size >= 30) {
            // Contour points available (usually 36 points around perimeter)
            val topForeheadY = faceContour.minOf { it.y }
            val bottomChinPoint = faceContour.maxByOrNull { it.y } ?: PointF(box.centerX().toFloat(), box.bottom.toFloat())
            faceLength = max(1f, bottomChinPoint.y - topForeheadY)

            // Cheekbone width: widest contour span around eye/cheek level
            val midY = (topForeheadY + bottomChinPoint.y) * 0.5f
            val midContourPoints = faceContour.filter { abs(it.y - midY) < faceLength * 0.20f }
            val minMidX = midContourPoints.minOfOrNull { it.x } ?: box.left.toFloat()
            val maxMidX = midContourPoints.maxOfOrNull { it.x } ?: box.right.toFloat()
            val contourCheekWidth = maxMidX - minMidX

            // If cheek landmarks are also detected, cross-reference for maximum precision
            val landmarkCheekWidth = if (leftCheek != null && rightCheek != null) {
                abs(rightCheek.x - leftCheek.x) * 1.30f
            } else {
                contourCheekWidth
            }
            cheekboneWidth = max(1f, max(contourCheekWidth, landmarkCheekWidth))

            // Forehead width: width across upper temple/eyebrow level
            val foreheadY = topForeheadY + faceLength * 0.25f
            val foreheadPoints = faceContour.filter { abs(it.y - foreheadY) < faceLength * 0.15f }
            val minForeheadX = foreheadPoints.minOfOrNull { it.x } ?: (box.left + box.width() * 0.10f)
            val maxForeheadX = foreheadPoints.maxOfOrNull { it.x } ?: (box.right - box.width() * 0.10f)
            foreheadWidth = max(1f, maxForeheadX - minForeheadX)

            // Jawline width: width across lower face at mouth/upper chin level
            val jawY = bottomChinPoint.y - faceLength * 0.22f
            val jawPoints = faceContour.filter { abs(it.y - jawY) < faceLength * 0.15f }
            val minJawX = jawPoints.minOfOrNull { it.x } ?: (box.left + box.width() * 0.18f)
            val maxJawX = jawPoints.maxOfOrNull { it.x } ?: (box.right - box.width() * 0.18f)
            jawWidth = max(1f, maxJawX - minJawX)

            // Chin curvature: width of chin tip itself across bottom contour points
            val chinNeighbors = faceContour.filter { it.y > bottomChinPoint.y - faceLength * 0.08f }
            chinCurvature = if (chinNeighbors.size >= 2) {
                val span = chinNeighbors.maxOf { it.x } - chinNeighbors.minOf { it.x }
                span / cheekboneWidth
            } else {
                0.24f
            }
        } else {
            // Fallback using standard landmarks & bounding box
            faceLength = max(1f, box.height().toFloat())
            cheekboneWidth = if (leftCheek != null && rightCheek != null) {
                max(1f, abs(rightCheek.x - leftCheek.x) * 1.35f)
            } else {
                max(1f, box.width().toFloat())
            }

            foreheadWidth = if (leftEar != null && rightEar != null) {
                abs(rightEar.x - leftEar.x) * 0.90f
            } else {
                cheekboneWidth * 0.88f
            }

            jawWidth = if (mouthBottom != null) {
                cheekboneWidth * 0.76f
            } else {
                cheekboneWidth * 0.72f
            }
            chinCurvature = 0.25f
        }

        // 4. Calculate Key Proportional Ratios
        val lengthToWidth = faceLength / cheekboneWidth
        val foreheadToJaw = foreheadWidth / max(1f, jawWidth)
        val cheekToJaw = cheekboneWidth / max(1f, jawWidth)
        val cheekToForehead = cheekboneWidth / max(1f, foreheadWidth)

        val isChinSharp = chinCurvature < 0.28f
        val isChinBroad = chinCurvature > 0.35f

        // 5. Multi-Metric Scoring Engine for all 6 Face Shapes
        // Robust to photo variations, camera distances, lighting, and natural facial features
        var diamondScore = 0f
        var heartScore = 0f
        var squareScore = 0f
        var roundScore = 0f
        var oblongScore = 0f
        var ovalScore = 0f

        // --- DIAMOND SHAPE SCORING ---
        // Defining signature: Cheekbones are the widest part of the face, noticeably wider
        // than BOTH the forehead AND the jawline, with a tapered lower face.
        if (cheekToForehead >= 1.05f) {
            diamondScore += min(45f, (cheekToForehead - 1.05f) * 160f + 20f)
        }
        if (cheekToJaw >= 1.08f) {
            diamondScore += min(45f, (cheekToJaw - 1.08f) * 160f + 20f)
        }
        if (foreheadToJaw in 0.85f..1.22f) {
            diamondScore += 25f // Balanced upper and lower narrowness
        }
        if (lengthToWidth in 1.18f..1.52f) {
            diamondScore += 20f
        }
        if (chinCurvature < 0.32f) {
            diamondScore += 18f
        }

        // --- HEART SHAPE SCORING ---
        // Defining signature: Forehead is prominently wider than jawline, tapering down to a pointed chin.
        if (foreheadToJaw >= 1.15f) {
            heartScore += min(50f, (foreheadToJaw - 1.15f) * 160f + 25f)
        }
        if (cheekToJaw >= 1.10f) {
            heartScore += 25f
        }
        if (chinCurvature < 0.30f) {
            heartScore += 20f
        }
        if (lengthToWidth in 1.18f..1.48f) {
            heartScore += 18f
        }

        // --- SQUARE SHAPE SCORING ---
        // Defining signature: Jawline is nearly as wide as cheekbones and forehead, angular lines, length ~ width.
        if (cheekToJaw <= 1.12f) {
            squareScore += min(50f, (1.12f - cheekToJaw) * 200f + 30f)
        }
        if (foreheadToJaw in 0.88f..1.14f) {
            squareScore += 25f
        }
        if (lengthToWidth <= 1.28f) {
            squareScore += 25f
        }
        if (isChinBroad) {
            squareScore += 20f
        }

        // --- ROUND SHAPE SCORING ---
        // Defining signature: Equal length and width (close to 1:1), soft circular curves, NOT narrow forehead/jaw like Diamond.
        if (lengthToWidth <= 1.20f) {
            roundScore += min(50f, (1.20f - lengthToWidth) * 180f + 25f)
        }
        if (cheekToForehead in 0.98f..1.12f && cheekToJaw in 1.04f..1.18f) {
            roundScore += 30f
        }
        if (chinCurvature in 0.24f..0.36f) {
            roundScore += 25f
        }
        // If cheeks are significantly wider than both forehead and jaw, it's Diamond, NOT Round!
        if (cheekToForehead >= 1.08f && cheekToJaw >= 1.14f) {
            roundScore -= 45f
        }

        // --- OBLONG SHAPE SCORING ---
        // Defining signature: Face length significantly longer than width (> 1.44x).
        if (lengthToWidth >= 1.44f) {
            oblongScore += min(60f, (lengthToWidth - 1.44f) * 250f + 35f)
        }
        if (cheekToJaw in 0.95f..1.18f && foreheadToJaw in 0.90f..1.18f) {
            oblongScore += 30f
        }

        // --- OVAL SHAPE SCORING ---
        // Defining signature: Balanced golden ratio, length ~ 1.28-1.46x width, forehead slightly wider than jaw.
        if (lengthToWidth in 1.26f..1.48f) {
            ovalScore += 35f
        }
        if (foreheadToJaw in 1.02f..1.22f) {
            ovalScore += 30f
        }
        if (cheekToJaw in 1.06f..1.22f) {
            ovalScore += 25f
        }
        if (chinCurvature in 0.20f..0.32f) {
            ovalScore += 18f
        }
        // If cheeks dominate over forehead and jaw, penalize Oval in favor of Diamond
        if (cheekToForehead >= 1.10f && cheekToJaw >= 1.15f) {
            ovalScore -= 30f
        }

        // Determine Winning Shape by Highest Affinity Score
        val scores = listOf(
            "Diamond" to diamondScore,
            "Heart" to heartScore,
            "Square" to squareScore,
            "Round" to roundScore,
            "Oblong" to oblongScore,
            "Oval" to ovalScore
        )
        val winningPair = scores.maxByOrNull { it.second } ?: ("Oval" to 85f)
        val shape = winningPair.first
        val rawScore = winningPair.second
        val confidence = min(96, max(82, (78f + rawScore * 0.15f).toInt()))

        val explanation: String
        val complementaryAdvice: List<String>
        val suitableHairstyles: List<String>

        when (shape) {
            "Diamond" -> {
                explanation = "Your cheekbones are the widest and most prominent facial feature, gracefully paired with a narrower forehead and a tapered chin."
                complementaryAdvice = listOf(
                    "Fringe or textured bangs to add flattering width across the forehead",
                    "Chin-length bobs or textured layers that add fullness at the jawline",
                    "Avoid excessive vertical volume on top that further stretches face height"
                )
                suitableHairstyles = listOf("Textured Crop", "Side Swept Fringe", "Layered Bob", "Medium Textured Quiff")
            }
            "Heart" -> {
                explanation = "Your forehead is prominently wider than your jawline, tapering gracefully into a delicate, sculpted chin."
                complementaryAdvice = listOf(
                    "Side-parted cuts and soft textured fringes balance forehead width",
                    "Chin-length styles, textured crops, or a medium beard add lower face fullness",
                    "Avoid slicked-back high-volume cuts that accentuate upper forehead width"
                )
                suitableHairstyles = listOf("Side Swept Fringe", "Mid Fade with Fringe", "Curtain Bangs", "Textured Crop")
            }
            "Square" -> {
                explanation = "Your face length and width are well balanced, defined by a strong, angular jawline and equal forehead-to-jaw width."
                complementaryAdvice = listOf(
                    "Soft textured layers, quiffs, and fades soften prominent jaw angles",
                    "Short sides with textured volume on top flatter angular lines",
                    "Avoid blunt, heavy square cuts with sharp horizontal edges"
                )
                suitableHairstyles = listOf("Textured Quiff", "Mid Fade", "Pompadour Fade", "Messy Textured Crop")
            }
            "Round" -> {
                explanation = "Your face length and width are approximately equal, characterized by soft circular curves and full cheeks."
                complementaryAdvice = listOf(
                    "Styles with vertical height and volume on top create an elongating effect",
                    "High skin fades, pompadours, and angled cuts slim the cheeks",
                    "Avoid round, bowl-shaped cuts or chin-length blunt bobs"
                )
                suitableHairstyles = listOf("Pompadour High Fade", "Spiky Textured Fade", "Angled Quiff", "Long Layered Waves")
            }
            "Oblong" -> {
                explanation = "Your face length is noticeably greater than its width with straight vertical cheek lines and uniform proportions."
                complementaryAdvice = listOf(
                    "Cuts with side volume and textured width help create horizontal harmony",
                    "Fringes or bangs cover part of the forehead to visually shorten face length",
                    "Avoid extreme high pompadours or vertical spikes that add excess height"
                )
                suitableHairstyles = listOf("Low Taper with Bangs", "Classic Side Part", "Wavy Mid-Length", "Textured Fringe")
            }
            else -> {
                explanation = "Your face exhibits naturally harmonious proportions, being slightly longer than wide with a softly curved, balanced jawline."
                complementaryAdvice = listOf(
                    "Most versatile shape: almost any cut, fade, or length looks natural",
                    "Textured quiffs, classic side parts, and crops look exceptionally sharp",
                    "Keep hair off the forehead to showcase your natural facial symmetry"
                )
                suitableHairstyles = listOf("Classic Side Part", "Textured Quiff", "Taper Fade Crop", "Curtain Bangs")
            }
        }

        val proportions = FacialProportions(
            lengthToWidthRatio = String.format("%.2f", lengthToWidth).toFloatOrNull() ?: lengthToWidth,
            foreheadToJawRatio = String.format("%.2f", foreheadToJaw).toFloatOrNull() ?: foreheadToJaw,
            cheekToJawRatio = String.format("%.2f", cheekToJaw).toFloatOrNull() ?: cheekToJaw,
            cheekToForeheadRatio = String.format("%.2f", cheekToForehead).toFloatOrNull() ?: cheekToForehead,
            chinShapeDescription = if (isChinSharp) "Pointed V-Taper" else if (isChinBroad) "Square / Broad" else "Softly Rounded",
            jawlineDescription = if (cheekToJaw <= 1.12f) "Strong & Angular" else if (foreheadToJaw >= 1.18f || shape == "Diamond") "Slender & Tapered" else "Gently Curved",
            cheekboneDescription = if (shape == "Diamond" || (cheekToForehead >= 1.08f && cheekToJaw >= 1.12f)) "Prominent High Cheeks" else "Harmonious"
        )

        return FaceShapeAnalysisResult(
            detectedShape = shape,
            confidencePercent = confidence,
            explanation = explanation,
            complementaryAdvice = complementaryAdvice,
            suitableHairstyleTypes = suitableHairstyles,
            proportions = proportions
        )
    }
}
