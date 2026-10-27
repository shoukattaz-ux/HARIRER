package com.example.ui.screens.scan

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.analysis.FaceShapeAnalyzer
import com.example.analysis.HairColorAdvisor
import com.example.analysis.StyleAdviceBuilder
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.model.UserPhotoSource
import com.example.data.repository.HairstyleRepository
import com.example.engine.HairstyleOverlayEngine
import com.example.ui.components.StyleAdviceReportCard
import com.example.ui.theme.ElectricVioletSoft
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.HairLookDarkBg
import com.example.ui.theme.HairLookPurple
import com.example.ui.theme.HairLookPurpleGradientEnd
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.GallerySaver
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.math.max

private enum class ScanPhase { IDLE, SEARCHING, TRACING, LOCKED, ERROR }

private const val MAX_SCAN_DIMENSION = 1280
private const val MIN_SEARCH_MS = 900L
private const val TRACE_MS = 1500
private const val RAIL_STEP_MS = 180L

/**
 * AI Face Scan — animated face shape detection followed by a shareable
 * "Personalized Style Advice" report (Hair Analysis + Color Analysis).
 *
 * Everything runs on-device with ML Kit; the photo is never uploaded.
 */
@Composable
fun AiFaceScanScreen(
    initialPhoto: UserPhotoSource?,
    initialGender: Gender,
    onPhotoSelected: (UserPhotoSource) -> Unit,
    onFaceShapeDetected: (String) -> Unit,
    onSkinToneDetected: (HairColorAdvisor.SkinToneResult?) -> Unit,
    onSelectHairstyle: (String) -> Unit,
    onTryLive: (HairstyleItem) -> Unit,
    onGenerateLooks: (List<HairstyleItem>) -> Unit,
    onOpenFaceShapeGuide: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    var phase by remember { mutableStateOf(ScanPhase.IDLE) }
    var photo by remember { mutableStateOf<Bitmap?>(null) }
    var contour by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var faceBounds by remember { mutableStateOf<Rect?>(null) }
    var analysis by remember { mutableStateOf<FaceShapeAnalyzer.FaceShapeAnalysisResult?>(null) }
    var skinTone by remember { mutableStateOf<HairColorAdvisor.SkinToneResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var railShape by remember { mutableStateOf<ScanFaceShape?>(null) }
    var gender by remember { mutableStateOf(initialGender) }
    var isExporting by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    var scanJob by remember { mutableStateOf<Job?>(null) }
    val trace = remember { Animatable(0f) }
    val reportLayer = rememberGraphicsLayer()

    val catalog = remember { HairstyleRepository.getAllHairstyles() }
    val report = remember(analysis, skinTone, gender) {
        analysis?.let { StyleAdviceBuilder.build(it, skinTone, gender, catalog) }
    }

    fun startScan(source: UserPhotoSource) {
        scanJob?.cancel()
        scanJob = scope.launch {
            phase = ScanPhase.SEARCHING
            errorMessage = null
            exportMessage = null
            analysis = null
            skinTone = null
            contour = emptyList()
            faceBounds = null
            trace.snapTo(0f)

            val bitmap = withContext(Dispatchers.IO) {
                HairstyleOverlayEngine.resolvePhotoToBitmap(context, source)?.let { downscale(it) }
            }
            if (bitmap == null) {
                phase = ScanPhase.ERROR
                errorMessage = "Unable to open this photo. Please try a different one."
                return@launch
            }
            photo = bitmap
            val startedAt = SystemClock.elapsedRealtime()

            val (result, face) = detectFace(bitmap)
            if (result == null || face == null) {
                phase = ScanPhase.ERROR
                errorMessage = "No clear face found. Use a well-lit, front-facing photo with your full face visible."
                return@launch
            }
            val tone = withContext(Dispatchers.Default) { HairColorAdvisor.estimateSkinTone(bitmap, face) }
            contour = face.getContour(FaceContour.FACE)?.points?.map { Offset(it.x, it.y) }.orEmpty()
            faceBounds = face.boundingBox.let {
                Rect(it.left.toFloat(), it.top.toFloat(), it.right.toFloat(), it.bottom.toFloat())
            }

            // Keep the scan line visible briefly so the analysis reads as deliberate, not instant
            delay(max(0L, MIN_SEARCH_MS - (SystemClock.elapsedRealtime() - startedAt)))
            phase = ScanPhase.TRACING
            trace.animateTo(1f, tween(TRACE_MS, easing = FastOutSlowInEasing))
            delay(250)

            skinTone = tone
            analysis = result
            phase = ScanPhase.LOCKED
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onFaceShapeDetected(result.detectedShape)
            onSkinToneDetected(tone)
        }
    }

    // Cycle the shape rail while scanning, then lock on the detected shape
    LaunchedEffect(phase) {
        when (phase) {
            ScanPhase.SEARCHING, ScanPhase.TRACING -> {
                var i = 0
                while (true) {
                    railShape = ScanFaceShape.entries[i % ScanFaceShape.entries.size]
                    i++
                    delay(RAIL_STEP_MS)
                }
            }
            ScanPhase.LOCKED -> {
                railShape = ScanFaceShape.fromLabel(analysis?.detectedShape)
            }
            else -> {
                railShape = null
            }
        }
    }

    LaunchedEffect(Unit) {
        initialPhoto?.let { startScan(it) }
    }

    fun usePhoto(source: UserPhotoSource) {
        onPhotoSelected(source)
        startScan(source)
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) usePhoto(UserPhotoSource.SelectedUri(uri))
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
        if (bmp != null) usePhoto(UserPhotoSource.Captured(bmp))
    }
    val cameraPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                errorMessage = "Camera is unavailable on this device. Please upload a photo instead."
            }
        } else {
            errorMessage = "Camera permission is needed for a selfie. You can upload a photo instead."
        }
    }
    val launchCamera: () -> Unit = {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                errorMessage = "Camera is unavailable on this device. Please upload a photo instead."
            }
        } else {
            cameraPermission.launch(Manifest.permission.CAMERA)
        }
    }
    val launchPicker: () -> Unit = {
        photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun exportReport(share: Boolean) {
        if (isExporting) return
        scope.launch {
            isExporting = true
            exportMessage = null
            try {
                val bitmap = reportLayer.toImageBitmap().asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)
                val uri = withContext(Dispatchers.IO) {
                    GallerySaver.saveBitmapToGallery(context, bitmap, "HairLook_StyleAdvice_${System.currentTimeMillis()}")
                }
                when {
                    uri == null -> {
                        exportMessage = "Couldn't save the report. Please check storage access and try again."
                    }
                    share -> GallerySaver.shareImage(context, uri)
                    else -> {
                        exportMessage = "Saved to Pictures/HairLook"
                    }
                }
            } catch (e: Exception) {
                exportMessage = "Couldn't create the report image. Please try again."
            } finally {
                isExporting = false
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HairLookDarkBg)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(HairLookCardBg)
                    .border(1.dp, HairLookCardBorder, CircleShape)
                    .testTag("face_scan_back_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "AI Face Scan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Find the cuts & colors that suit you",
                    style = MaterialTheme.typography.bodySmall,
                    color = HairLookPurpleGradientEnd
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Scan stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1A1D2B))
                    .testTag("face_scan_stage"),
                contentAlignment = Alignment.Center
            ) {
                val bmp = photo
                if (bmp != null) {
                    val imageBitmap = remember(bmp) { bmp.asImageBitmap() }
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Your photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    FaceScanCanvas(
                        bitmapWidth = bmp.width,
                        bitmapHeight = bmp.height,
                        contour = contour,
                        faceBounds = faceBounds,
                        traceProgress = trace.value,
                        isSearching = phase == ScanPhase.SEARCHING
                    )
                    if (phase != ScanPhase.IDLE && phase != ScanPhase.ERROR) {
                        FaceShapeRail(
                            highlighted = railShape,
                            locked = phase == ScanPhase.LOCKED,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 12.dp)
                        )
                    }
                    ScanStatusChip(
                        phase = phase,
                        analysis = analysis,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 14.dp)
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            Icons.Default.Face,
                            contentDescription = null,
                            tint = ElectricVioletSoft,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Not sure which haircut suits you?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Take a selfie or upload a clear, front-facing photo. We'll analyze your face shape and skin tone on-device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFF87171),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            val isBusy = phase == ScanPhase.SEARCHING || phase == ScanPhase.TRACING
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = launchCamera,
                    enabled = !isBusy,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("face_scan_selfie_btn"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (photo == null) "Take Selfie" else "New Selfie")
                }
                OutlinedButton(
                    onClick = launchPicker,
                    enabled = !isBusy,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("face_scan_upload_btn"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextPrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload Photo", color = TextPrimary)
                }
            }

            AnimatedVisibility(
                visible = phase == ScanPhase.LOCKED && report != null,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 6 }
            ) {
                val currentReport = report ?: return@AnimatedVisibility
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show styles for", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Gender.entries.forEach { g ->
                            FilterChip(
                                selected = gender == g,
                                onClick = { gender = g },
                                label = { Text(g.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = HairLookPurple,
                                    selectedLabelColor = Color.White,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    StyleAdviceReportCard(
                        report = currentReport,
                        onStyleClick = onSelectHairstyle,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .drawWithContent {
                                reportLayer.record { this@drawWithContent.drawContent() }
                                drawLayer(reportLayer)
                            }
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { exportReport(share = false) },
                            enabled = !isExporting,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("face_scan_save_btn"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp), tint = TextPrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save", color = TextPrimary)
                        }
                        Button(
                            onClick = { exportReport(share = true) },
                            enabled = !isExporting,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("face_scan_share_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple)
                        ) {
                            if (isExporting) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share")
                        }
                    }
                    exportMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, style = MaterialTheme.typography.bodySmall, color = TextMuted, textAlign = TextAlign.Center)
                    }

                    if (currentReport.recommendedStyles.size >= 2) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onGenerateLooks(currentReport.recommendedStyles) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("face_scan_generate_looks_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HairLookPurple)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate My 4 AI Looks", fontWeight = FontWeight.Bold)
                        }
                    }

                    currentReport.recommendedStyles.firstOrNull()?.let { topPick ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onTryLive(topPick) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("face_scan_try_top_pick_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricVioletSoft)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Try “${topPick.name}” Live", maxLines = 1)
                        }
                    }
                    TextButton(onClick = onOpenFaceShapeGuide) {
                        Text("Explore all styles by face shape", color = HairLookPurpleGradientEnd)
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanStatusChip(
    phase: ScanPhase,
    analysis: FaceShapeAnalyzer.FaceShapeAnalysisResult?,
    modifier: Modifier = Modifier
) {
    val text = when (phase) {
        ScanPhase.SEARCHING -> "Locating facial landmarks…"
        ScanPhase.TRACING -> "Measuring forehead, cheekbones & jawline…"
        ScanPhase.LOCKED -> analysis?.let { "${it.detectedShape} face · ${it.confidencePercent}% match" }
        else -> null
    } ?: return
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (phase != ScanPhase.LOCKED) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricVioletSoft, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(text, color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

private suspend fun detectFace(bitmap: Bitmap): Pair<FaceShapeAnalyzer.FaceShapeAnalysisResult?, Face?> =
    suspendCancellableCoroutine { cont ->
        FaceShapeAnalyzer.analyzeFaceDetailed(bitmap) { result, face ->
            if (cont.isActive) cont.resume(result to face)
        }
    }

private fun downscale(bitmap: Bitmap): Bitmap {
    val largest = max(bitmap.width, bitmap.height)
    if (largest <= MAX_SCAN_DIMENSION) return bitmap
    val ratio = MAX_SCAN_DIMENSION.toFloat() / largest
    return Bitmap.createScaledBitmap(
        bitmap,
        (bitmap.width * ratio).toInt().coerceAtLeast(1),
        (bitmap.height * ratio).toInt().coerceAtLeast(1),
        true
    )
}
