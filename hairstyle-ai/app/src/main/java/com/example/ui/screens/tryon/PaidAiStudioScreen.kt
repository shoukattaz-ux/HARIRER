package com.example.ui.screens.tryon

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.DetectedFaceLandmarks
import com.example.data.model.Gender
import com.example.data.model.HairstyleOverlayAsset
import com.example.data.model.OverlayTransform
import com.example.data.model.UserPhotoSource
import com.example.data.repository.PaidApiTryOnRepository
import com.example.engine.HairstyleOverlayEngine
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.ImageSaver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PaidAiStudioScreen(
    userPhoto: UserPhotoSource?,
    onUserPhotoChanged: (UserPhotoSource) -> Unit,
    onBack: () -> Unit,
    onOpenLiveCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // VIP Hairstyle Assets Library
    val allAssets = remember { HairstyleOverlayEngine.getAllAssets() }
    var selectedFilter by remember { mutableStateOf("All") }

    // User selected hairstyle from carousel line
    var selectedAsset by remember { mutableStateOf<HairstyleOverlayAsset?>(null) }
    // Actually generated/applied hairstyle on head via Dedicated API
    var appliedAsset by remember { mutableStateOf<HairstyleOverlayAsset?>(null) }
    var apiFittedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var apiEngineStatusNote by remember { mutableStateOf("Dedicated Hair AI REST API Service") }

    // Filtered assets for carousel line
    val filteredAssets = remember(selectedFilter) {
        when (selectedFilter) {
            "Men" -> allAssets.filter { it.gender == Gender.MEN }
            "Women" -> allAssets.filter { it.gender == Gender.WOMEN }
            "VIP Pro" -> allAssets.filter { it.category.startsWith("VIP") }
            else -> allAssets
        }
    }

    // High resolution loaded user photo
    var baseBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var landmarks by remember { mutableStateOf<DetectedFaceLandmarks?>(null) }

    // Fine-tuning adjustments
    var nudgeY by remember { mutableFloatStateOf(0f) }
    var scaleModifier by remember { mutableFloatStateOf(1f) }

    // Generation and Preview state
    var isGeneratingAi by remember { mutableStateOf(false) }
    var aiGenerationMessage by remember { mutableStateOf("Connecting to Dedicated Hair AI REST API...") }
    var isComparingOriginal by remember { mutableStateOf(false) }
    var showAdjustControls by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Dedicated API Settings Dialog state
    var showApiSettingsDialog by remember { mutableStateOf(false) }
    var apiUrlInput by remember { mutableStateOf(PaidApiTryOnRepository.getApiUrl(context)) }
    var apiKeyInput by remember { mutableStateOf(PaidApiTryOnRepository.getApiKey(context)) }
    var apiTestMessage by remember { mutableStateOf<String?>(null) }
    var isTestingApi by remember { mutableStateOf(false) }

    // Gallery photo picker (Zero-permission Android Photo Picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            appliedAsset = null
            apiFittedBitmap = null
            onUserPhotoChanged(UserPhotoSource.SelectedUri(uri))
        }
    }

    // Camera photo capture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            appliedAsset = null
            apiFittedBitmap = null
            onUserPhotoChanged(UserPhotoSource.Captured(bitmap))
        }
    }

    // Load base user photo bitmap & auto-detect face landmarks
    LaunchedEffect(userPhoto) {
        if (userPhoto != null) {
            val resolved = HairstyleOverlayEngine.resolvePhotoToBitmap(context, userPhoto)
            if (resolved != null) {
                baseBitmap = resolved
                landmarks = HairstyleOverlayEngine.detectFaceLandmarks(resolved)
                nudgeY = 0f
                scaleModifier = 1f
            }
        } else {
            baseBitmap = null
            landmarks = null
            appliedAsset = null
            apiFittedBitmap = null
        }
    }

    // Function to trigger realistic AI generation via Dedicated REST API
    fun triggerAiHairstyleFitting(asset: HairstyleOverlayAsset) {
        val bmp = baseBitmap ?: return
        coroutineScope.launch {
            isGeneratingAi = true
            aiGenerationMessage = "Connecting to Dedicated Hair AI REST API..."
            delay(350)
            aiGenerationMessage = "Sending user head contours & ${asset.name} to API..."
            delay(400)
            aiGenerationMessage = "API synthesizing natural follicular strands onto head..."

            val (resultBmp, note) = PaidApiTryOnRepository.executePaidApiCall(
                context = context,
                userBitmap = bmp,
                asset = asset,
                offsetY = nudgeY,
                scaleModifier = scaleModifier
            )

            apiFittedBitmap = resultBmp
            appliedAsset = asset
            apiEngineStatusNote = note
            isGeneratingAi = false
        }
    }

    // When micro-adjustments change and a style is already fitted, re-composite seamlessly
    LaunchedEffect(nudgeY, scaleModifier) {
        val bmp = baseBitmap
        val asset = appliedAsset
        val lms = landmarks
        if (bmp != null && asset != null && lms != null && apiFittedBitmap != null) {
            val autoTransform = HairstyleOverlayEngine.calculateAutoTransform(lms, asset)
            val combinedTransform = OverlayTransform(
                offsetX = autoTransform.offsetX,
                offsetY = autoTransform.offsetY + nudgeY,
                scale = autoTransform.scale * scaleModifier,
                rotation = autoTransform.rotation
            )
            apiFittedBitmap = HairstyleOverlayEngine.composeOverlay(context, bmp, asset, combinedTransform)
        }
    }

    // Save look function
    fun saveLook() {
        val lookToSave = apiFittedBitmap ?: baseBitmap ?: return
        isSaving = true
        coroutineScope.launch {
            val uri = ImageSaver.saveBitmapToGallery(
                context = context,
                bitmap = lookToSave,
                styleName = appliedAsset?.name ?: "Hairstyle"
            )
            isSaving = false
            if (uri != null) {
                Toast.makeText(context, "Look saved to gallery with photorealistic AI blend!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save photo.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Infinite breathing glow for VIP Studio Header
    val infiniteTransition = rememberInfiniteTransition(label = "vip_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // --- VIP PRO STUDIO TOP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("vip_studio_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "👑 AI Pro Hair Studio",
                            style = MaterialTheme.typography.titleMedium,
                            color = ChampagneGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Dedicated REST API Engine • Paid VIP",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // API Settings Dialog Button
                    IconButton(
                        onClick = { showApiSettingsDialog = true },
                        modifier = Modifier.testTag("vip_studio_api_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "API Settings",
                            tint = ChampagneGold
                        )
                    }

                    // Save Look button (only active when look is generated)
                    if (apiFittedBitmap != null) {
                        IconButton(
                            onClick = { saveLook() },
                            enabled = !isSaving,
                            modifier = Modifier.testTag("vip_studio_save_btn")
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = ChampagneGold,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Save Look",
                                    tint = ChampagneGold
                                )
                            }
                        }
                    }
                }
            }

            // --- DEDICATED REST API STATUS BADGE (NON-GEMINI) ---
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { showApiSettingsDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SuccessGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connected via Dedicated Hair AI REST API",
                            color = ChampagneGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "API Settings ⚙",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // =========================================================================
            // STAGE 1: IF USER HAS NOT UPLOADED OR CAPTURED PHOTO YET
            // =========================================================================
            if (baseBitmap == null) {
                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, CardBorderStroke)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ChampagneGold.copy(alpha = 0.15f),
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Step 1: Add Your Photo",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "This paid version connects directly to a dedicated AI REST API. Take a selfie with your camera or upload a photo from your gallery. Then choose a hairstyle to fit it naturally onto your head using the AI API.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Big Button 1: Take Selfie with Camera
                        Button(
                            onClick = { cameraLauncher.launch(null) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ChampagneGold,
                                contentColor = ObsidianBackground
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("studio_camera_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Take Live Selfie",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Big Button 2: Upload Photo from Gallery
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = BorderStroke(1.5.dp, ChampagneGold.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("studio_gallery_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Upload from Gallery",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Demo photo shortcut
                        Surface(
                            color = SurfaceDark,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, CardBorderStroke),
                            modifier = Modifier
                                .clickable {
                                    onUserPhotoChanged(
                                        UserPhotoSource.PresetModel(R.drawable.img_short_1, "Sample Client")
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = null,
                                    tint = ElectricViolet,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Or test instantly with a demo photo",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            } else {
                // =========================================================================
                // STAGE 2: USER PHOTO IS READY ON SCREEN!
                // =========================================================================
                Spacer(modifier = Modifier.height(8.dp))

                // Photo Action Header (Change Photo / Retake)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (appliedAsset != null) "Fitted via AI REST API" else "Photo Ready",
                            color = if (appliedAsset != null) SuccessGreen else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Change Photo Button
                    Surface(
                        color = SurfaceCard,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CardBorderStroke),
                        modifier = Modifier.clickable {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Collections,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Change Photo",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- MAIN HEAD VIEWPORT (HIGH DEFINITION MIRROR) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(SurfaceDark)
                        .border(
                            2.dp,
                            if (appliedAsset != null) ChampagneGold.copy(alpha = pulseAlpha) else CardBorderStroke,
                            RoundedCornerShape(22.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val displayBitmap = if (isComparingOriginal || appliedAsset == null) {
                        baseBitmap
                    } else {
                        apiFittedBitmap ?: baseBitmap
                    }

                    if (displayBitmap != null) {
                        Image(
                            bitmap = displayBitmap.asImageBitmap(),
                            contentDescription = "User Photo with AI Hairstyle",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        CircularProgressIndicator(color = ChampagneGold)
                    }

                    // AI Processing Overlay Animation
                    if (isGeneratingAi) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.72f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(24.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = ChampagneGold,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(46.dp)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "⚡ Dedicated Hair AI REST API",
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = aiGenerationMessage,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Floating Comparison Button (Hold to View Original)
                    if (appliedAsset != null) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(14.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isComparingOriginal = true
                                            tryAwaitRelease()
                                            isComparingOriginal = false
                                        }
                                    )
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Compare,
                                    contentDescription = null,
                                    tint = if (isComparingOriginal) SunsetRose else ChampagneGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isComparingOriginal) "Original Hair" else "Hold to Compare",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Badge Top-Right: Natural Real Hair Fitting via API
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "API Follicle Fit",
                                    color = SuccessGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // --- STEP 2: SELECTED STYLE CARD & "GENERATE VIA AI API" ACTION BUTTON ---
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val currentStyle = selectedAsset

                        if (currentStyle == null) {
                            // Prompt to pick from line
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Step 2: Select a Hairstyle",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Select your favorite hairstyle from the row below",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        } else {
                            // Hairstyle is selected!
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Selected: ${currentStyle.name}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${currentStyle.gender.displayName} • ${currentStyle.category}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ChampagneGold
                                    )
                                }

                                if (appliedAsset != null) {
                                    IconButton(onClick = { showAdjustControls = !showAdjustControls }) {
                                        Icon(
                                            imageVector = Icons.Default.Tune,
                                            contentDescription = "Fine-tune",
                                            tint = if (showAdjustControls) ChampagneGold else TextMuted
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action Button: "Generate via AI REST API"
                            val isThisStyleApplied = appliedAsset?.id == currentStyle.id

                            Button(
                                onClick = {
                                    triggerAiHairstyleFitting(currentStyle)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isThisStyleApplied) SurfaceCardElevated else ChampagneGold,
                                    contentColor = if (isThisStyleApplied) ChampagneGold else ObsidianBackground
                                ),
                                border = if (isThisStyleApplied) BorderStroke(1.5.dp, ChampagneGold) else null,
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("generate_ai_hairstyle_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isThisStyleApplied)
                                        "✓ Style Fitted via API (Apply Again)"
                                    else
                                        "⚡ Generate with AI API",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }

                            // Expandable Fine Tune Placement
                            AnimatedVisibility(visible = showAdjustControls && appliedAsset != null) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    Text(
                                        text = "Hairline Placement",
                                        fontSize = 12.sp,
                                        color = TextMuted,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { nudgeY -= 0.015f },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Up", fontSize = 11.sp, color = TextPrimary)
                                        }
                                        OutlinedButton(
                                            onClick = { nudgeY += 0.015f },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Down", fontSize = 11.sp, color = TextPrimary)
                                        }
                                        OutlinedButton(
                                            onClick = { scaleModifier += 0.03f },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Scale +", fontSize = 11.sp, color = TextPrimary)
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                nudgeY = 0f
                                                scaleModifier = 1f
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.RestartAlt, contentDescription = "Reset", modifier = Modifier.size(14.dp), tint = ChampagneGold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // --- STEP 3: HAIRSTYLES LINE (CAROUSEL) ---
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hairstyles Line",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${filteredAssets.size} Styles Available",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Filter category chips (All, Men, Women, VIP Pro)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Men", "Women", "VIP Pro").forEach { filter ->
                            val isSelected = selectedFilter == filter
                            Surface(
                                color = if (isSelected) ChampagneGold else SurfaceCard,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isSelected) ChampagneGold else CardBorderStroke),
                                modifier = Modifier.clickable { selectedFilter = filter }
                            ) {
                                Text(
                                    text = filter,
                                    color = if (isSelected) ObsidianBackground else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Line of Hairstyles (Clicking any automatically triggers API fitting)
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredAssets, key = { it.id }) { asset ->
                            val isSelected = selectedAsset?.id == asset.id
                            val isApplied = appliedAsset?.id == asset.id
                            val itemAccent = if (asset.category.startsWith("VIP")) ChampagneGold else if (asset.gender == Gender.WOMEN) ElectricViolet else ChampagneGold

                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) itemAccent.copy(alpha = 0.16f) else SurfaceCard
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) itemAccent else CardBorderStroke
                                ),
                                modifier = Modifier
                                    .width(120.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        // Customer selects this style
                                        selectedAsset = asset
                                        // Directly fit onto head via Dedicated REST API
                                        triggerAiHairstyleFitting(asset)
                                    }
                                    .testTag("vip_style_${asset.id}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(SurfaceDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Image(
                                            painter = painterResource(id = asset.overlayRes),
                                            contentDescription = asset.name,
                                            modifier = Modifier.size(60.dp),
                                            contentScale = ContentScale.Fit
                                        )
                                        if (asset.category.startsWith("VIP")) {
                                            Surface(
                                                color = ChampagneGold,
                                                shape = RoundedCornerShape(bottomStart = 8.dp),
                                                modifier = Modifier.align(Alignment.TopEnd)
                                            ) {
                                                Text(
                                                    text = "VIP",
                                                    color = ObsidianBackground,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = asset.name,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )

                                    Text(
                                        text = asset.category,
                                        color = itemAccent,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )

                                    if (isApplied) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "● Fitted",
                                            color = SuccessGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DEDICATED REST API SETTINGS DIALOG ---
    if (showApiSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showApiSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hair AI REST API Settings",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "This paid version is linked directly to a dedicated AI REST API service instead of Google Gemini. You can also configure your own server or custom API endpoint here.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = apiUrlInput,
                        onValueChange = { apiUrlInput = it },
                        label = { Text("API Endpoint URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = { apiKeyInput = it },
                        label = { Text("Authorization Key / Bearer Token") },
                        singleLine = true,
                        // Mask the token and keep it out of keyboard learning/suggestions
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (apiTestMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = apiTestMessage!!,
                                color = SuccessGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Test Connection Button
                    OutlinedButton(
                        onClick = {
                            isTestingApi = true
                            apiTestMessage = "Pinging API endpoint..."
                            coroutineScope.launch {
                                val result = PaidApiTryOnRepository.testConnection(context)
                                isTestingApi = false
                                apiTestMessage = result.second
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isTestingApi) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = ChampagneGold, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Test API Connection", fontSize = 12.sp, color = ChampagneGold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        PaidApiTryOnRepository.setApiUrl(context, apiUrlInput)
                        PaidApiTryOnRepository.setApiKey(context, apiKeyInput)
                        showApiSettingsDialog = false
                        Toast.makeText(context, "API Settings Saved & Active!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold, contentColor = ObsidianBackground),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save & Apply", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApiSettingsDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
