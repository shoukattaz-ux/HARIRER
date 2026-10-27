package com.example.ar

import androidx.compose.ui.geometry.Offset

/**
 * Real-time anatomical head and cranial landmarks calculated from live camera tracking.
 * Used to transform, position, scale, and rotate transparent hairstyle overlays in real time.
 */
data class ArHeadTransform(
    // 1. Face Center (centroid of the user's face)
    val faceCenterX: Float = 0f,
    val faceCenterY: Float = 0f,

    // 2. Forehead Position (anatomical mid-forehead)
    val foreheadX: Float = 0f,
    val foreheadY: Float = 0f,

    // 3. Hairline Position (Trichion - where hairstyle anchors to skull)
    val hairlineX: Float = 0f,
    val hairlineY: Float = 0f,

    // 4. Left & Right Temples (lateral skull boundary)
    val leftTempleX: Float = 0f,
    val leftTempleY: Float = 0f,
    val rightTempleX: Float = 0f,
    val rightTempleY: Float = 0f,

    // 5. Head Width (bitemporal cranial width)
    val headWidth: Float = 0f,

    // 6. Head Height and Estimated Head Top (Cranial Vertex)
    val headHeight: Float = 0f,
    val headTopX: Float = 0f,
    val headTopY: Float = 0f,

    // 7. Head Rotation / Orientation
    val rollDegrees: Float = 0f,   // Head tilt (Euler Z)
    val yawDegrees: Float = 0f,    // Head turn left/right (Euler Y)
    val pitchDegrees: Float = 0f,  // Head nod up/down (Euler X)

    // 8. Scale & Position Metrics
    val eyeDistance: Float = 0f,   // Interpupillary distance on screen
    val scaleFactor: Float = 1f,   // Proportional scale factor
    val confidence: Float = 1f,    // Face detection confidence

    // 9. Detected Face Screen Bounding Box (for AR fitting and visual debug tracking)
    val boundingBoxLeft: Float = 0f,
    val boundingBoxTop: Float = 0f,
    val boundingBoxRight: Float = 0f,
    val boundingBoxBottom: Float = 0f,

    // Metadata
    val isFrontCamera: Boolean = true,
    val viewWidth: Float = 0f,
    val viewHeight: Float = 0f,
    val timestampMs: Long = System.currentTimeMillis()
) {
    val hairlineOffset: Offset get() = Offset(hairlineX, hairlineY)
    val foreheadOffset: Offset get() = Offset(foreheadX, foreheadY)
    val faceCenterOffset: Offset get() = Offset(faceCenterX, faceCenterY)
    val headTopOffset: Offset get() = Offset(headTopX, headTopY)
    val leftTempleOffset: Offset get() = Offset(leftTempleX, leftTempleY)
    val rightTempleOffset: Offset get() = Offset(rightTempleX, rightTempleY)
    val boundingBoxWidth: Float get() = boundingBoxRight - boundingBoxLeft
    val boundingBoxHeight: Float get() = boundingBoxBottom - boundingBoxTop
}
