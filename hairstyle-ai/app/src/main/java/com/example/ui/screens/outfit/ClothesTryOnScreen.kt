package com.example.ui.screens.outfit

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ar.ArFaceTracker
import com.example.ar.FaceExtractor
import com.example.data.model.OutfitItem
import com.example.data.model.OutfitRepository
import com.example.ui.theme.DarkCardBorderStroke
import com.example.ui.theme.HairLookBlueAccent
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.InputStream

/**
 * Clean, unobstructed Showroom Mannequin Try-On Studio.
 * Live camera has been removed as requested; focus is 100% on uploading a photo
 * and seamlessly attaching the user's segmented face onto the real mannequin.
 * All clutter over the face (chips, badges, alerts) has been completely eliminated.
 */
@Composable
fun ClothesTryOnScreen(
    initialOutfitId: String? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val context = LocalContext.current
    val allOutfits = OutfitRepository.outfits
    val initialItem = allOutfits.find { it.id == initialOutfitId } ?: allOutfits.first()
    var activeOutfit by remember { mutableStateOf(initialItem) }

    var viewWidth by remember { mutableFloatStateOf(1080f) }
    var viewHeight by remember { mutableFloatStateOf(1920f) }

    // User photo & segmented face state
    var uploadedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var extractedFace by remember { mutableStateOf<FaceExtractor.ExtractedFace?>(null) }
    var isDetectingPhoto by remember { mutableStateOf(false) }

    // Micro-adjustments: pan and zoom gestures
    var userScale by remember { mutableFloatStateOf(1.0f) }
    var userOffsetX by remember { mutableFloatStateOf(0f) }
    var userOffsetY by remember { mutableFloatStateOf(0f) }

    // Cached outfit bitmap
    var cachedOutfitBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cachedOutfitId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(activeOutfit.id) {
        if (activeOutfit.id != cachedOutfitId) {
            try {
                val d = ContextCompat.getDrawable(context, activeOutfit.drawableRes)
                if (d != null) {
                    val w = if (d.intrinsicWidth > 0) d.intrinsicWidth else 800
                    val h = if (d.intrinsicHeight > 0) d.intrinsicHeight else 1400
                    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                    val c = Canvas(bmp)
                    d.setBounds(0, 0, w, h)
                    d.draw(c)
                    cachedOutfitBitmap = bmp
                    cachedOutfitId = activeOutfit.id
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val stream: InputStream? = context.contentResolver.openInputStream(uri)
                val bmp = BitmapFactory.decodeStream(stream)
                uploadedBitmap = bmp
                isDetectingPhoto = true
                if (bmp != null) {
                    ArFaceTracker.extractFaceFromBitmap(bmp) { face ->
                        extractedFace = face
                        isDetectingPhoto = false
                        userScale = 1.0f
                        userOffsetX = 0f
                        userOffsetY = 0f
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                isDetectingPhoto = false
            }
        }
    }

    // Save states
    var isSaving by remember { mutableStateOf(false) }
    var showSavedDialog by remember { mutableStateOf(false) }
    var savedBitmapPreview by remember { mutableStateOf<Bitmap?>(null) }

    // Capture & Save composited showroom mannequin photo
    val performSaveOutfit: () -> Unit = {
        isSaving = true
        try {
            val outfitBmp = cachedOutfitBitmap
            if (outfitBmp != null) {
                val resultWidth = 1080
                val resultHeight = 1920
                val resultBmp = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(resultBmp)

                // 1. Showroom studio background
                val bgPaint = Paint().apply { color = 0xFF090D16.toInt() }
                canvas.drawRect(0f, 0f, resultWidth.toFloat(), resultHeight.toFloat(), bgPaint)

                // 2. Draw Mannequin
                val isFullOutfit = outfitBmp.height > outfitBmp.width * 1.25f
                val mannequinDisplayHeight = resultHeight * 0.76f
                val mannequinScale = mannequinDisplayHeight / outfitBmp.height.toFloat()
                val mannequinWidth = outfitBmp.width * mannequinScale
                val mannequinLeft = (resultWidth - mannequinWidth) * 0.5f
                val mannequinTop = resultHeight * 0.16f

                val mMatrix = Matrix().apply {
                    postScale(mannequinScale, mannequinScale)
                    postTranslate(mannequinLeft, mannequinTop)
                }
                val p = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                canvas.drawBitmap(outfitBmp, mMatrix, p)

                // 3. Draw User's Segmented Face on Mannequin Collar
                val face = extractedFace
                if (face != null) {
                    val collarRelativeY = if (isFullOutfit) 228f else outfitBmp.height * 0.30f
                    val collarX = resultWidth * 0.5f + (userOffsetX * (resultWidth.toFloat() / viewWidth))
                    val collarY = mannequinTop + (collarRelativeY * mannequinScale) + (userOffsetY * (resultHeight.toFloat() / viewHeight))

                    val targetFaceWidth = 220f * mannequinScale * userScale
                    val faceScale = targetFaceWidth / face.faceWidth

                    val faceMatrix = Matrix().apply {
                        postTranslate(-face.faceWidth * 0.5f, -face.faceHeight * face.chinRelativeY)
                        postScale(faceScale, faceScale)
                        postRotate(face.rollDegrees)
                        postTranslate(collarX, collarY)
                    }
                    canvas.drawBitmap(face.faceBitmap, faceMatrix, p)
                }

                // 4. Save to MediaStore
                val filename = "StyleStudio_Outfit_${System.currentTimeMillis()}.jpg"
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/StyleStudio")
                        put(MediaStore.Images.Media.IS_PENDING, 1)
                    }
                }

                val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { stream ->
                        resultBmp.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear()
                        values.put(MediaStore.Images.Media.IS_PENDING, 0)
                        context.contentResolver.update(uri, values, null, null)
                    }
                    savedBitmapPreview = resultBmp
                    showSavedDialog = true
                    Toast.makeText(context, "Saved to Gallery!", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isSaving = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090D16))
            .onSizeChanged { size ->
                if (size.width > 0 && size.height > 0) {
                    viewWidth = size.width.toFloat()
                    viewHeight = size.height.toFloat()
                }
            }
    ) {
        // 1. SHOWROOM MANNEQUIN STUDIO (Touch gestures pan & zoom the face onto collar)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(activeOutfit.id) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        userOffsetX += pan.x
                        userOffsetY += pan.y
                        userScale = (userScale * zoom).coerceIn(0.5f, 2.5f)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val outfitBmp = cachedOutfitBitmap
                if (outfitBmp != null) {
                    val isFullOutfit = outfitBmp.height > outfitBmp.width * 1.25f

                    // 1. Draw Mannequin centered in showroom
                    val mannequinDisplayHeight = size.height * 0.74f
                    val mannequinScale = mannequinDisplayHeight / outfitBmp.height.toFloat()
                    val mannequinWidth = outfitBmp.width * mannequinScale
                    val mannequinLeft = (size.width - mannequinWidth) * 0.5f
                    val mannequinTop = size.height * 0.16f

                    val mMatrix = Matrix().apply {
                        postScale(mannequinScale, mannequinScale)
                        postTranslate(mannequinLeft, mannequinTop)
                    }
                    val p = Paint().apply {
                        isAntiAlias = true
                        isFilterBitmap = true
                    }
                    drawContext.canvas.nativeCanvas.drawBitmap(outfitBmp, mMatrix, p)

                    // 2. Draw User's Segmented Face on Mannequin Collar
                    val face = extractedFace
                    if (face != null) {
                        val collarRelativeY = if (isFullOutfit) 228f else outfitBmp.height * 0.30f
                        val collarX = size.width * 0.5f + userOffsetX
                        val collarY = mannequinTop + (collarRelativeY * mannequinScale) + userOffsetY

                        val targetFaceWidth = 220f * mannequinScale * userScale
                        val faceScale = targetFaceWidth / face.faceWidth

                        val faceMatrix = Matrix().apply {
                            postTranslate(-face.faceWidth * 0.5f, -face.faceHeight * face.chinRelativeY)
                            postScale(faceScale, faceScale)
                            postRotate(face.rollDegrees)
                            postTranslate(collarX, collarY)
                        }
                        drawContext.canvas.nativeCanvas.drawBitmap(face.faceBitmap, faceMatrix, p)
                    }
                }
            }

            // Floating CTA if no photo uploaded yet
            if (extractedFace == null && !isDetectingPhoto) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp)
                ) {
                    Surface(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(22.dp),
                        color = Color.Black.copy(alpha = 0.85f),
                        border = BorderStroke(1.5.dp, HairLookBlueAccent)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(HairLookBlueAccent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Put Your Face on Mannequin",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Tap to upload photo & see real clothes on you",
                                    color = HairLookBlueAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Subtle loader when AI segmentation is running
            if (isDetectingPhoto) {
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = Color.Black.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, HairLookBlueAccent.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = HairLookBlueAccent,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Extracting face & attaching to mannequin...",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // 2. CLEAN TOP BAR (Back button, Change Photo, and Save button - 0 clutter over face!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .border(1.dp, Color.White.copy(alpha = 0.20f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Actions: Change Photo & Save to Gallery
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Change Photo Button
                Surface(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = HairLookBlueAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (extractedFace == null) "Upload Photo" else "Change Photo",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Save Button (Clear, prominent, emerald green)
                Button(
                    onClick = performSaveOutfit,
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Save",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // 3. BOTTOM OUTFIT SELECTOR CAROUSEL & SAVE BUTTON (0 Clutter on photo)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.70f),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
                .navigationBarsPadding()
                .padding(vertical = 12.dp)
        ) {
            // Prominent Save Button above carousel when photo is uploaded
            if (extractedFace != null) {
                Button(
                    onClick = performSaveOutfit,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 10.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saving...", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save to Gallery",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Outfits Selector Horizontal List
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allOutfits) { outfit ->
                    val isSelected = activeOutfit.id == outfit.id
                    Card(
                        modifier = Modifier
                            .width(96.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) HairLookBlueAccent else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                activeOutfit = outfit
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF131D2E) else Color(0xFF0F172A)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(75.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF070B12))
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = outfit.drawableRes),
                                    contentDescription = outfit.name,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = outfit.name.take(14) + if (outfit.name.length > 14) "…" else "",
                                color = if (isSelected) HairLookBlueAccent else Color.White,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    // Success dialog after saving
    if (showSavedDialog) {
        AlertDialog(
            onDismissRequest = { showSavedDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Saved to Gallery!",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Your styled photo with the mannequin has been saved to your photo gallery.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    if (savedBitmapPreview != null) {
                        Image(
                            bitmap = savedBitmapPreview!!.asImageBitmap(),
                            contentDescription = "Saved Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSavedDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = HairLookBlueAccent)
                ) {
                    Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF1E293B),
            shape = RoundedCornerShape(20.dp)
        )
    }
}
