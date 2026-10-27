package com.example.ar

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin

/**
 * High-performance, on-device real-time face & head tracking engine.
 *
 * Uses Google ML Kit Face Landmarker on device (0ms network, no generative AI, 100% local).
 * Implements Google's official GraphicOverlay coordinate transformation matrix:
 * 1. Proportional aspect fill scaling (ScaleType.FILL_CENTER)
 * 2. Centered viewport offsets (postScaleWidthOffset, postScaleHeightOffset)
 * 3. Horizontal mirror reflection around preview center for front-camera selfie mode
 * 4. Calibrated anatomical hairline and cranial landmark calculations
 * 5. Temporal EMA smoothing to eliminate micro-jitter while tracking head motion
 */
class ArFaceTracker(
    private val onTransformUpdated: (ArHeadTransform?) -> Unit
) : ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "ArFaceTracker"
        private const val EMA_ALPHA_POS = 0.50f
        private const val EMA_ALPHA_SCALE = 0.40f
        private const val EMA_ALPHA_ROT = 0.42f
        private const val FACE_HOLD_GRACE_MS = 350L

        /**
         * Reusable face landmark detection for uploaded static photos.
         * Runs on-device ML Kit on a bitmap and delivers calibrated ArHeadTransform.
         */
        fun processStaticBitmap(
            bitmap: Bitmap,
            viewWidth: Float,
            viewHeight: Float,
            onResult: (ArHeadTransform?) -> Unit
        ) {
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .setMinFaceSize(0.10f)
                .build()

            val detector = FaceDetection.getClient(options)
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val primaryFace = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }!!
                        val tracker = ArFaceTracker(onResult).apply {
                            this.previewWidth = viewWidth
                            this.previewHeight = viewHeight
                            this.isFrontCamera = false // Uploaded photos are standard unmirrored images
                            this.isStaticPhotoFit = true
                        }
                        tracker.processDetectedFaces(listOf(primaryFace), bitmap.width, bitmap.height)
                    } else {
                        onResult(null)
                    }
                    detector.close()
                }
                .addOnFailureListener {
                    Log.e(TAG, "Static photo face detection failed", it)
                    onResult(null)
                    detector.close()
                }
        }

        /**
         * Extracts ONLY the user's face and hair from an uploaded photo,
         * discarding the user's original body, background, and old clothes.
         */
        fun extractFaceFromBitmap(
            bitmap: Bitmap,
            onResult: (FaceExtractor.ExtractedFace?) -> Unit
        ) {
            val options = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                .setMinFaceSize(0.10f)
                .build()

            val detector = FaceDetection.getClient(options)
            val inputImage = InputImage.fromBitmap(bitmap, 0)

            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isNotEmpty()) {
                        val primaryFace = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }!!
                        FaceExtractor.extractCleanFace(bitmap, primaryFace) { extracted ->
                            onResult(extracted)
                            detector.close()
                        }
                    } else {
                        onResult(null)
                        detector.close()
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "Face extraction failed", it)
                    onResult(null)
                    detector.close()
                }
        }
    }

    private val detector: FaceDetector

    // Screen viewport dimensions
    var previewWidth: Float = 1080f
    var previewHeight: Float = 1920f
    var isFrontCamera: Boolean = true
    var isStaticPhotoFit: Boolean = false

    // Temporal smoothing state
    private var lastTransform: ArHeadTransform? = null
    private var lastFaceSeenTimeMs: Long = 0L

    init {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.12f)
            .build()
        detector = FaceDetection.getClient(options)
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                val orientedWidth: Int
                val orientedHeight: Int
                if (rotationDegrees == 90 || rotationDegrees == 270) {
                    orientedWidth = mediaImage.height
                    orientedHeight = mediaImage.width
                } else {
                    orientedWidth = mediaImage.width
                    orientedHeight = mediaImage.height
                }
                processDetectedFaces(faces, orientedWidth, orientedHeight)
            }
            .addOnFailureListener { exc ->
                Log.e(TAG, "ML Kit Face processing failed", exc)
                checkGracePeriod()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun processDetectedFaces(faces: List<Face>, imageWidth: Int, imageHeight: Int) {
        val now = System.currentTimeMillis()

        if (faces.isEmpty()) {
            checkGracePeriod()
            return
        }

        // Pick primary face with largest screen area
        val primaryFace = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }
        if (primaryFace == null) {
            checkGracePeriod()
            return
        }

        lastFaceSeenTimeMs = now

        val imgW = imageWidth.toFloat()
        val imgH = imageHeight.toFloat()

        // 1. Precise Viewport Scaling and Centering Matrix (Fill for Live Camera, Fit for Static Photo)
        val scaleFactor = if (isStaticPhotoFit) {
            minOf(previewWidth / imgW, previewHeight / imgH)
        } else {
            max(previewWidth / imgW, previewHeight / imgH)
        }
        val postScaleWidthOffset = (previewWidth - (imgW * scaleFactor)) / 2f
        val postScaleHeightOffset = (previewHeight - (imgH * scaleFactor)) / 2f

        fun mapX(rawX: Float): Float {
            val scaledX = rawX * scaleFactor + postScaleWidthOffset
            return if (isFrontCamera) previewWidth - scaledX else scaledX
        }

        fun mapY(rawY: Float): Float {
            return rawY * scaleFactor + postScaleHeightOffset
        }

        fun mapPoint(rawX: Float, rawY: Float): PointF = PointF(mapX(rawX), mapY(rawY))

        // 2. Screen Bounding Box of Face
        val rawBox = primaryFace.boundingBox
        val boxX1 = mapX(rawBox.left.toFloat())
        val boxX2 = mapX(rawBox.right.toFloat())
        val boxLeft = minOf(boxX1, boxX2)
        val boxRight = maxOf(boxX1, boxX2)
        val boxTop = mapY(rawBox.top.toFloat())
        val boxBottom = mapY(rawBox.bottom.toFloat())
        val boxWidth = boxRight - boxLeft
        val boxHeight = boxBottom - boxTop
        val boxCenterX = (boxLeft + boxRight) / 2f
        val boxCenterY = (boxTop + boxBottom) / 2f

        // 3. Eye Landmarks and Head Orientation in Screen Space
        val leftEyeLandmark = primaryFace.getLandmark(FaceLandmark.LEFT_EYE)?.position
        val rightEyeLandmark = primaryFace.getLandmark(FaceLandmark.RIGHT_EYE)?.position

        val eyeMidScreenX: Float
        val eyeMidScreenY: Float
        val eyeDistScreen: Float
        val rollDegrees: Float
        val rightX: Float
        val rightY: Float
        val upX: Float
        val upY: Float

        if (leftEyeLandmark != null && rightEyeLandmark != null) {
            val pLeftEye = mapPoint(leftEyeLandmark.x, leftEyeLandmark.y)
            val pRightEye = mapPoint(rightEyeLandmark.x, rightEyeLandmark.y)

            // Determine screen-left and screen-right eye positions
            val screenLeft = if (pLeftEye.x <= pRightEye.x) pLeftEye else pRightEye
            val screenRight = if (pLeftEye.x <= pRightEye.x) pRightEye else pLeftEye

            val dx = screenRight.x - screenLeft.x
            val dy = screenRight.y - screenLeft.y
            val dist = hypot(dx, dy)
            eyeDistScreen = if (dist > 10f) dist else boxWidth * 0.44f

            eyeMidScreenX = (screenLeft.x + screenRight.x) / 2f
            eyeMidScreenY = (screenLeft.y + screenRight.y) / 2f

            // Head roll angle directly from visual eye line orientation on screen:
            rollDegrees = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()

            // Normalized right vector (+X along eye line)
            rightX = dx / eyeDistScreen
            rightY = dy / eyeDistScreen

            // Normalized UP vector pointing towards top of head (90° CCW in screen space where +Y is down)
            upX = dy / eyeDistScreen
            upY = -dx / eyeDistScreen
        } else {
            val rawRoll = primaryFace.headEulerAngleZ
            rollDegrees = if (isFrontCamera) -rawRoll else rawRoll
            val rad = Math.toRadians(rollDegrees.toDouble())
            val cosR = cos(rad).toFloat()
            val sinR = sin(rad).toFloat()
            rightX = cosR
            rightY = sinR
            upX = sinR
            upY = -cosR

            eyeDistScreen = boxWidth * 0.44f
            eyeMidScreenX = boxCenterX
            eyeMidScreenY = boxTop + (boxHeight * 0.40f)
        }

        val yawDegrees = if (isFrontCamera) -primaryFace.headEulerAngleY else primaryFace.headEulerAngleY
        val pitchDegrees = primaryFace.headEulerAngleX

        // 4. Anatomical cranial landmarks along the detected head axis
        // Forehead: ~0.65x eye distance above eye line (mid-forehead)
        val foreheadX = eyeMidScreenX + (upX * eyeDistScreen * 0.65f)
        val foreheadY = eyeMidScreenY + (upY * eyeDistScreen * 0.65f)

        // Hairline (Trichion): ~1.15x eye distance above eye line (anatomical natural hairline)
        val hairlineX = eyeMidScreenX + (upX * eyeDistScreen * 1.15f)
        val hairlineY = eyeMidScreenY + (upY * eyeDistScreen * 1.15f)

        // Head Top (Cranial vertex): ~1.85x eye distance above eye line
        val headTopX = eyeMidScreenX + (upX * eyeDistScreen * 1.85f)
        val headTopY = eyeMidScreenY + (upY * eyeDistScreen * 1.85f)

        // Head width & height: cranial width across temples and parietal bones
        val headWidth = max(eyeDistScreen * 2.10f, boxWidth * 1.05f)
        val headHeight = max(eyeDistScreen * 2.85f, boxHeight * 1.10f)

        // Face center (mid-face centroid)
        val faceCenterX = eyeMidScreenX - (upX * eyeDistScreen * 0.25f)
        val faceCenterY = eyeMidScreenY - (upY * eyeDistScreen * 0.25f)

        // Left & Right Temples
        val templeDist = eyeDistScreen * 1.15f
        val templeUp = eyeDistScreen * 0.55f
        val leftTempleX = eyeMidScreenX - (rightX * templeDist) + (upX * templeUp)
        val leftTempleY = eyeMidScreenY - (rightY * templeDist) + (upY * templeUp)
        val rightTempleX = eyeMidScreenX + (rightX * templeDist) + (upX * templeUp)
        val rightTempleY = eyeMidScreenY + (rightY * templeDist) + (upY * templeUp)

        // 5. Apply Exponential Moving Average (EMA) smoothing to eliminate micro-jitter
        val prev = lastTransform
        val smoothedTransform = if (prev != null && (now - lastFaceSeenTimeMs) < 600L) {
            ArHeadTransform(
                faceCenterX = ema(prev.faceCenterX, faceCenterX, EMA_ALPHA_POS),
                faceCenterY = ema(prev.faceCenterY, faceCenterY, EMA_ALPHA_POS),
                foreheadX = ema(prev.foreheadX, foreheadX, EMA_ALPHA_POS),
                foreheadY = ema(prev.foreheadY, foreheadY, EMA_ALPHA_POS),
                hairlineX = ema(prev.hairlineX, hairlineX, EMA_ALPHA_POS),
                hairlineY = ema(prev.hairlineY, hairlineY, EMA_ALPHA_POS),
                leftTempleX = ema(prev.leftTempleX, leftTempleX, EMA_ALPHA_POS),
                leftTempleY = ema(prev.leftTempleY, leftTempleY, EMA_ALPHA_POS),
                rightTempleX = ema(prev.rightTempleX, rightTempleX, EMA_ALPHA_POS),
                rightTempleY = ema(prev.rightTempleY, rightTempleY, EMA_ALPHA_POS),
                headWidth = ema(prev.headWidth, headWidth, EMA_ALPHA_SCALE),
                headHeight = ema(prev.headHeight, headHeight, EMA_ALPHA_SCALE),
                headTopX = ema(prev.headTopX, headTopX, EMA_ALPHA_POS),
                headTopY = ema(prev.headTopY, headTopY, EMA_ALPHA_POS),
                rollDegrees = emaAngle(prev.rollDegrees, rollDegrees, EMA_ALPHA_ROT),
                yawDegrees = ema(prev.yawDegrees, yawDegrees, EMA_ALPHA_ROT),
                pitchDegrees = ema(prev.pitchDegrees, pitchDegrees, EMA_ALPHA_ROT),
                eyeDistance = ema(prev.eyeDistance, eyeDistScreen, EMA_ALPHA_SCALE),
                scaleFactor = scaleFactor,
                confidence = 0.95f,
                boundingBoxLeft = ema(prev.boundingBoxLeft, boxLeft, EMA_ALPHA_POS),
                boundingBoxTop = ema(prev.boundingBoxTop, boxTop, EMA_ALPHA_POS),
                boundingBoxRight = ema(prev.boundingBoxRight, boxRight, EMA_ALPHA_POS),
                boundingBoxBottom = ema(prev.boundingBoxBottom, boxBottom, EMA_ALPHA_POS),
                isFrontCamera = isFrontCamera,
                viewWidth = previewWidth,
                viewHeight = previewHeight,
                timestampMs = now
            )
        } else {
            ArHeadTransform(
                faceCenterX = faceCenterX,
                faceCenterY = faceCenterY,
                foreheadX = foreheadX,
                foreheadY = foreheadY,
                hairlineX = hairlineX,
                hairlineY = hairlineY,
                leftTempleX = leftTempleX,
                leftTempleY = leftTempleY,
                rightTempleX = rightTempleX,
                rightTempleY = rightTempleY,
                headWidth = headWidth,
                headHeight = headHeight,
                headTopX = headTopX,
                headTopY = headTopY,
                rollDegrees = rollDegrees,
                yawDegrees = yawDegrees,
                pitchDegrees = pitchDegrees,
                eyeDistance = eyeDistScreen,
                scaleFactor = scaleFactor,
                confidence = 0.90f,
                boundingBoxLeft = boxLeft,
                boundingBoxTop = boxTop,
                boundingBoxRight = boxRight,
                boundingBoxBottom = boxBottom,
                isFrontCamera = isFrontCamera,
                viewWidth = previewWidth,
                viewHeight = previewHeight,
                timestampMs = now
            )
        }

        lastTransform = smoothedTransform
        onTransformUpdated(smoothedTransform)
    }

    private fun checkGracePeriod() {
        val now = System.currentTimeMillis()
        if (lastTransform != null && (now - lastFaceSeenTimeMs) < FACE_HOLD_GRACE_MS) {
            onTransformUpdated(lastTransform)
        } else {
            lastTransform = null
            onTransformUpdated(null)
        }
    }

    private fun ema(current: Float, target: Float, alpha: Float): Float {
        return current + alpha * (target - current)
    }

    private fun emaAngle(current: Float, target: Float, alpha: Float): Float {
        var diff = (target - current) % 360f
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f
        return current + alpha * diff
    }

    fun close() {
        detector.close()
    }
}
