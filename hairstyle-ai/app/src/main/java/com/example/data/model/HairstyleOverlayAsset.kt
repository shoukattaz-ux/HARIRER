package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

/**
 * Data class representing a transparent hairstyle overlay asset.
 * Includes both drawable resource and WebP asset path.
 */
data class HairstyleOverlayAsset(
    val id: String,
    val name: String,
    val category: String,
    val gender: Gender,
    @DrawableRes val overlayRes: Int,
    val assetPath: String = "",
    val hairlineAnchorX: Float = 0.5f,  // Horizontal center of the hairstyle arch (0.0 to 1.0)
    val hairlineAnchorY: Float = 0.768f, // Vertical position of the center hairline arch in asset (0.0 to 1.0)
    val hairWidthRatio: Float = 0.865f, // Ratio of hair width to asset width (519/600 = 0.865)
    val defaultScale: Float = 1.0f,
    val defaultOffsetX: Float = 0f,
    val defaultOffsetY: Float = 0f,
    val defaultRotation: Float = 0f
)

/**
 * Geometric bounds, angle, and landmark information detected from the user's face photo:
 * - head position (headCenterX, headCenterY)
 * - head width & height
 * - head roll angle (headAngleZ)
 * - forehead position (foreheadX, foreheadY)
 * - temple positions (leftTempleX/Y, rightTempleX/Y)
 * - hairline position (hairlineX, hairlineY)
 */
data class DetectedFaceLandmarks(
    val hasFace: Boolean,
    val headCenterX: Float,
    val headCenterY: Float,
    val headWidth: Float,
    val headHeight: Float,
    val headAngleZ: Float = 0f, // Head roll / tilt angle in degrees
    val foreheadX: Float,
    val foreheadY: Float,
    val leftTempleX: Float,
    val leftTempleY: Float,
    val rightTempleX: Float,
    val rightTempleY: Float,
    val hairlineX: Float,
    val hairlineY: Float,
    val eyeDistance: Float,
    val eyeMidX: Float,
    val eyeMidY: Float,
    val faceWidth: Float,
    val imageWidth: Int,
    val imageHeight: Int,
    val confidence: Float = 0.8f
) {
    val normHeadCenterX: Float get() = if (imageWidth > 0) headCenterX / imageWidth else 0.5f
    val normHeadCenterY: Float get() = if (imageHeight > 0) headCenterY / imageHeight else 0.38f
    val normForeheadX: Float get() = if (imageWidth > 0) foreheadX / imageWidth else 0.5f
    val normForeheadY: Float get() = if (imageHeight > 0) foreheadY / imageHeight else 0.28f
    val normHairlineX: Float get() = if (imageWidth > 0) hairlineX / imageWidth else 0.5f
    val normHairlineY: Float get() = if (imageHeight > 0) hairlineY / imageHeight else 0.22f
    val normHeadWidth: Float get() = if (imageWidth > 0) headWidth / imageWidth else 0.48f
    val normFaceWidth: Float get() = if (imageWidth > 0) faceWidth / imageWidth else 0.48f
    val normLeftTempleX: Float get() = if (imageWidth > 0) leftTempleX / imageWidth else 0.26f
    val normRightTempleX: Float get() = if (imageWidth > 0) rightTempleX / imageWidth else 0.74f
}

/**
 * Transform state for positioning, scaling, and rotating the hairstyle overlay.
 */
data class OverlayTransform(
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
    val scale: Float = 1.0f,
    val rotation: Float = 0f
)
