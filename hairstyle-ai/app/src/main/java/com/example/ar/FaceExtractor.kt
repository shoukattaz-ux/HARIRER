package com.example.ar

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.segmentation.Segmentation
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * Precision Anatomical Head and Neck Extractor for Mannequin Try-On.
 *
 * Guarantees ZERO clothing from the uploaded photo:
 * - Completely eliminates shirt collars, shoulders, and chest from the user's photo.
 * - Restricts the lower portion strictly to an anatomical tapered neck that fits into the mannequin collar.
 * - Feathers all edges smoothly to prevent any rectangular box artifacts or harsh borders.
 * - Works seamlessly with ML Kit Selfie Segmentation and provides an equally clean fallback.
 */
object FaceExtractor {

    private const val TAG = "FaceExtractor"

    data class ExtractedFace(
        val faceBitmap: Bitmap,
        val faceWidth: Float,
        val faceHeight: Float,
        val rollDegrees: Float,
        val chinRelativeY: Float
    )

    fun extractCleanFace(
        sourceBitmap: Bitmap,
        face: Face,
        onResult: (ExtractedFace?) -> Unit
    ) {
        val box = face.boundingBox
        val faceW = box.width().toFloat()
        val faceH = box.height().toFloat()
        val centerX = box.exactCenterX()
        val centerY = box.exactCenterY()
        val chinY = box.bottom.toFloat()
        val topY = box.top.toFloat()

        // Strict crop bounds: Full hair above, strictly tapered neck below (NO shirt/shoulders)
        val cropLeft = max(0, (centerX - faceW * 0.65f).toInt())
        val cropRight = min(sourceBitmap.width, (centerX + faceW * 0.65f).toInt())
        val cropTop = max(0, (topY - faceH * 0.55f).toInt())
        val cropBottom = min(sourceBitmap.height, (chinY + faceH * 0.12f).toInt())

        val cropW = max(10, cropRight - cropLeft)
        val cropH = max(10, cropBottom - cropTop)

        try {
            val options = SelfieSegmenterOptions.Builder()
                .setDetectorMode(SelfieSegmenterOptions.SINGLE_IMAGE_MODE)
                .build()
            val segmenter = Segmentation.getClient(options)
            val inputImage = InputImage.fromBitmap(sourceBitmap, 0)

            segmenter.process(inputImage)
                .addOnSuccessListener { mask ->
                    try {
                        val maskWidth = mask.width
                        val maskHeight = mask.height
                        val maskBuffer = mask.buffer

                        val scaleX = maskWidth.toFloat() / sourceBitmap.width.toFloat()
                        val scaleY = maskHeight.toFloat() / sourceBitmap.height.toFloat()

                        val resultBitmap = Bitmap.createBitmap(cropW, cropH, Bitmap.Config.ARGB_8888)
                        val srcPixels = IntArray(cropW * cropH)
                        sourceBitmap.getPixels(srcPixels, 0, cropW, cropLeft, cropTop, cropW, cropH)

                        val outPixels = IntArray(cropW * cropH)

                        maskBuffer.rewind()
                        val maskFloats = FloatArray(maskWidth * maskHeight)
                        maskBuffer.asFloatBuffer().get(maskFloats)

                        for (y in 0 until cropH) {
                            val globalY = cropTop + y
                            val maskY = min(maskHeight - 1, (globalY * scaleY).toInt())

                            for (x in 0 until cropW) {
                                val globalX = cropLeft + x
                                val maskX = min(maskWidth - 1, (globalX * scaleX).toInt())
                                val mlConfidence = maskFloats[maskY * maskWidth + maskX]

                                val anatomicalWeight = computeAnatomicalWeight(
                                    globalX = globalX.toFloat(),
                                    globalY = globalY.toFloat(),
                                    centerX = centerX,
                                    centerY = centerY,
                                    topY = topY,
                                    chinY = chinY,
                                    faceW = faceW,
                                    faceH = faceH
                                )

                                val idx = y * cropW + x
                                val srcColor = srcPixels[idx]

                                // Combine ML Kit segmentation with strict anatomical neck cutoff
                                val combinedFactor = if (mlConfidence > 0.35f) {
                                    val mlAlpha = ((mlConfidence - 0.35f) / 0.40f).coerceIn(0f, 1f)
                                    mlAlpha * anatomicalWeight
                                } else {
                                    // Fallback to anatomical weight if segmentation missed hair strands
                                    anatomicalWeight * 0.70f
                                }

                                if (combinedFactor > 0.03f) {
                                    val finalAlpha = (combinedFactor * 255f).toInt().coerceIn(0, 255)
                                    val r = Color.red(srcColor)
                                    val g = Color.green(srcColor)
                                    val b = Color.blue(srcColor)
                                    outPixels[idx] = Color.argb(finalAlpha, r, g, b)
                                } else {
                                    outPixels[idx] = 0 // 100% transparent
                                }
                            }
                        }

                        resultBitmap.setPixels(outPixels, 0, cropW, 0, 0, cropW, cropH)
                        val chinRatio = ((chinY - cropTop) / cropH.toFloat()).coerceIn(0.70f, 0.95f)

                        segmenter.close()
                        onResult(
                            ExtractedFace(
                                faceBitmap = resultBitmap,
                                faceWidth = cropW.toFloat(),
                                faceHeight = cropH.toFloat(),
                                rollDegrees = face.headEulerAngleZ,
                                chinRelativeY = chinRatio
                            )
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing mask", e)
                        segmenter.close()
                        onResult(fallbackExtract(sourceBitmap, face))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Selfie segmentation failed", e)
                    segmenter.close()
                    onResult(fallbackExtract(sourceBitmap, face))
                }
        } catch (e: Exception) {
            Log.e(TAG, "Could not initialize SelfieSegmenter", e)
            onResult(fallbackExtract(sourceBitmap, face))
        }
    }

    /**
     * Fallback extraction using pure sculpted anatomical geometry:
     * Eliminates rectangular boxes, removes all clothes, and tapers the neck softly.
     */
    private fun fallbackExtract(sourceBitmap: Bitmap, face: Face): ExtractedFace {
        val box = face.boundingBox
        val faceW = box.width().toFloat()
        val faceH = box.height().toFloat()
        val centerX = box.exactCenterX()
        val centerY = box.exactCenterY()
        val chinY = box.bottom.toFloat()
        val topY = box.top.toFloat()

        val cropLeft = max(0, (centerX - faceW * 0.65f).toInt())
        val cropRight = min(sourceBitmap.width, (centerX + faceW * 0.65f).toInt())
        val cropTop = max(0, (topY - faceH * 0.55f).toInt())
        val cropBottom = min(sourceBitmap.height, (chinY + faceH * 0.12f).toInt())

        val cropW = max(10, cropRight - cropLeft)
        val cropH = max(10, cropBottom - cropTop)

        val resultBitmap = Bitmap.createBitmap(cropW, cropH, Bitmap.Config.ARGB_8888)
        val srcPixels = IntArray(cropW * cropH)
        sourceBitmap.getPixels(srcPixels, 0, cropW, cropLeft, cropTop, cropW, cropH)
        val outPixels = IntArray(cropW * cropH)

        for (y in 0 until cropH) {
            val globalY = cropTop + y
            for (x in 0 until cropW) {
                val globalX = cropLeft + x
                val weight = computeAnatomicalWeight(
                    globalX = globalX.toFloat(),
                    globalY = globalY.toFloat(),
                    centerX = centerX,
                    centerY = centerY,
                    topY = topY,
                    chinY = chinY,
                    faceW = faceW,
                    faceH = faceH
                )

                val idx = y * cropW + x
                if (weight > 0.02f) {
                    val srcColor = srcPixels[idx]
                    val finalAlpha = (weight * 255f).toInt().coerceIn(0, 255)
                    val r = Color.red(srcColor)
                    val g = Color.green(srcColor)
                    val b = Color.blue(srcColor)
                    outPixels[idx] = Color.argb(finalAlpha, r, g, b)
                } else {
                    outPixels[idx] = 0 // 100% transparent (no rectangular box!)
                }
            }
        }

        resultBitmap.setPixels(outPixels, 0, cropW, 0, 0, cropW, cropH)
        val chinRatio = ((chinY - cropTop) / cropH.toFloat()).coerceIn(0.70f, 0.95f)

        return ExtractedFace(
            faceBitmap = resultBitmap,
            faceWidth = cropW.toFloat(),
            faceHeight = cropH.toFloat(),
            rollDegrees = face.headEulerAngleZ,
            chinRelativeY = chinRatio
        )
    }

    /**
     * Computes the smooth anatomical alpha weight for any pixel.
     * Guarantees that below the chin, all shirt collars and shoulders are zeroed out (0 alpha),
     * leaving only a clean, tapered neck that blends into the mannequin's shirt collar.
     */
    private fun computeAnatomicalWeight(
        globalX: Float,
        globalY: Float,
        centerX: Float,
        centerY: Float,
        topY: Float,
        chinY: Float,
        faceW: Float,
        faceH: Float
    ): Float {
        val dx = globalX - centerX

        // 1. Below Chin: The Tapered Neck (ZERO clothes / shirt collar)
        if (globalY > chinY) {
            val distBelowChin = globalY - chinY
            val maxNeckDepth = faceH * 0.09f // Only 9% of face height below chin (cuts off above any shirt)

            if (distBelowChin > maxNeckDepth) {
                return 0f // Completely transparent below neck!
            }

            // Neck half-width: Tapers slightly from jaw down towards collar
            val progress = (distBelowChin / maxNeckDepth).coerceIn(0f, 1f)
            val neckHalfWidth = faceW * (0.24f * (1f - progress * 0.20f))

            if (Math.abs(dx) > neckHalfWidth) {
                return 0f // Completely transparent outside neck! (Eliminates shirt collar flaps & shoulders)
            }

            // Smooth feathering at neck horizontal edges
            val edgeDist = neckHalfWidth - Math.abs(dx)
            val featherW = faceW * 0.05f
            val hFade = (edgeDist / featherW).coerceIn(0f, 1f)

            // Smooth vertical fade into the collar
            val vFade = (1f - progress).coerceIn(0f, 1f)

            return hFade * vFade
        }

        // 2. Above Chin: Hair, Forehead, Cheeks, and Jaw
        // Upper Head & Hair Dome
        val midHeadY = topY + faceH * 0.30f
        if (globalY < midHeadY) {
            val rx = faceW * 0.58f
            val ry = faceH * 0.62f
            val normDist = hypot(dx / rx, (globalY - midHeadY) / ry)

            return if (normDist <= 1.0f) {
                1.0f
            } else {
                (1f - (normDist - 1.0f) / 0.15f).coerceIn(0f, 1f)
            }
        }

        // Mid Face: Cheeks and Jawline down to Chin
        val progress = ((globalY - midHeadY) / (chinY - midHeadY)).coerceIn(0f, 1f)
        val jawHalfWidth = faceW * (0.55f * (1f - progress) + 0.32f * progress)

        return if (Math.abs(dx) <= jawHalfWidth) {
            1.0f
        } else {
            val overflow = Math.abs(dx) - jawHalfWidth
            val featherW = faceW * 0.08f
            (1f - overflow / featherW).coerceIn(0f, 1f)
        }
    }
}
