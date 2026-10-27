package com.example.ui.screens.tryon

import android.graphics.Bitmap
import android.graphics.Paint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.filled.ArrowLeft
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DetectedFaceLandmarks
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.model.HairstyleOverlayAsset
import com.example.data.model.UserPhotoSource
import com.example.engine.HairstyleOverlayEngine
import com.example.ui.components.UserPhotoDisplay
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.ImageSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Modern AR Hairstyle Try-On Screen.
 *
 * User Flow:
 * 1. User uploads a photo.
 * 2. Automatically detects face and head landmarks on-device (zero generative AI, zero API tokens).
 * 3. Automatically calculates head position, width, height, roll angle, forehead and temple positions.
 * 4. Automatically fits the selected hairstyle to the user's head.
 * 5. Hairstyle immediately appears on the uploaded photo.
 * 6. Tapping another hairstyle replaces it instantly without generating any AI image (0ms switch).
 * 7. Manual adjustment controls are hidden by default under an optional "Fine Tune" button.
 */
@Composable
fun PhotoPreviewScreen(
    userPhoto: UserPhotoSource?,
    hairstyle: HairstyleItem?,
    onChangeHairstyle: () -> Unit = {},
    onRetakePhoto: () -> Unit = {},
    onBack: () -> Unit = {},
    onShowToBarber: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val initialGender = hairstyle?.gender ?: Gender.MEN
    val themeAccent = if (initialGender == Gender.WOMEN) ElectricViolet else ChampagneGold

    // 1. Current Hairstyle Selection
    var selectedGenderFilter by remember { mutableStateOf(initialGender) }
    val defaultAsset = remember(hairstyle, initialGender) {
        if (hairstyle != null) {
            HairstyleOverlayEngine.getAssetForHairstyleItem(hairstyle)
        } else {
            HairstyleOverlayEngine.getAssetsForGender(initialGender).first()
        }
    }
    var selectedAsset by remember(defaultAsset) {
        mutableStateOf(defaultAsset)
    }

    LaunchedEffect(defaultAsset) {
        selectedAsset = defaultAsset
    }

    // 2. On-Device Face Landmark State
    var detectedLandmarks by remember { mutableStateOf<DetectedFaceLandmarks?>(null) }
    var isDetectingFace by remember { mutableStateOf(true) }
    var userBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // 3. Optional Fine-Tuning State (Hidden by default, automatic fitting is default)
    var isFineTuneExpanded by remember { mutableStateOf(false) }
    var userNudgeX by remember { mutableFloatStateOf(0f) }
    var userNudgeY by remember { mutableFloatStateOf(0f) }
    var userScaleMultiplier by remember { mutableFloatStateOf(1.0f) }
    var userRotationDegrees by remember { mutableFloatStateOf(0f) }

    // 4. View Modes
    var isShowingBefore by remember { mutableStateOf(false) }
    var isSavingImage by remember { mutableStateOf(false) }

    // Automatically run on-device face landmark detection as soon as user photo is provided
    LaunchedEffect(userPhoto) {
        if (userPhoto != null) {
            isDetectingFace = true
            withContext(Dispatchers.Default) {
                val bmp = HairstyleOverlayEngine.resolvePhotoToBitmap(context, userPhoto)
                userBitmap = bmp
                if (bmp != null) {
                    val landmarks = HairstyleOverlayEngine.detectFaceLandmarks(bmp)
                    detectedLandmarks = landmarks
                }
            }
            isDetectingFace = false
        }
    }

    // Pre-decode overlay bitmap for current hairstyle
    val currentOverlayBitmap = remember(selectedAsset) {
        selectedAsset?.let { HairstyleOverlayEngine.loadOverlayBitmap(context, it) }
    }

    // Reset user fine-tuning back to 100% automatic fit
    fun resetToAutoPosition() {
        userNudgeX = 0f
        userNudgeY = 0f
        userScaleMultiplier = 1.0f
        userRotationDegrees = 0f
        Toast.makeText(context, "Reset to automatic face fit", Toast.LENGTH_SHORT).show()
    }

    // Save composited look to device gallery
    fun saveCompositedLook() {
        val bmp = userBitmap ?: return
        val asset = selectedAsset ?: return
        val landmarks = detectedLandmarks ?: return

        coroutineScope.launch {
            isSavingImage = true
            try {
                val compositedBitmap = withContext(Dispatchers.Default) {
                    HairstyleOverlayEngine.composeOverlayWithLandmarks(
                        context = context,
                        userBitmap = bmp,
                        asset = asset,
                        landmarks = landmarks,
                        nudgeX = userNudgeX,
                        nudgeY = userNudgeY,
                        scaleMultiplier = userScaleMultiplier,
                        rotationNudge = userRotationDegrees
                    )
                }

                val savedUri = ImageSaver.saveBitmapToGallery(
                    context = context,
                    bitmap = compositedBitmap,
                    styleName = asset.name
                )

                if (savedUri != null) {
                    Toast.makeText(context, "Saved hairstyle preview to gallery!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Could not save photo to gallery.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error saving: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isSavingImage = false
            }
        }
    }

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
                .padding(bottom = 28.dp)
        ) {
            // --- TOP APP BAR ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("preview_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AR Hairstyle Try-On",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Instant Non-Generative AR Preview",
                        style = MaterialTheme.typography.labelSmall,
                        color = SuccessGreen,
                        fontSize = 11.sp
                    )
                }

                // Save Look Quick Button
                IconButton(
                    onClick = { saveCompositedLook() },
                    enabled = !isSavingImage && userBitmap != null,
                    modifier = Modifier.testTag("top_save_btn")
                ) {
                    if (isSavingImage) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = ChampagneGold,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Save Photo",
                            tint = ChampagneGold
                        )
                    }
                }
            }

            // --- INSTANT STATUS PILL: "Hairstyle automatically fitted" ---
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("preview_status_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = SuccessGreen.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isDetectingFace) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = SuccessGreen,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isDetectingFace) "Fitting hairstyle..." else "Hairstyle automatically fitted",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isDetectingFace) {
                                    "Detecting face & head landmarks..."
                                } else if (detectedLandmarks?.hasFace == true) {
                                    "Aligned to face landmarks & head angle"
                                } else {
                                    "Portrait calibrated auto-fit"
                                },
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Before / After Toggle Button
                    Surface(
                        color = if (isShowingBefore) ChampagneGold.copy(alpha = 0.2f) else SurfaceDark,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isShowingBefore) ChampagneGold else CardBorderStroke
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { isShowingBefore = !isShowingBefore }
                            .testTag("before_after_toggle_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Compare,
                                contentDescription = null,
                                tint = if (isShowingBefore) ChampagneGold else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isShowingBefore) "Before" else "After",
                                color = if (isShowingBefore) ChampagneGold else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- AR PREVIEW CARD: USER PHOTO + TRANSPARENT HAIRSTYLE OVERLAY ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.5.dp, CardBorderStroke, RoundedCornerShape(22.dp))
                    .testTag("interactive_overlay_card"),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                // Determine container aspect ratio naturally from user photo
                val photoRatio = if (userBitmap != null && userBitmap!!.height > 0) {
                    (userBitmap!!.width.toFloat() / userBitmap!!.height.toFloat()).coerceIn(0.65f, 1.35f)
                } else {
                    1.0f
                }

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(photoRatio)
                        .clip(RoundedCornerShape(22.dp))
                ) {
                    val containerWidthPx = constraints.maxWidth.toFloat()
                    val containerHeightPx = constraints.maxHeight.toFloat()

                    // Layer 1: Base User Photo (Completely untouched)
                    UserPhotoDisplay(
                        photoSource = userPhoto,
                        contentDescription = "User Photo",
                        modifier = Modifier.fillMaxSize()
                    )

                    // Layer 2: Transparent Hairstyle Overlay automatically positioned via detected landmarks
                    if (!isShowingBefore && selectedAsset != null && currentOverlayBitmap != null) {
                        val currentAsset = selectedAsset!!
                        val landmarks = detectedLandmarks ?: HairstyleOverlayEngine.createFallbackLandmarks(
                            width = (userBitmap?.width ?: containerWidthPx.toInt()),
                            height = (userBitmap?.height ?: containerHeightPx.toInt())
                        )

                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("overlay_image_element")
                        ) {
                            drawIntoCanvas { canvas ->
                                val nativeCanvas = canvas.nativeCanvas
                                val matrix = HairstyleOverlayEngine.calculateAutoMatrix(
                                    landmarks = landmarks,
                                    asset = currentAsset,
                                    overlayBitmap = currentOverlayBitmap,
                                    destinationWidth = size.width,
                                    destinationHeight = size.height,
                                    nudgeX = userNudgeX,
                                    nudgeY = userNudgeY,
                                    scaleMultiplier = userScaleMultiplier,
                                    rotationNudge = userRotationDegrees
                                )
                                val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                                nativeCanvas.drawBitmap(currentOverlayBitmap, matrix, paint)
                            }
                        }
                    }

                    // Status Pill Overlay
                    Surface(
                        color = Color.Black.copy(alpha = 0.70f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isShowingBefore) Icons.Default.Visibility else Icons.Default.Face,
                                contentDescription = null,
                                tint = if (isShowingBefore) SunsetRose else ChampagneGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isShowingBefore) "Original Photo" else "${selectedAsset?.name ?: "Hairstyle"}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- MEN / WOMEN SELECTION & HAIRSTYLE CAROUSEL ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Choose Hairstyle",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    // Gender Filter Tabs (Men / Women)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(Gender.MEN, Gender.WOMEN).forEach { gender ->
                            val isSelected = selectedGenderFilter == gender
                            val pillAccent = if (gender == Gender.MEN) ChampagneGold else ElectricViolet
                            Surface(
                                color = if (isSelected) pillAccent.copy(alpha = 0.2f) else SurfaceDark,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isSelected) pillAccent else CardBorderStroke),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        selectedGenderFilter = gender
                                        val firstOfGender = HairstyleOverlayEngine.getAssetsForGender(gender).firstOrNull()
                                        if (firstOfGender != null) {
                                            selectedAsset = firstOfGender
                                        }
                                    }
                                    .testTag("filter_${gender.name.lowercase()}")
                            ) {
                                Text(
                                    text = gender.displayName,
                                    color = if (isSelected) pillAccent else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Hairstyle Overlay Thumbnails
                val displayedAssets = remember(selectedGenderFilter) {
                    HairstyleOverlayEngine.getAssetsForGender(selectedGenderFilter)
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedAssets, key = { it.id }) { asset ->
                        val isSelected = selectedAsset?.id == asset.id
                        val itemAccent = if (asset.gender == Gender.WOMEN) ElectricViolet else ChampagneGold

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) itemAccent.copy(alpha = 0.15f) else SurfaceCard
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(
                                if (isSelected) 2.dp else 1.dp,
                                if (isSelected) itemAccent else CardBorderStroke
                            ),
                            modifier = Modifier
                                .width(115.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    // Instant 0ms automatic replacement without generating an AI image
                                    selectedAsset = asset
                                }
                                .testTag("style_item_${asset.id}")
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = asset.overlayRes),
                                        contentDescription = asset.name,
                                        modifier = Modifier.size(54.dp),
                                        contentScale = ContentScale.Fit
                                    )
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
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --- OPTIONAL FINE TUNE ACCORDION (Hidden by default, automatic fit is default) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Fine Tune Toggle Button
                Surface(
                    color = if (isFineTuneExpanded) SurfaceCard else SurfaceDark,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { isFineTuneExpanded = !isFineTuneExpanded }
                        .testTag("fine_tune_toggle_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = themeAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Fine Tune (Optional)",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = if (isFineTuneExpanded) "Hide" else "Show Controls",
                            color = ChampagneGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Expandable Controls (Left, Right, Up, Down, Size, Rotate)
                AnimatedVisibility(
                    visible = isFineTuneExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, CardBorderStroke),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .testTag("adjustment_controls_card")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Manual Nudge & Scale",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )

                                Text(
                                    text = "Reset to Auto Fit",
                                    color = ChampagneGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable { resetToAutoPosition() }
                                        .testTag("overlay_reset_btn")
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Position Controls (Left, Right, Up, Down)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ControlButton(
                                    text = "Left",
                                    icon = Icons.Default.ArrowLeft,
                                    onClick = { userNudgeX -= 0.015f },
                                    modifier = Modifier.weight(1f).testTag("overlay_move_left")
                                )
                                ControlButton(
                                    text = "Right",
                                    icon = Icons.Default.ArrowRight,
                                    onClick = { userNudgeX += 0.015f },
                                    modifier = Modifier.weight(1f).testTag("overlay_move_right")
                                )
                                ControlButton(
                                    text = "Up",
                                    icon = Icons.Default.ArrowUpward,
                                    onClick = { userNudgeY -= 0.015f },
                                    modifier = Modifier.weight(1f).testTag("overlay_move_up")
                                )
                                ControlButton(
                                    text = "Down",
                                    icon = Icons.Default.ArrowDownward,
                                    onClick = { userNudgeY += 0.015f },
                                    modifier = Modifier.weight(1f).testTag("overlay_move_down")
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Scale & Rotation Controls
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ControlButton(
                                    text = "+ Size",
                                    icon = Icons.Default.Add,
                                    onClick = { userScaleMultiplier = (userScaleMultiplier + 0.04f).coerceAtMost(2.0f) },
                                    modifier = Modifier.weight(1f).testTag("overlay_zoom_in")
                                )
                                ControlButton(
                                    text = "- Size",
                                    icon = Icons.Default.Remove,
                                    onClick = { userScaleMultiplier = (userScaleMultiplier - 0.04f).coerceAtLeast(0.6f) },
                                    modifier = Modifier.weight(1f).testTag("overlay_zoom_out")
                                )
                                ControlButton(
                                    text = "↺ -2°",
                                    icon = Icons.Default.RotateLeft,
                                    onClick = { userRotationDegrees -= 2f },
                                    modifier = Modifier.weight(1f).testTag("overlay_rotate_left")
                                )
                                ControlButton(
                                    text = "↻ +2°",
                                    icon = Icons.Default.RotateRight,
                                    onClick = { userRotationDegrees += 2f },
                                    modifier = Modifier.weight(1f).testTag("overlay_rotate_right")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BOTTOM ACTIONS: SAVE PHOTO & CHANGE PHOTO ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Save High-Res Composited Look to Gallery
                Button(
                    onClick = { saveCompositedLook() },
                    enabled = !isSavingImage && userBitmap != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_look_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = themeAccent,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    if (isSavingImage) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = ObsidianBackground,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Saving to Gallery...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Look to Gallery",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Show to Barber Button (Front, Side, Back views for barber)
                if (onShowToBarber != null && hairstyle != null) {
                    OutlinedButton(
                        onClick = { onShowToBarber(hairstyle.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("preview_show_barber_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, ElectricViolet),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Show to Barber",
                            tint = ElectricViolet,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Show to Barber (360° Specs)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                    }
                }

                // Change Photo Button
                OutlinedButton(
                    onClick = onRetakePhoto,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("retake_photo_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = BorderStroke(1.dp, CardBorderStroke),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Change Photo",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ControlButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CardBorderStroke),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = TextPrimary,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
