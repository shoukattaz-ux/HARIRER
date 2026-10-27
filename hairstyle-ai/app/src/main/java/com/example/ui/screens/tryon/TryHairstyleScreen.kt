package com.example.ui.screens.tryon

import android.Manifest
import android.widget.Toast
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import com.example.util.GallerySaver
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.R
import com.example.ar.ArFaceTracker
import com.example.ar.ArHeadTransform
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.model.HairstyleOverlayAsset
import com.example.data.model.UserPhotoSource
import com.example.engine.HairstyleOverlayEngine
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.InputStream

enum class TryOnMode {
    LIVE_CAMERA,
    UPLOAD_PHOTO
}

/**
 * Live Front-Camera AR Hairstyle Try-On Screen with dual mode support:
 * 1. LIVE FRONT CAMERA (Default, Snapchat-style real-time 3D head tracking)
 * 2. PHOTO UPLOAD (Same automatic face detection & hairstyle fitting on uploaded photos)
 */
@Composable
fun TryHairstyleScreen(
    hairstyle: HairstyleItem?,
    initialMode: TryOnMode = TryOnMode.LIVE_CAMERA,
    onBack: () -> Unit,
    onPhotoSelected: (UserPhotoSource) -> Unit,
    onChangeHairstyle: () -> Unit,
    onShowToBarber: ((String) -> Unit)? = null,
    /** Sends a clean camera photo plus the selected style to AI for a photorealistic result. */
    onMakeRealistic: ((Bitmap, HairstyleOverlayAsset) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Active Try-On Mode: LIVE_CAMERA (primary) vs UPLOAD_PHOTO (secondary)
    var currentMode by remember { mutableStateOf(initialMode) }

    // Camera permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission && currentMode == TryOnMode.LIVE_CAMERA) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Active Category & Hairstyle
    var selectedGender by remember {
        mutableStateOf(hairstyle?.gender ?: Gender.MEN)
    }

    val defaultAsset = remember(selectedGender, hairstyle) {
        if (hairstyle != null && hairstyle.gender == selectedGender) {
            HairstyleOverlayEngine.getAssetForHairstyleItem(hairstyle)
        } else {
            if (selectedGender == Gender.MEN) {
                HairstyleOverlayEngine.getAssetById("overlay_m_natural_quiff")
                    ?: HairstyleOverlayEngine.getAssetsForGender(Gender.MEN).first()
            } else {
                HairstyleOverlayEngine.getAssetById("overlay_w_bob")
                    ?: HairstyleOverlayEngine.getAssetsForGender(Gender.WOMEN).first()
            }
        }
    }

    var activeAsset by remember(defaultAsset) { mutableStateOf<HairstyleOverlayAsset?>(defaultAsset) }

    LaunchedEffect(defaultAsset) {
        activeAsset = defaultAsset
    }

    // Live Camera state
    var useFrontCamera by remember { mutableStateOf(true) }
    var liveTransform by remember { mutableStateOf<ArHeadTransform?>(null) }
    var showDebugOverlay by remember { mutableStateOf(false) }

    // Photo Upload mode state
    var uploadedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var photoTransform by remember { mutableStateOf<ArHeadTransform?>(null) }
    var isDetectingPhotoFace by remember { mutableStateOf(false) }

    // Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentMode = TryOnMode.UPLOAD_PHOTO
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bmp = BitmapFactory.decodeStream(inputStream)
                uploadedBitmap = bmp
                isDetectingPhotoFace = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(initialMode) {
        currentMode = initialMode
        if (initialMode == TryOnMode.UPLOAD_PHOTO && uploadedBitmap == null) {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    }

    // Fine-tune controls
    var isFineTuneOpen by remember { mutableStateOf(false) }
    var fineTuneScale by remember { mutableFloatStateOf(1.0f) }
    var fineTuneOffsetY by remember { mutableFloatStateOf(0f) }
    var fineTuneOffsetX by remember { mutableFloatStateOf(0f) }
    var selectedColorTint by remember { mutableStateOf<Int?>(null) }

    // Save to Gallery states and triggers
    var liveCameraCaptureTrigger by remember { mutableStateOf<(() -> Bitmap?)?>(null) }
    var liveRawCaptureTrigger by remember { mutableStateOf<(() -> Bitmap?)?>(null) }
    var photoUploadCaptureTrigger by remember { mutableStateOf<(() -> Bitmap?)?>(null) }

    var isSavingImage by remember { mutableStateOf(false) }
    var savedImageUri by remember { mutableStateOf<Uri?>(null) }
    var savedBitmapPreview by remember { mutableStateOf<Bitmap?>(null) }
    var showSavedDialog by remember { mutableStateOf(false) }

    val performSaveToGallery: () -> Unit = {
        val bitmapToSave = if (currentMode == TryOnMode.LIVE_CAMERA) {
            liveCameraCaptureTrigger?.invoke()
        } else {
            photoUploadCaptureTrigger?.invoke()
        }

        if (bitmapToSave != null) {
            isSavingImage = true
            try {
                val uri = GallerySaver.saveBitmapToGallery(context, bitmapToSave)
                if (uri != null) {
                    savedImageUri = uri
                    savedBitmapPreview = bitmapToSave
                    showSavedDialog = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSavingImage = false
            }
        }
    }

    val availableAssets = remember(selectedGender) {
        HairstyleOverlayEngine.getAssetsForGender(selectedGender)
    }

    val themeAccent = if (selectedGender == Gender.WOMEN) ElectricViolet else ChampagneGold

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground)
    ) {
        // ==========================================
        // 1. MAIN VIEWPORT (LIVE CAMERA OR PHOTO)
        // ==========================================
        when (currentMode) {
            TryOnMode.LIVE_CAMERA -> {
                if (hasCameraPermission) {
                    LiveArCameraView(
                        activeAsset = activeAsset,
                        useFrontCamera = useFrontCamera,
                        fineTuneScale = fineTuneScale,
                        fineTuneOffsetX = fineTuneOffsetX,
                        fineTuneOffsetY = fineTuneOffsetY,
                        colorTint = selectedColorTint,
                        showDebugOverlay = showDebugOverlay,
                        onTransformChanged = { transform ->
                            liveTransform = transform
                        },
                        onRegisterCapture = { trigger ->
                            liveCameraCaptureTrigger = trigger
                        },
                        onRegisterRawCapture = { trigger ->
                            liveRawCaptureTrigger = trigger
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CameraPermissionRequiredCard(
                        onGrantPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        onUploadPhoto = {
                            currentMode = TryOnMode.UPLOAD_PHOTO
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onBack = onBack,
                        accentColor = themeAccent
                    )
                }
            }

            TryOnMode.UPLOAD_PHOTO -> {
                PhotoUploadArView(
                    bitmap = uploadedBitmap,
                    activeAsset = activeAsset,
                    fineTuneScale = fineTuneScale,
                    fineTuneOffsetX = fineTuneOffsetX,
                    fineTuneOffsetY = fineTuneOffsetY,
                    colorTint = selectedColorTint,
                    onPickPhoto = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    onRegisterCapture = { trigger ->
                        photoUploadCaptureTrigger = trigger
                    },
                    accentColor = themeAccent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Top Gradient Scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black.copy(alpha = 0.40f),
                            Color.Transparent
                        )
                    )
                )
                .align(Alignment.TopCenter)
        )

        // Bottom Gradient Scrim
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.65f),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
                .align(Alignment.BottomCenter)
        )

        // ==========================================
        // 2. TOP HEADER & MODE SELECTOR
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(SurfaceDark.copy(alpha = 0.85f))
                        .border(1.dp, CardBorderStroke, CircleShape)
                        .testTag("ar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Title and Tracking Status Indicator (weight ensures right actions never get squished)
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Hair Try-On",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )

                    if (currentMode == TryOnMode.LIVE_CAMERA) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(top = 2.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (liveTransform != null) Color(0xFF1B5E20).copy(alpha = 0.85f)
                                    else Color(0xFFE65100).copy(alpha = 0.80f)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (liveTransform != null) Color(0xFF00E676)
                                        else Color(0xFFFFD600)
                                    )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (liveTransform != null) "Tracked" else "Position Face",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    } else {
                        Text(
                            text = "Photo Mode",
                            color = themeAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Top Right Action Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (currentMode == TryOnMode.LIVE_CAMERA) {
                        // Flip Camera (Front / Back)
                        IconButton(
                            onClick = { useFrontCamera = !useFrontCamera },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark.copy(alpha = 0.85f))
                                .border(1.dp, CardBorderStroke, CircleShape)
                                .testTag("ar_flip_camera_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlipCameraAndroid,
                                contentDescription = "Switch Camera",
                                tint = themeAccent,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    // Fine Tune Toggle
                    IconButton(
                        onClick = { isFineTuneOpen = !isFineTuneOpen },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFineTuneOpen) themeAccent.copy(alpha = 0.3f)
                                else SurfaceDark.copy(alpha = 0.85f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isFineTuneOpen) themeAccent else CardBorderStroke,
                                shape = CircleShape
                            )
                            .testTag("ar_fine_tune_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Fine Tune Overlay",
                            tint = if (isFineTuneOpen) themeAccent else TextPrimary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Show to Barber Button
                    if (onShowToBarber != null && hairstyle != null) {
                        IconButton(
                            onClick = { onShowToBarber(hairstyle.id) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SurfaceDark.copy(alpha = 0.85f))
                                .border(1.dp, CardBorderStroke, CircleShape)
                                .testTag("ar_show_barber_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = "Show to Barber",
                                tint = themeAccent,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    // Save Button (Prominent, always full width, never squeezed)
                    Button(
                        onClick = performSaveToGallery,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("save_tryon_button")
                    ) {
                        if (isSavingImage) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Save",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // DUAL MODE SWITCHER: [ 📹 Live Front Camera ] [ 🖼️ Upload Photo ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = SurfaceDark.copy(alpha = 0.90f),
                    border = BorderStroke(1.dp, CardBorderStroke)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        ModeTogglePill(
                            title = "Live Camera",
                            icon = Icons.Default.Videocam,
                            isSelected = currentMode == TryOnMode.LIVE_CAMERA,
                            activeColor = themeAccent,
                            onClick = { currentMode = TryOnMode.LIVE_CAMERA }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        ModeTogglePill(
                            title = "Upload Photo",
                            icon = Icons.Default.PhotoLibrary,
                            isSelected = currentMode == TryOnMode.UPLOAD_PHOTO,
                            activeColor = themeAccent,
                            onClick = {
                                currentMode = TryOnMode.UPLOAD_PHOTO
                                if (uploadedBitmap == null) {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        // ==========================================
        // 3. BOTTOM PANEL: CAROUSEL & CONTROLS
        // ==========================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        ) {
            // Optional Fine-Tune Drawer (hidden by default)
            AnimatedVisibility(
                visible = isFineTuneOpen,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                FineTunePanel(
                    scale = fineTuneScale,
                    onScaleChange = { fineTuneScale = it },
                    offsetY = fineTuneOffsetY,
                    onOffsetYChange = { fineTuneOffsetY = it },
                    selectedColorTint = selectedColorTint,
                    onColorTintChange = { selectedColorTint = it },
                    onReset = {
                        fineTuneScale = 1.0f
                        fineTuneOffsetY = 0f
                        fineTuneOffsetX = 0f
                        selectedColorTint = null
                    },
                    accentColor = themeAccent,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Photorealistic AI version of the current look (the live overlay is only a quick preview)
            val makeRealistic = onMakeRealistic
            val assetForAi = activeAsset
            if (currentMode == TryOnMode.LIVE_CAMERA && makeRealistic != null && assetForAi != null) {
                Button(
                    onClick = {
                        val frame = liveRawCaptureTrigger?.invoke()
                        if (frame != null) {
                            makeRealistic(frame, assetForAi)
                        } else {
                            Toast.makeText(context, "Camera not ready yet. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet, contentColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .height(46.dp)
                        .testTag("ar_make_realistic_btn")
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Make It Realistic (AI)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }

            // Gender Category Selector: [ Men's Styles ] [ Women's Styles ]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SurfaceDark.copy(alpha = 0.90f),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        GenderChip(
                            label = "Men's Styles",
                            isSelected = selectedGender == Gender.MEN,
                            activeColor = ChampagneGold,
                            onClick = {
                                selectedGender = Gender.MEN
                                activeAsset = HairstyleOverlayEngine.getAssetById("overlay_m_natural_quiff")
                                    ?: HairstyleOverlayEngine.getAssetsForGender(Gender.MEN).first()
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        GenderChip(
                            label = "Women's Styles",
                            isSelected = selectedGender == Gender.WOMEN,
                            activeColor = ElectricViolet,
                            onClick = {
                                selectedGender = Gender.WOMEN
                                activeAsset = HairstyleOverlayEngine.getAssetById("overlay_w_bob")
                                    ?: HairstyleOverlayEngine.getAssetsForGender(Gender.WOMEN).first()
                            }
                        )
                    }
                }
            }

            // Hairstyle Cards Carousel
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("ar_hairstyle_carousel")
            ) {
                items(availableAssets, key = { it.id }) { asset ->
                    val isSelected = activeAsset?.id == asset.id
                    ArHairstyleCard(
                        asset = asset,
                        isSelected = isSelected,
                        accentColor = themeAccent,
                        onClick = {
                            // Instant local overlay replacement - NO Generative AI called!
                            activeAsset = asset
                        }
                    )
                }
            }
        }

        // Success Dialog when image is saved to Gallery
        if (showSavedDialog && savedBitmapPreview != null) {
            AlertDialog(
                onDismissRequest = { showSavedDialog = false },
                containerColor = SurfaceCard,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Saved to Gallery!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, CardBorderStroke),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            Image(
                                bitmap = savedBitmapPreview!!.asImageBitmap(),
                                contentDescription = "Saved Hairstyle Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Your hairstyle photo has been saved to your device's Pictures/HairLook album.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val uri = savedImageUri
                            if (uri != null) {
                                GallerySaver.shareImage(context, uri)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showSavedDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done", color = TextPrimary)
                    }
                }
            )
        }
    }
}

/**
 * High-performance Photo Upload AR Try-On View.
 * Runs on-device ML Kit Face Landmarker on the uploaded photo, and composites
 * the selected hairstyle asset at the exact anatomical hairline position.
 */
@Composable
private fun PhotoUploadArView(
    bitmap: Bitmap?,
    activeAsset: HairstyleOverlayAsset?,
    fineTuneScale: Float,
    fineTuneOffsetX: Float,
    fineTuneOffsetY: Float,
    colorTint: Int?,
    onPickPhoto: () -> Unit,
    onRegisterCapture: ((() -> Bitmap?) -> Unit)? = null,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var viewWidth by remember { mutableFloatStateOf(1080f) }
    var viewHeight by remember { mutableFloatStateOf(1920f) }

    var detectedTransform by remember { mutableStateOf<ArHeadTransform?>(null) }
    var isDetecting by remember { mutableStateOf(false) }

    // Direct finger gesture states
    var userDragOffsetX by remember { mutableFloatStateOf(0f) }
    var userDragOffsetY by remember { mutableFloatStateOf(0f) }
    var userScale by remember { mutableFloatStateOf(1.0f) }
    var userRotation by remember { mutableFloatStateOf(0f) }
    var isFingerTouching by remember { mutableStateOf(false) }

    // Run ML Kit on uploaded photo
    LaunchedEffect(bitmap, viewWidth, viewHeight) {
        val bmp = bitmap
        if (bmp != null && viewWidth > 0 && viewHeight > 0) {
            isDetecting = true
            ArFaceTracker.processStaticBitmap(bmp, viewWidth, viewHeight) { transform ->
                detectedTransform = transform
                isDetecting = false
            }
        } else {
            detectedTransform = null
        }
    }

    // Cache hair bitmap
    var cachedAssetId by remember { mutableStateOf<String?>(null) }
    var cachedHairBitmap by remember { mutableStateOf<Bitmap?>(null) }

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

    LaunchedEffect(activeAsset?.id) {
        if (activeAsset != null && activeAsset.id != cachedAssetId) {
            try {
                cachedHairBitmap = HairstyleOverlayEngine.loadOverlayBitmap(context, activeAsset)
                cachedAssetId = activeAsset.id
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun captureCompositedPhoto(): Bitmap? {
        val bmp = bitmap ?: return null
        val result = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(result)
        canvas.drawBitmap(bmp, 0f, 0f, null)

        val transform = detectedTransform ?: ArHeadTransform(
            hairlineX = viewWidth * 0.50f,
            hairlineY = viewHeight * 0.36f,
            headWidth = viewWidth * 0.44f,
            headHeight = viewHeight * 0.36f,
            rollDegrees = 0f
        )
        val hairBitmap = cachedHairBitmap
        val asset = activeAsset

        if (hairBitmap != null && asset != null && viewWidth > 0 && viewHeight > 0) {
            val scaleFactor = maxOf(viewWidth / bmp.width.toFloat(), viewHeight / bmp.height.toFloat())
            val postScaleWidthOffset = (viewWidth - (bmp.width.toFloat() * scaleFactor)) / 2f
            val postScaleHeightOffset = (viewHeight - (bmp.height.toFloat() * scaleFactor)) / 2f

            val photoTransform = transform.copy(
                hairlineX = (transform.hairlineX - postScaleWidthOffset) / scaleFactor,
                hairlineY = (transform.hairlineY - postScaleHeightOffset) / scaleFactor,
                headWidth = transform.headWidth / scaleFactor,
                headHeight = transform.headHeight / scaleFactor
            )

            val render = HairstyleOverlayEngine.calculateUnifiedRender(
                transform = photoTransform,
                asset = asset,
                hairBitmap = hairBitmap,
                userScale = fineTuneScale * userScale,
                userOffsetX = fineTuneOffsetX + if (transform.headWidth > 0f) (userDragOffsetX / transform.headWidth) else 0f,
                userOffsetY = fineTuneOffsetY + if (transform.headHeight > 0f) (userDragOffsetY / transform.headHeight) else 0f,
                userRotation = userRotation
            )

            val renderPaint = Paint().apply {
                isAntiAlias = true
                isFilterBitmap = true
                isDither = true
                if (colorTint != null && colorTint != 0) {
                    colorFilter = PorterDuffColorFilter(colorTint, PorterDuff.Mode.SRC_ATOP)
                }
            }

            canvas.drawBitmap(hairBitmap, render.occlusionMatrix, occlusionPaint)
            canvas.drawBitmap(hairBitmap, render.hairMatrix, renderPaint)
        }

        return result
    }

    LaunchedEffect(onRegisterCapture, bitmap, activeAsset, fineTuneScale, fineTuneOffsetX, fineTuneOffsetY, userDragOffsetX, userDragOffsetY, userScale, userRotation, colorTint, viewWidth, viewHeight) {
        onRegisterCapture?.invoke { captureCompositedPhoto() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                if (size.width > 0 && size.height > 0) {
                    viewWidth = size.width.toFloat()
                    viewHeight = size.height.toFloat()
                }
            }
    ) {
        if (bitmap == null) {
            // Empty State: Prompt user to choose photo
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, CardBorderStroke)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Photo",
                                tint = accentColor,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Choose Photo for Try-On",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Select a clear portrait photo from your gallery. Hairstyles will automatically fit onto your head in the photo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onPickPhoto,
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Select from Gallery",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Render Photo + Hairstyle Canvas with Finger Gestures
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(bitmap) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            isFingerTouching = true
                            userDragOffsetX += pan.x
                            userDragOffsetY += pan.y
                            userScale = (userScale * zoom).coerceIn(0.4f, 3.5f)
                            userRotation = (userRotation + rotation) % 360f
                        }
                    }
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "User Photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Reliable auto-fit: use ML Kit detection or default portrait center
                    val transform = detectedTransform ?: ArHeadTransform(
                        hairlineX = viewWidth * 0.50f,
                        hairlineY = viewHeight * 0.36f,
                        headWidth = viewWidth * 0.44f,
                        headHeight = viewHeight * 0.36f,
                        rollDegrees = 0f
                    )
                    val hairBitmap = cachedHairBitmap
                    val asset = activeAsset

                    if (hairBitmap != null && asset != null) {
                        val render = HairstyleOverlayEngine.calculateUnifiedRender(
                            transform = transform,
                            asset = asset,
                            hairBitmap = hairBitmap,
                            userScale = fineTuneScale * userScale,
                            userOffsetX = fineTuneOffsetX + if (transform.headWidth > 0f) (userDragOffsetX / transform.headWidth) else 0f,
                            userOffsetY = fineTuneOffsetY + if (transform.headHeight > 0f) (userDragOffsetY / transform.headHeight) else 0f,
                            userRotation = userRotation
                        )

                        drawContext.canvas.nativeCanvas.drawBitmap(hairBitmap, render.occlusionMatrix, occlusionPaint)
                        drawContext.canvas.nativeCanvas.drawBitmap(hairBitmap, render.hairMatrix, paint)
                    }
                }

                // Floating Adjustment Micro-tools on the right edge
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Change Photo Button
                    IconButton(
                        onClick = onPickPhoto,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark.copy(alpha = 0.88f))
                            .border(1.dp, CardBorderStroke, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Change Photo",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Reset Button
                    IconButton(
                        onClick = {
                            userDragOffsetX = 0f
                            userDragOffsetY = 0f
                            userScale = 1.0f
                            userRotation = 0f
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark.copy(alpha = 0.88f))
                            .border(1.dp, accentColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Position",
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Zoom In (+)
                    IconButton(
                        onClick = {
                            userScale = (userScale * 1.1f).coerceAtMost(3.5f)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark.copy(alpha = 0.88f))
                            .border(1.dp, CardBorderStroke, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Zoom Out (-)
                    IconButton(
                        onClick = {
                            userScale = (userScale * 0.9f).coerceAtLeast(0.4f)
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark.copy(alpha = 0.88f))
                            .border(1.dp, CardBorderStroke, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomOut,
                            contentDescription = "Zoom Out",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Rotate Left
                    IconButton(
                        onClick = {
                            userRotation = (userRotation - 6f) % 360f
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark.copy(alpha = 0.88f))
                            .border(1.dp, CardBorderStroke, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RotateLeft,
                            contentDescription = "Rotate Left",
                            tint = TextPrimary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Rotate Right
                    IconButton(
                        onClick = {
                            userRotation = (userRotation + 6f) % 360f
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceDark.copy(alpha = 0.88f))
                            .border(1.dp, CardBorderStroke, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RotateRight,
                            contentDescription = "Rotate Right",
                            tint = TextPrimary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Top Mode Selector Pill (Live Camera vs Upload Photo).
 */
@Composable
private fun ModeTogglePill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.Black else TextSecondary
            )
        }
    }
}

/**
 * Individual hairstyle card in the live AR carousel.
 * Tapping immediately applies the hairstyle asset on the user's live front-camera feed.
 */
@Composable
private fun ArHairstyleCard(
    asset: HairstyleOverlayAsset,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(108.dp)
            .height(118.dp)
            .clickable { onClick() }
            .testTag("ar_card_${asset.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceCard else SurfaceDark.copy(alpha = 0.85f)
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, accentColor)
        } else {
            BorderStroke(1.dp, CardBorderStroke)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Hairstyle Visual Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E24)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = asset.overlayRes),
                    contentDescription = asset.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                )

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Active",
                            tint = Color.Black,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Hairstyle Label
            Text(
                text = asset.name,
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Category toggle pill for switching between Men and Women hairstyles.
 */
@Composable
private fun GenderChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.Black else TextSecondary
        )
    }
}

/**
 * Optional Fine-Tune Panel allowing users to adjust vertical hairline position,
 * hair scale, or natural color tints. Hidden by default.
 */
@Composable
private fun FineTunePanel(
    scale: Float,
    onScaleChange: (Float) -> Unit,
    offsetY: Float,
    onOffsetYChange: (Float) -> Unit,
    selectedColorTint: Int?,
    onColorTintChange: (Int?) -> Unit,
    onReset: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.95f)),
        border = BorderStroke(1.dp, CardBorderStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fine Tune AR Fit",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onReset,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceDark,
                        contentColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = "Reset",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Auto Reset", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hairline Position Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hairline Height",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.width(95.dp)
                )
                Slider(
                    value = offsetY,
                    onValueChange = onOffsetYChange,
                    valueRange = -0.08f..0.08f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Hair Scale Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hair Scale",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.width(95.dp)
                )
                Slider(
                    value = scale,
                    onValueChange = onScaleChange,
                    valueRange = 0.85f..1.15f,
                    colors = SliderDefaults.colors(
                        thumbColor = accentColor,
                        activeTrackColor = accentColor,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Hair Color Tint Selector
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hair Color Tone",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            val colorTints = listOf(
                Pair("Natural", null),
                Pair("Espresso", 0xFF2A1E17.toInt()),
                Pair("Chestnut", 0xFF5A3825.toInt()),
                Pair("Honey Blonde", 0xFFB58A4A.toInt()),
                Pair("Silver Ash", 0xFF7A7E85.toInt()),
                Pair("Copper Ruby", 0xFF8A3324.toInt())
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colorTints.forEach { (name, tint) ->
                    val isColorSelected = selectedColorTint == tint
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isColorSelected) accentColor.copy(alpha = 0.25f) else SurfaceDark)
                            .border(
                                width = 1.dp,
                                color = if (isColorSelected) accentColor else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onColorTintChange(tint) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            color = if (isColorSelected) accentColor else TextSecondary,
                            fontWeight = if (isColorSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

/**
 * Friendly card shown when camera permission is denied, providing easy one-tap grant
 * and secondary option to upload photo.
 */
@Composable
private fun CameraPermissionRequiredCard(
    onGrantPermission: () -> Unit,
    onUploadPhoto: () -> Unit,
    onBack: () -> Unit,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, CardBorderStroke)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Camera Permission",
                        tint = accentColor,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Front Camera Access Needed",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Live AR Try-On tracks your head movement in real time on your device. Camera frames are processed 100% locally and never sent to cloud servers.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onGrantPermission,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Allow Camera Access",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onUploadPhoto,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Or Choose Photo from Gallery",
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Go Back",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onBack() }
                )
            }
        }
    }
}
