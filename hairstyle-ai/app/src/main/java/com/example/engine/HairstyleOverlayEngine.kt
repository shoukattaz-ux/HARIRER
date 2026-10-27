package com.example.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.media.FaceDetector
import android.util.Log
import com.example.R
import com.example.ar.ArHeadTransform
import com.example.data.model.DetectedFaceLandmarks
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.model.HairstyleOverlayAsset
import com.example.data.model.OverlayTransform
import java.io.InputStream
import kotlin.math.max

/**
 * Reusable, client-side, non-generative Hairstyle Overlay Engine.
 *
 * Responsibilities:
 * 1. Hairstyle asset registry for Men and Women (WebP assets and Drawable PNGs).
 * 2. On-device Face & Landmark detection (forehead, hairline, face width, head roll angle).
 * 3. Proportional scaling and anatomical hairline positioning.
 * 4. User fine-tuning transform management.
 * 5. Realistic single-canvas image compositing with alpha blending.
 */
object HairstyleOverlayEngine {

    private const val TAG = "HairstyleOverlayEngine"

    // Hairstyle Asset Registry with WebP assets and Drawables
    private val overlayAssets = listOf(
        // MEN'S HAIRSTYLES
        HairstyleOverlayAsset(
            id = "overlay_m_modern_quiff",
            name = "Modern Textured Quiff",
            category = "Textured",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_modern_quiff,
            assetPath = "hairstyles/men/modern-textured-quiff.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.868f,
            hairWidthRatio = 0.865f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_m_natural_quiff",
            name = "Natural Quiff",
            category = "Quiff",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_natural_quiff,
            assetPath = "hairstyles/men/natural-quiff.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.825f,
            hairWidthRatio = 0.865f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_m_buzz",
            name = "Classic Buzz Cut",
            category = "Buzz Cut",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_buzz,
            assetPath = "hairstyles/men/buzz-cut.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.435f,
            hairWidthRatio = 0.752f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_m_crop",
            name = "Textured French Crop",
            category = "Short",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_crop,
            assetPath = "hairstyles/men/french-crop.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.835f,
            hairWidthRatio = 0.848f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_m_pompadour",
            name = "Executive Pompadour",
            category = "Pompadour",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_pompadour,
            assetPath = "hairstyles/men/pompadour.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.695f,
            hairWidthRatio = 0.865f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_m_fade",
            name = "Low Taper Fade",
            category = "Fade",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_fade,
            assetPath = "hairstyles/men/low-mid-fade.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.675f,
            hairWidthRatio = 0.865f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_m_curly",
            name = "Tousled Messy Modern",
            category = "Textured",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_curly,
            assetPath = "hairstyles/men/curly-fade.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.940f,
            hairWidthRatio = 0.857f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_m_quiff",
            name = "Side-Swept Layered Flow",
            category = "Flow",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_quiff,
            assetPath = "hairstyles/men/quiff.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.845f,
            hairWidthRatio = 0.865f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),

        // WOMEN'S HAIRSTYLES
        HairstyleOverlayAsset(
            id = "overlay_w_bob",
            name = "French Chin-Length Bob",
            category = "Bob",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_bob,
            assetPath = "hairstyles/women/bob.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.730f,
            hairWidthRatio = 0.593f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_w_pixie",
            name = "Textured Pixie Cut",
            category = "Pixie",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_pixie,
            assetPath = "hairstyles/women/pixie.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.465f,
            hairWidthRatio = 0.553f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_w_layers",
            name = "Butterfly Face-Framing Layers",
            category = "Layers",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_layers,
            assetPath = "hairstyles/women/long-layered.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.835f,
            hairWidthRatio = 0.668f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_w_wavy",
            name = "Tousled Beach Waves",
            category = "Wavy",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_wavy,
            assetPath = "hairstyles/women/beach-waves.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.890f,
            hairWidthRatio = 0.697f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_w_braids",
            name = "Knotless Box Braids",
            category = "Braids",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_braids,
            assetPath = "hairstyles/women/box-braids.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.830f,
            hairWidthRatio = 0.603f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_w_curly",
            name = "Voluminous Natural Curls",
            category = "Curly",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_wavy,
            assetPath = "hairstyles/women/beach-waves.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.890f,
            hairWidthRatio = 0.697f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_w_medium",
            name = "Medium Layered Cut",
            category = "Medium",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_layers,
            assetPath = "hairstyles/women/long-layered.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.835f,
            hairWidthRatio = 0.668f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_w_long",
            name = "Long Cascading Locks",
            category = "Long",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_layers,
            assetPath = "hairstyles/women/long-layered.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.835f,
            hairWidthRatio = 0.668f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_w_ponytail",
            name = "Sleek High Ponytail",
            category = "Ponytail",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_layers,
            assetPath = "hairstyles/women/long-layered.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.835f,
            hairWidthRatio = 0.668f,
            defaultScale = 1.0f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),

        // VIP EXCLUSIVE PRO HAIRSTYLES (Available in Paid AI Studio)
        HairstyleOverlayAsset(
            id = "overlay_vip_royal_quiff",
            name = "VIP Royal Modern Quiff",
            category = "VIP Pro",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_natural_quiff,
            assetPath = "hairstyles/men/natural-quiff.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.825f,
            hairWidthRatio = 0.865f,
            defaultScale = 1.18f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_vip_diamond_fade",
            name = "VIP Diamond Taper Fade",
            category = "VIP Pro",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_fade,
            assetPath = "hairstyles/men/low-mid-fade.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.675f,
            hairWidthRatio = 0.865f,
            defaultScale = 1.18f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_vip_korean_flow",
            name = "VIP Korean Layered Flow",
            category = "VIP Pro",
            gender = Gender.MEN,
            overlayRes = R.drawable.overlay_m_quiff,
            assetPath = "hairstyles/men/quiff.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.845f,
            hairWidthRatio = 0.865f,
            defaultScale = 1.18f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_vip_platinum_layers",
            name = "VIP Platinum Butterfly Layers",
            category = "VIP Pro",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_layers,
            assetPath = "hairstyles/women/long-layered.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.835f,
            hairWidthRatio = 0.668f,
            defaultScale = 1.18f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_vip_silk_bob",
            name = "VIP Silk Parisian Bob",
            category = "VIP Pro",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_bob,
            assetPath = "hairstyles/women/bob.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.730f,
            hairWidthRatio = 0.593f,
            defaultScale = 1.18f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        ),
        HairstyleOverlayAsset(
            id = "overlay_vip_siren_waves",
            name = "VIP Siren Gloss Waves",
            category = "VIP Pro",
            gender = Gender.WOMEN,
            overlayRes = R.drawable.overlay_w_wavy,
            assetPath = "hairstyles/women/beach-waves.webp",
            hairlineAnchorX = 0.50f,
            hairlineAnchorY = 0.890f,
            hairWidthRatio = 0.697f,
            defaultScale = 1.18f,
            defaultOffsetX = 0f,
            defaultOffsetY = 0f
        )
    )

    fun getAllAssets(): List<HairstyleOverlayAsset> = overlayAssets

    fun getAssetsForGender(gender: Gender): List<HairstyleOverlayAsset> {
        return overlayAssets.filter { it.gender == gender }
    }

    fun getAssetForHairstyle(nameOrCategory: String, gender: Gender): HairstyleOverlayAsset {
        val lower = nameOrCategory.lowercase()
        return overlayAssets.firstOrNull {
            it.gender == gender && (
                lower.contains(it.category.lowercase()) ||
                it.name.lowercase().contains(lower) ||
                lower.contains(it.name.lowercase())
            )
        } ?: overlayAssets.first { it.gender == gender }
    }

    fun getAssetById(id: String): HairstyleOverlayAsset? {
        return overlayAssets.firstOrNull { it.id == id }
    }

    fun getAssetForHairstyleItem(item: HairstyleItem): HairstyleOverlayAsset {
        if (!item.overlayAssetId.isNullOrEmpty()) {
            val byId = getAssetById(item.overlayAssetId)
            if (byId != null) return byId
        }
        return getAssetForHairstyle(item.name, item.gender)
    }

    /**
     * Client-side face & head landmark detection using Android's native FaceDetector API.
     * Extracts:
     * - Head center & position (headCenterX, headCenterY)
     * - Head width & height
     * - Head tilt angle (headAngleZ roll in degrees)
     * - Forehead position (foreheadX, foreheadY)
     * - Temple positions (leftTempleX/Y, rightTempleX/Y)
     * - Hairline position (hairlineX, hairlineY)
     */
    fun detectFaceLandmarks(bitmap: Bitmap): DetectedFaceLandmarks {
        val width = bitmap.width
        val height = bitmap.height

        return try {
            val evenWidth = if (width % 2 == 0) width else width - 1
            val maxDim = 800
            val scale = if (max(evenWidth, height) > maxDim) {
                maxDim.toFloat() / max(evenWidth, height).toFloat()
            } else {
                1.0f
            }
            val scaledW = ((evenWidth * scale).toInt() / 2) * 2
            val scaledH = (height * scale).toInt()

            val bitmap565 = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, false)
                .copy(Bitmap.Config.RGB_565, false)

            val maxFaces = 1
            val faces = arrayOfNulls<FaceDetector.Face>(maxFaces)
            val detector = FaceDetector(scaledW, scaledH, maxFaces)
            val count = detector.findFaces(bitmap565, faces)

            if (count > 0 && faces[0] != null) {
                val face = faces[0]!!
                val midPoint = PointF()
                face.getMidPoint(midPoint)
                val eyeDist = face.eyesDistance() / scale
                val eyeMidX = midPoint.x / scale
                val eyeMidY = midPoint.y / scale
                val confidence = face.confidence()
                val headAngleZ = face.pose(FaceDetector.Face.EULER_Z)

                val rad = Math.toRadians(headAngleZ.toDouble())
                val cosA = Math.cos(rad).toFloat()
                val sinA = Math.sin(rad).toFloat()
                // In image coordinates (Y is down):
                // Vector pointing UP along the face midline:
                val upX = sinA
                val upY = -cosA
                // Vector pointing RIGHT across eye line:
                val rightX = cosA
                val rightY = sinA

                // Anthropometric cranial & facial dimensions:
                val headWidth = eyeDist * 2.25f
                val headHeight = eyeDist * 3.20f
                val faceWidth = eyeDist * 2.15f

                // Mid-forehead sits ~0.48x eye distance directly above eye line along head axis
                val foreheadX = eyeMidX + (upX * eyeDist * 0.48f)
                val foreheadY = eyeMidY + (upY * eyeDist * 0.48f)

                // Natural hairline (trichion) sits ~0.68x eye distance directly above eye line
                val hairlineX = eyeMidX + (upX * eyeDist * 0.68f)
                val hairlineY = eyeMidY + (upY * eyeDist * 0.68f)

                // Head center sits between eyes and top of head (~0.85x eye distance above eyes)
                val headCenterX = eyeMidX + (upX * eyeDist * 0.85f)
                val headCenterY = eyeMidY + (upY * eyeDist * 0.85f)

                // Left temple (viewer's left = person's right temple):
                val leftTempleX = eyeMidX - (rightX * eyeDist * 1.12f) + (upX * eyeDist * 0.20f)
                val leftTempleY = eyeMidY - (rightY * eyeDist * 1.12f) + (upY * eyeDist * 0.20f)

                // Right temple (viewer's right = person's left temple):
                val rightTempleX = eyeMidX + (rightX * eyeDist * 1.12f) + (upX * eyeDist * 0.20f)
                val rightTempleY = eyeMidY + (rightY * eyeDist * 1.12f) + (upY * eyeDist * 0.20f)

                Log.d(TAG, "Face detected! Eyes dist: $eyeDist, hairline: ($hairlineX, $hairlineY), angle: $headAngleZ, confidence: $confidence")

                DetectedFaceLandmarks(
                    hasFace = true,
                    headCenterX = headCenterX,
                    headCenterY = headCenterY,
                    headWidth = headWidth,
                    headHeight = headHeight,
                    headAngleZ = headAngleZ,
                    foreheadX = foreheadX,
                    foreheadY = foreheadY,
                    leftTempleX = leftTempleX,
                    leftTempleY = leftTempleY,
                    rightTempleX = rightTempleX,
                    rightTempleY = rightTempleY,
                    hairlineX = hairlineX,
                    hairlineY = hairlineY,
                    eyeDistance = eyeDist,
                    eyeMidX = eyeMidX,
                    eyeMidY = eyeMidY,
                    faceWidth = faceWidth,
                    imageWidth = width,
                    imageHeight = height,
                    confidence = confidence
                )
            } else {
                Log.d(TAG, "No face detected by FaceDetector; falling back to calibrated portrait proportions.")
                createFallbackLandmarks(width, height)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Face detection error", e)
            createFallbackLandmarks(width, height)
        }
    }

    fun createFallbackLandmarks(width: Int, height: Int): DetectedFaceLandmarks {
        val eyeDist = width * 0.22f
        val midX = width * 0.50f
        val midY = height * 0.44f
        val hairlineY = midY - (eyeDist * 0.68f)
        val foreheadY = midY - (eyeDist * 0.48f)
        val headWidth = eyeDist * 2.50f
        val headHeight = eyeDist * 3.30f
        return DetectedFaceLandmarks(
            hasFace = false,
            headCenterX = midX,
            headCenterY = midY - (eyeDist * 0.85f),
            headWidth = headWidth,
            headHeight = headHeight,
            headAngleZ = 0f,
            foreheadX = midX,
            foreheadY = foreheadY,
            leftTempleX = midX - (eyeDist * 1.12f),
            leftTempleY = midY - (eyeDist * 0.20f),
            rightTempleX = midX + (eyeDist * 1.12f),
            rightTempleY = midY - (eyeDist * 0.20f),
            hairlineX = midX,
            hairlineY = hairlineY,
            eyeDistance = eyeDist,
            eyeMidX = midX,
            eyeMidY = midY,
            faceWidth = width * 0.48f,
            imageWidth = width,
            imageHeight = height,
            confidence = 0.5f
        )
    }

    /**
     * Calculates the exact android.graphics.Matrix to automatically scale, position,
     * and rotate the transparent hairstyle overlay onto any target canvas or bitmap.
     *
     * Principles:
     * 1. Scale according to user's head width (temple-to-temple).
     * 2. Position the hairstyle's hairline arch directly on the user's detected forehead/hairline.
     * 3. Center horizontally with the user's head midline.
     * 4. Rotate along the user's detected head roll angle (Z-axis).
     * 5. Preserve aspect ratio strictly.
     * 6. Prevent hairstyle from floating, prevent forehead covering, prevent top cropping.
     */
    fun calculateAutoMatrix(
        landmarks: DetectedFaceLandmarks,
        asset: HairstyleOverlayAsset,
        overlayBitmap: Bitmap,
        destinationWidth: Float,
        destinationHeight: Float,
        nudgeX: Float = 0f,
        nudgeY: Float = 0f,
        scaleMultiplier: Float = 1.0f,
        rotationNudge: Float = 0f
    ): Matrix {
        val renderScale = minOf(
            destinationWidth / landmarks.imageWidth.toFloat(),
            destinationHeight / landmarks.imageHeight.toFloat()
        )
        val renderDx = (destinationWidth - (landmarks.imageWidth * renderScale)) / 2f
        val renderDy = (destinationHeight - (landmarks.imageHeight * renderScale)) / 2f

        // Scaled coordinates in destination space
        val screenHairlineX = (landmarks.hairlineX * renderScale) + renderDx + (nudgeX * destinationWidth)
        val screenHairlineY = (landmarks.hairlineY * renderScale) + renderDy + (nudgeY * destinationHeight)
        val screenHeadWidth = landmarks.headWidth * renderScale

        // Target hair width on the head
        val targetHairWidth = screenHeadWidth * 1.08f * asset.defaultScale * scaleMultiplier
        val hairWidthInAsset = overlayBitmap.width * asset.hairWidthRatio.coerceIn(0.6f, 1.0f)
        val assetScale = targetHairWidth / hairWidthInAsset

        // Anchor point in asset: center hairline arch
        val archXInAsset = overlayBitmap.width * 0.50f
        val archYInAsset = overlayBitmap.height * asset.hairlineAnchorY

        // Top-left destination corner
        val tx = screenHairlineX - (archXInAsset * assetScale)
        val ty = screenHairlineY - (archYInAsset * assetScale)

        val totalRotation = landmarks.headAngleZ + asset.defaultRotation + rotationNudge

        val matrix = Matrix()
        matrix.postScale(assetScale, assetScale)
        matrix.postTranslate(tx, ty)
        matrix.postRotate(totalRotation, screenHairlineX, screenHairlineY)
        return matrix
    }

    /**
     * Calculates the automatic position and scale for the hairstyle overlay.
     * Scales hairstyle according to user's head width (temple-to-temple).
     * Anchors the hairstyle hairline directly to the user's detected forehead/hairline.
     * Rotates along the head roll angle.
     */
    fun calculateAutoTransform(
        landmarks: DetectedFaceLandmarks,
        asset: HairstyleOverlayAsset
    ): OverlayTransform {
        // Desired hair width on photo = landmarks.headWidth * 1.06f * asset.defaultScale
        val targetHairWidth = landmarks.headWidth * 1.06f * asset.defaultScale
        val assetHairRatio = asset.hairWidthRatio.coerceIn(0.6f, 1.0f)
        val baseScale = (targetHairWidth / (landmarks.imageWidth * assetHairRatio)).coerceIn(0.40f, 2.5f)

        // Center horizontally on detected hairline
        val baseOffsetX = (landmarks.normHairlineX - 0.5f) + asset.defaultOffsetX
        // Center vertically: align asset's hairline arch to detected hairline
        val archOffsetFromCenterY = (asset.hairlineAnchorY - 0.50f) * baseScale
        val baseOffsetY = (landmarks.normHairlineY - 0.50f) - archOffsetFromCenterY + asset.defaultOffsetY

        return OverlayTransform(
            offsetX = baseOffsetX,
            offsetY = baseOffsetY,
            scale = baseScale,
            rotation = landmarks.headAngleZ + asset.defaultRotation
        )
    }

    /**
     * Loads transparent hairstyle overlay from assets/ or res/drawable/.
     * Includes safe fallback if asset cannot be read.
     */
    fun loadOverlayBitmap(context: Context, asset: HairstyleOverlayAsset): Bitmap? {
        // Try loading from assets first
        if (asset.assetPath.isNotBlank()) {
            try {
                val stream: InputStream = context.assets.open(asset.assetPath)
                val bmp = BitmapFactory.decodeStream(stream)
                stream.close()
                if (bmp != null) return bmp
            } catch (e: Exception) {
                Log.d(TAG, "Asset path ${asset.assetPath} not found, falling back to drawable.")
            }
        }
        // Fallback to drawable resource
        return try {
            BitmapFactory.decodeResource(context.resources, asset.overlayRes)
        } catch (e: Exception) {
            Log.e(TAG, "Failed loading hairstyle overlay resource", e)
            null
        }
    }

    /**
     * Unified mathematical result of fitting a hairstyle to detected head landmarks.
     * Used identically in Live Camera mode (real-time surface) and Upload Photo mode (static bitmap).
     */
    data class UnifiedHairstyleRender(
        val hairMatrix: Matrix,
        val occlusionMatrix: Matrix,
        val targetHairWidth: Float,
        val targetHairHeight: Float,
        val anchorScreenX: Float,
        val anchorScreenY: Float,
        val rollDegrees: Float,
        val scale: Float
    )

    /**
     * Calculates the exact scale, rotation, and positioning matrix for the selected hairstyle.
     * Fits the hairstyle snugly to the detected head width and anatomical hairline,
     * following head movements, tilt, and scale naturally.
     */
    fun calculateUnifiedRender(
        transform: ArHeadTransform,
        asset: HairstyleOverlayAsset,
        hairBitmap: Bitmap,
        userScale: Float = 1.0f,
        userOffsetX: Float = 0f,
        userOffsetY: Float = 0f,
        userRotation: Float = 0f
    ): UnifiedHairstyleRender {
        // Natural cranial fit: hair width encompasses skull width with natural hair volume
        val cranialHeadWidth = transform.headWidth
        val targetHairWidth = (cranialHeadWidth * 1.05f / asset.hairWidthRatio) * userScale * asset.defaultScale
        val scale = targetHairWidth / hairBitmap.width.toFloat()
        val targetHairHeight = hairBitmap.height.toFloat() * scale

        // Anchor point in hair bitmap coordinates
        val bAnchorX = hairBitmap.width.toFloat() * asset.hairlineAnchorX
        val bAnchorY = hairBitmap.height.toFloat() * asset.hairlineAnchorY

        // Anchor point in screen/destination coordinates
        val finalRoll = transform.rollDegrees + userRotation
        val targetX = transform.hairlineX + (userOffsetX * cranialHeadWidth)
        val targetY = transform.hairlineY + (userOffsetY * transform.headHeight)

        // Main hair matrix:
        // Translate anchor to origin, scale, rotate around origin, translate to screen anchor
        val hairMatrix = Matrix().apply {
            postTranslate(-bAnchorX, -bAnchorY)
            postScale(scale, scale)
            postRotate(finalRoll)
            postTranslate(targetX, targetY)
        }

        // Scalp occlusion matrix (covers all user natural hair underneath with +3% expansion)
        val occlusionMatrix = Matrix().apply {
            postTranslate(-bAnchorX, -bAnchorY)
            postScale(scale * 1.03f, scale * 1.03f)
            postRotate(finalRoll)
            postTranslate(targetX, targetY)
        }

        return UnifiedHairstyleRender(
            hairMatrix = hairMatrix,
            occlusionMatrix = occlusionMatrix,
            targetHairWidth = targetHairWidth,
            targetHairHeight = targetHairHeight,
            anchorScreenX = targetX,
            anchorScreenY = targetY,
            rollDegrees = finalRoll,
            scale = scale
        )
    }

    /**
     * Composites user photo and transparent hairstyle overlay into a single
     * high-resolution Bitmap using android.graphics.Canvas with alpha blending.
     * Preserves user's face, skin, eyes, beard, clothes, and background completely untouched.
     */
    fun composeOverlayWithLandmarks(
        context: Context,
        userBitmap: Bitmap,
        asset: HairstyleOverlayAsset,
        landmarks: DetectedFaceLandmarks,
        nudgeX: Float = 0f,
        nudgeY: Float = 0f,
        scaleMultiplier: Float = 1.0f,
        rotationNudge: Float = 0f
    ): Bitmap {
        val width = userBitmap.width
        val height = userBitmap.height

        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. Draw original base user photo (completely untouched)
        canvas.drawBitmap(userBitmap, 0f, 0f, paint)

        // 2. Decode transparent hairstyle overlay
        val overlayBitmap = loadOverlayBitmap(context, asset)
        if (overlayBitmap != null) {
            val matrix = calculateAutoMatrix(
                landmarks = landmarks,
                asset = asset,
                overlayBitmap = overlayBitmap,
                destinationWidth = width.toFloat(),
                destinationHeight = height.toFloat(),
                nudgeX = nudgeX,
                nudgeY = nudgeY,
                scaleMultiplier = scaleMultiplier,
                rotationNudge = rotationNudge
            )
            // 3. Draw transparent hairstyle overlay with alpha blending
            canvas.drawBitmap(overlayBitmap, matrix, paint)
        }

        return resultBitmap
    }

    /**
     * Composites user photo and transparent hairstyle overlay into a single
     * high-resolution Bitmap using android.graphics.Canvas with alpha blending.
     * Preserves user's face, skin, eyes, beard, clothes, and background completely untouched.
     */
    fun composeOverlay(
        context: Context,
        userBitmap: Bitmap,
        asset: HairstyleOverlayAsset,
        transform: OverlayTransform
    ): Bitmap {
        val width = userBitmap.width
        val height = userBitmap.height

        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(resultBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        // 1. Draw original base user photo (completely unchanged)
        canvas.drawBitmap(userBitmap, 0f, 0f, paint)

        // 2. Decode transparent hairstyle overlay
        val overlayBitmap = loadOverlayBitmap(context, asset)
        if (overlayBitmap != null) {
            val matrix = Matrix()

            // Scale to match base photo canvas
            val baseScale = (width.toFloat() / overlayBitmap.width.toFloat()) * transform.scale
            matrix.postScale(baseScale, baseScale)

            val scaledWidth = overlayBitmap.width * baseScale
            val scaledHeight = overlayBitmap.height * baseScale

            val pixelOffsetX = if (Math.abs(transform.offsetX) <= 2.0f) transform.offsetX * width else transform.offsetX
            val pixelOffsetY = if (Math.abs(transform.offsetY) <= 2.0f) transform.offsetY * height else transform.offsetY

            val tx = (width - scaledWidth) / 2f + pixelOffsetX
            val ty = (height - scaledHeight) / 2f + pixelOffsetY
            matrix.postTranslate(tx, ty)

            // Rotation around center of hairline arch
            val pivotX = tx + (scaledWidth / 2f)
            val pivotY = ty + (scaledHeight * asset.hairlineAnchorY)
            matrix.postRotate(transform.rotation, pivotX, pivotY)

            // 3. Draw transparent hairstyle overlay with alpha blending
            canvas.drawBitmap(overlayBitmap, matrix, paint)
        }

        return resultBitmap
    }

    /**
     * Resolves UserPhotoSource into a displayable and compositable Bitmap with correct EXIF orientation.
     */
    fun resolvePhotoToBitmap(context: Context, source: com.example.data.model.UserPhotoSource): Bitmap? {
        return try {
            when (source) {
                is com.example.data.model.UserPhotoSource.Captured -> source.bitmap
                is com.example.data.model.UserPhotoSource.SelectedUri -> {
                    val stream = context.contentResolver.openInputStream(source.uri)
                    val original = BitmapFactory.decodeStream(stream)
                    stream?.close()
                    if (original != null) {
                        try {
                            val exifStream = context.contentResolver.openInputStream(source.uri)
                            if (exifStream != null) {
                                val exif = android.media.ExifInterface(exifStream)
                                val orientation = exif.getAttributeInt(
                                    android.media.ExifInterface.TAG_ORIENTATION,
                                    android.media.ExifInterface.ORIENTATION_NORMAL
                                )
                                exifStream.close()
                                rotateBitmapByOrientation(original, orientation)
                            } else original
                        } catch (e: Exception) {
                            Log.w(TAG, "Exif orientation read failed, using original bitmap", e)
                            original
                        }
                    } else null
                }
                is com.example.data.model.UserPhotoSource.PresetModel -> {
                    BitmapFactory.decodeResource(context.resources, source.drawableRes)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed resolving photo to bitmap", e)
            null
        }
    }

    private fun rotateBitmapByOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            android.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            android.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            android.media.ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            android.media.ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            android.media.ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            else -> return bitmap
        }
        return try {
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (rotated != bitmap) bitmap.recycle()
            rotated
        } catch (e: Exception) {
            bitmap
        }
    }
}
