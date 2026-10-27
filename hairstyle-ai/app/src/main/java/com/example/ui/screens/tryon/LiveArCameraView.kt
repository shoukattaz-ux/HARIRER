package com.example.ui.screens.tryon

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ar.ArFaceTracker
import com.example.ar.ArHeadTransform
import com.example.data.model.HairstyleOverlayAsset
import com.example.engine.HairstyleOverlayEngine
import java.util.Locale
import java.util.concurrent.Executors

/**
 * High-performance Jetpack Compose AR Camera View.
 *
 * 1. Streams front-camera video with CameraX.
 * 2. Runs local ML Kit face/head tracking in real-time.
 * 3. Automatically anchors transparent hairstyle overlay to detected anatomical hairline.
 * 4. Automatically scales hairstyle with head width and distance from camera.
 * 5. Automatically rotates hairstyle according to visual head tilt angle.
 * 6. Features live debug tracking overlay showing bounding box, anchor point, scale, and rotation.
 * 7. Zero generative AI calls; instant, responsive 60fps face filter experience.
 */
@Composable
fun LiveArCameraView(
    activeAsset: HairstyleOverlayAsset?,
    useFrontCamera: Boolean,
    fineTuneScale: Float = 1.0f,
    fineTuneOffsetX: Float = 0f,
    fineTuneOffsetY: Float = 0f,
    colorTint: Int? = null,
    showDebugOverlay: Boolean = true,
    onTransformChanged: (ArHeadTransform?) -> Unit,
    onRegisterCapture: ((() -> Bitmap?) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var currentTransform by remember { mutableStateOf<ArHeadTransform?>(null) }
    var viewWidth by remember { mutableStateOf(1080) }
    var viewHeight by remember { mutableStateOf(1920) }

    // PreviewView reference for lifecycle management
    var previewViewRef by remember { mutableStateOf<PreviewView?>(null) }

    // Cache decoded hairstyle bitmap to avoid decoding on every draw frame
    var cachedAssetId by remember { mutableStateOf<String?>(null) }
    var cachedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val paint = remember {
        Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
        }
    }

    val occlusionPaint = remember {
        Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
            colorFilter = PorterDuffColorFilter(
                android.graphics.Color.argb(255, 16, 13, 11),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    LaunchedEffect(colorTint) {
        if (colorTint != null && colorTint != 0) {
            paint.colorFilter = PorterDuffColorFilter(colorTint, PorterDuff.Mode.SRC_ATOP)
            val r = (android.graphics.Color.red(colorTint) * 0.40f).toInt().coerceIn(10, 255)
            val g = (android.graphics.Color.green(colorTint) * 0.40f).toInt().coerceIn(10, 255)
            val b = (android.graphics.Color.blue(colorTint) * 0.40f).toInt().coerceIn(10, 255)
            occlusionPaint.colorFilter = PorterDuffColorFilter(
                android.graphics.Color.argb(255, r, g, b),
                PorterDuff.Mode.SRC_IN
            )
        } else {
            paint.colorFilter = null
            occlusionPaint.colorFilter = PorterDuffColorFilter(
                android.graphics.Color.argb(255, 16, 13, 11),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

    // Load active asset bitmap when hairstyle changes
    LaunchedEffect(activeAsset?.id) {
        if (activeAsset != null && activeAsset.id != cachedAssetId) {
            try {
                val bmp = HairstyleOverlayEngine.loadOverlayBitmap(context, activeAsset)
                cachedBitmap = bmp
                cachedAssetId = activeAsset.id
            } catch (e: Exception) {
                Log.e("LiveArCameraView", "Failed to decode hairstyle asset ${activeAsset.id}", e)
            }
        }
    }

    // Create AR face tracker
    val arTracker = remember {
        ArFaceTracker { transform ->
            currentTransform = transform
            onTransformChanged(transform)
        }
    }

    LaunchedEffect(useFrontCamera, viewWidth, viewHeight) {
        arTracker.isFrontCamera = useFrontCamera
        arTracker.previewWidth = viewWidth.toFloat()
        arTracker.previewHeight = viewHeight.toFloat()
    }

    // Bind camera lifecycle when previewView or lens changes (NOT on every frame recomposition)
    LaunchedEffect(previewViewRef, useFrontCamera) {
        val pView = previewViewRef ?: return@LaunchedEffect
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()

                // Both use cases configured with 16:9 for identical sensor FOV
                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build().also {
                        it.setSurfaceProvider(pView.surfaceProvider)
                    }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build().also {
                        it.setAnalyzer(cameraExecutor, arTracker)
                    }

                val cameraSelector = if (useFrontCamera) {
                    if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA))
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    else
                        CameraSelector.DEFAULT_BACK_CAMERA
                } else {
                    if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA))
                        CameraSelector.DEFAULT_BACK_CAMERA
                    else
                        CameraSelector.DEFAULT_FRONT_CAMERA
                }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("LiveArCameraView", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            arTracker.close()
        }
    }

    fun captureCurrentView(): Bitmap? {
        val baseFrame = previewViewRef?.bitmap ?: return null
        val result = Bitmap.createBitmap(baseFrame.width, baseFrame.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(result)
        canvas.drawBitmap(baseFrame, 0f, 0f, null)

        val transform = currentTransform
        val hairBitmap = cachedBitmap
        val asset = activeAsset

        if (transform != null && hairBitmap != null && asset != null && viewWidth > 0 && viewHeight > 0) {
            val scaleX = baseFrame.width.toFloat() / viewWidth.toFloat()
            val scaleY = baseFrame.height.toFloat() / viewHeight.toFloat()

            val scaledTransform = transform.copy(
                hairlineX = transform.hairlineX * scaleX,
                hairlineY = transform.hairlineY * scaleY,
                headWidth = transform.headWidth * scaleX,
                headHeight = transform.headHeight * scaleY
            )

            val render = HairstyleOverlayEngine.calculateUnifiedRender(
                transform = scaledTransform,
                asset = asset,
                hairBitmap = hairBitmap,
                userScale = fineTuneScale,
                userOffsetX = fineTuneOffsetX,
                userOffsetY = fineTuneOffsetY
            )

            canvas.drawBitmap(hairBitmap, render.occlusionMatrix, occlusionPaint)
            canvas.drawBitmap(hairBitmap, render.hairMatrix, paint)
        }

        return result
    }

    LaunchedEffect(onRegisterCapture) {
        onRegisterCapture?.invoke { captureCurrentView() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                if (size.width > 0 && size.height > 0) {
                    viewWidth = size.width
                    viewHeight = size.height
                    arTracker.previewWidth = size.width.toFloat()
                    arTracker.previewHeight = size.height.toFloat()
                }
            }
    ) {
        // 1. Live Camera Preview View (fills screen)
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    previewViewRef = this
                }
            },
            update = { /* No-op on recomposition to prevent camera rebind stutter */ },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Real-Time AR Hairstyle Overlay Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val transform = currentTransform
            val hairBitmap = cachedBitmap
            val asset = activeAsset

            if (transform != null && hairBitmap != null && asset != null) {
                val render = HairstyleOverlayEngine.calculateUnifiedRender(
                    transform = transform,
                    asset = asset,
                    hairBitmap = hairBitmap,
                    userScale = fineTuneScale,
                    userOffsetX = fineTuneOffsetX,
                    userOffsetY = fineTuneOffsetY
                )

                // 1. Occlusion foundation underlay - completely masks user's original visible hair
                drawContext.canvas.nativeCanvas.drawBitmap(hairBitmap, render.occlusionMatrix, occlusionPaint)

                // 2. Primary high-definition hairstyle overlay
                drawContext.canvas.nativeCanvas.drawBitmap(hairBitmap, render.hairMatrix, paint)

                val targetX = render.anchorScreenX
                val targetY = render.anchorScreenY

                // 3. Visual Debug Overlays (Requirement 9)
                if (showDebugOverlay) {
                    // A. Detected face bounding box
                    val bLeft = transform.boundingBoxLeft
                    val bTop = transform.boundingBoxTop
                    val bWidth = transform.boundingBoxWidth
                    val bHeight = transform.boundingBoxHeight
                    if (bWidth > 0 && bHeight > 0) {
                        drawRect(
                            color = Color(0x8000E676),
                            topLeft = Offset(bLeft, bTop),
                            size = Size(bWidth, bHeight),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    // B. Hairstyle hairline anchor reticle
                    drawCircle(
                        color = Color(0xFF00E676),
                        radius = 10.dp.toPx(),
                        center = Offset(targetX, targetY),
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(
                        color = Color(0xFFFFD600),
                        radius = 4.dp.toPx(),
                        center = Offset(targetX, targetY)
                    )

                    // C. Cranial vertex / head top indicator
                    drawCircle(
                        color = Color(0xFF40C4FF),
                        radius = 6.dp.toPx(),
                        center = Offset(transform.headTopX, transform.headTopY)
                    )
                }
            }
        }

        // 4. Telemetry Debug HUD Card (Requirement 9)
        if (showDebugOverlay && currentTransform != null) {
            val t = currentTransform!!
            val asset = activeAsset
            val hairScale = if (asset != null && cachedBitmap != null) {
                (t.headWidth / asset.hairWidthRatio) * fineTuneScale * asset.defaultScale / cachedBitmap!!.width.toFloat()
            } else 1.0f

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.80f),
                border = BorderStroke(1.dp, Color(0xFF00E676).copy(alpha = 0.7f)),
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 12.dp, top = 64.dp)
                    .align(Alignment.TopStart)
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                    Text(
                        text = "⚡ AR HEAD TRACKING DEBUG",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Face BBox: [${t.boundingBoxLeft.toInt()}, ${t.boundingBoxTop.toInt()}, ${t.boundingBoxRight.toInt()}, ${t.boundingBoxBottom.toInt()}]",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Anchor X/Y: (${t.hairlineX.toInt()}, ${t.hairlineY.toInt()})",
                        color = Color(0xFFFFD600),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Hair Scale: ${String.format(Locale.US, "%.2f", hairScale)}x | Head W: ${t.headWidth.toInt()}px",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "Head Rotation: ${String.format(Locale.US, "%.1f°", t.rollDegrees)}",
                        color = Color(0xFF80D8FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
