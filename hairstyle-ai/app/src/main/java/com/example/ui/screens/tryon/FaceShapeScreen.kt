package com.example.ui.screens.tryon

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.analysis.FaceShapeAnalyzer
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.repository.HairstyleRepository
import com.example.ui.theme.HairLookBlueAccent
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.HairLookDarkBg
import com.example.ui.theme.HairLookPinkAccent
import com.example.ui.theme.HairLookPurple
import com.example.ui.theme.HairLookPurpleGradientEnd
import com.example.ui.theme.HairLookPurpleGradientStart
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.io.InputStream

/**
 * Screens 11 & 12 — Professional Face Shape Analysis & AI Recommendations
 * Evaluates facial geometry & proportions (length, width, forehead, cheekbones, jawline, chin)
 * and estimates the user's face shape (Oval, Round, Square, Heart, Oblong, Diamond).
 *
 * Runs 100% on-device using ML Kit without uploading or storing user photos.
 */
@Composable
fun FaceShapeScreen(
    onSelectHairstyle: (String) -> Unit,
    onTryLive: (HairstyleItem) -> Unit,
    onBack: () -> Unit,
    onFaceShapeDetected: (String) -> Unit = {},
    initialFaceShape: String = "Oval",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val faceShapes = listOf("Oval", "Round", "Square", "Heart", "Oblong", "Diamond")
    var selectedFaceShape by remember { mutableStateOf(initialFaceShape) }
    var selectedGender by remember { mutableStateOf(Gender.MEN) }

    // Analysis states
    var analysisResult by remember { mutableStateOf<FaceShapeAnalyzer.FaceShapeAnalysisResult?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisMessage by remember { mutableStateOf<String?>(null) }

    // Reusable on-device analysis runner
    val runFaceAnalysis: (Bitmap) -> Unit = { bitmap ->
        isAnalyzing = true
        analysisMessage = "Analyzing facial landmarks and proportions on-device..."
        FaceShapeAnalyzer.analyzeFaceShape(bitmap) { result ->
            isAnalyzing = false
            if (result != null) {
                analysisResult = result
                selectedFaceShape = result.detectedShape
                analysisMessage = null
                onFaceShapeDetected(result.detectedShape)
            } else {
                analysisMessage = "Could not detect a clear front-facing face. Please try another photo with good lighting."
            }
        }
    }

    // Zero-permission modern Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bmp = context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
                if (bmp != null) {
                    runFaceAnalysis(bmp)
                } else {
                    analysisMessage = "Unable to decode the selected image. Please try a different photo."
                }
            } catch (e: Exception) {
                analysisMessage = "Error loading image. Please try another photo."
            }
        }
    }

    // Camera Snapshot Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bmp: Bitmap? ->
        if (bmp != null) {
            runFaceAnalysis(bmp)
        }
    }

    // Camera Permission Launcher with User Guidance
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                analysisMessage = "Camera is currently unavailable on this device. Please upload a photo from your gallery."
            }
        } else {
            analysisMessage = "Camera permission is required to take a live selfie. You can also upload a photo from your gallery."
        }
    }

    val allStyles = remember { HairstyleRepository.getAllHairstyles() }
    val recommendedStyles = remember(selectedFaceShape, selectedGender) {
        allStyles.filter { it.gender == selectedGender && it.faceShapes.contains(selectedFaceShape) }
            .ifEmpty { allStyles.filter { it.gender == selectedGender }.take(4) }
    }

    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(HairLookPurpleGradientStart, HairLookPurpleGradientEnd)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HairLookDarkBg)
    ) {
        // Top Bar
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
                    .testTag("face_shape_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Face Shape with AI Recommend",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Curated styles for $selectedFaceShape face",
                    style = MaterialTheme.typography.bodySmall,
                    color = HairLookPurpleGradientEnd
                )
            }
        }

        // Scrollable Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // 1. SMART FACE SHAPE SCANNER CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, HairLookPurpleGradientEnd.copy(alpha = 0.5f), RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(HairLookPurpleGradientEnd.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Scan",
                                    tint = HairLookPurpleGradientEnd,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Scan Face Shape",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "On-device landmark geometry estimate",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted
                                )
                            }
                        }

                        if (analysisResult != null) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HairLookPurpleGradientEnd.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, HairLookPurpleGradientEnd.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "${analysisResult?.confidencePercent}% Match",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HairLookPurpleGradientEnd,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isAnalyzing) {
                        // Analysis In-Progress State
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = HairLookPurpleGradientEnd,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = analysisMessage ?: "Scanning facial landmarks...",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else if (analysisResult != null) {
                        // Detailed Analysis Result Card
                        val res = analysisResult!!
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(HairLookDarkBg)
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Detected",
                                    tint = HairLookPurpleGradientEnd,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Detected Face Shape: ${res.detectedShape}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = res.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Proportions breakdown chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HairLookCardBg,
                                    border = BorderStroke(1.dp, HairLookCardBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Ratio", fontSize = 10.sp, color = TextMuted)
                                        Text("${res.proportions.lengthToWidthRatio}x", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HairLookCardBg,
                                    border = BorderStroke(1.dp, HairLookCardBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Jawline", fontSize = 10.sp, color = TextMuted)
                                        Text(res.proportions.jawlineDescription, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = HairLookCardBg,
                                    border = BorderStroke(1.dp, HairLookCardBorder),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text("Chin", fontSize = 10.sp, color = TextMuted)
                                        Text(res.proportions.chinShapeDescription, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Aesthetic Estimate Disclaimer
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Disclaimer",
                                    tint = TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = res.disclaimer,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Re-scan Button
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, HairLookCardBorder)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Scan Another Photo",
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Scan Another Photo", color = TextPrimary, fontSize = 12.sp)
                        }
                    } else {
                        // Scan Actions: Camera or Photo Upload
                        if (analysisMessage != null) {
                            Text(
                                text = analysisMessage!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = HairLookPinkAccent,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (!isAnalyzing) {
                                        val hasPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                        if (hasPerm) {
                                            try {
                                                cameraLauncher.launch(null)
                                            } catch (e: Exception) {
                                                analysisMessage = "Camera is currently unavailable on this device. Please upload a photo from your gallery."
                                            }
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    }
                                },
                                enabled = !isAnalyzing,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HairLookPurpleGradientEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Camera",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Camera", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }

                            Button(
                                onClick = {
                                    if (!isAnalyzing) {
                                        try {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        } catch (e: Exception) {
                                            analysisMessage = "Unable to open photo picker. Please try again."
                                        }
                                    }
                                },
                                enabled = !isAnalyzing,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HairLookCardBorder)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Upload Photo",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Upload Photo", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Face Shape Overview Card with Shape Selector Pills
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.5.dp, HairLookPurpleGradientEnd.copy(alpha = 0.6f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Face Shape Graphic Outline
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        HairLookPurple.copy(alpha = 0.35f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(2.dp, HairLookPurpleGradientEnd, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Face Shape",
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = selectedFaceShape,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = when (selectedFaceShape) {
                            "Oval" -> "Naturally balanced proportions with gently rounded jawline. Suits almost any cut, especially quiffs, pompadours, and textured crops."
                            "Square" -> "Strong, sharp jawline with equal width at forehead and cheeks. Soft textures, fades, and layered volume soften prominent angles."
                            "Round" -> "Equal width and length with soft curves. Cuts with vertical height, volume on top, and short tapered sides elongate the face."
                            "Heart" -> "Wider forehead tapering into a pointed chin. Fringe, textured bangs, and mid-length cuts balance facial width."
                            "Diamond" -> "Widest at prominent cheekbones with narrow forehead and tapered chin. Cuts with side width, textured fringe, and jawline layers create harmonic balance."
                            else -> "Longer face with straight cheek lines. Cuts with width, bangs, and side volume create harmonic balance."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selectable Face Shape Pills (All 6 supported shapes)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        faceShapes.forEach { shape ->
                            val isSelected = selectedFaceShape == shape
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) HairLookPurpleGradientEnd else HairLookDarkBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) HairLookPurpleGradientEnd else HairLookCardBorder
                                ),
                                modifier = Modifier.clickable {
                                    selectedFaceShape = shape
                                    onFaceShapeDetected(shape)
                                }
                            ) {
                                Text(
                                    text = shape,
                                    color = if (isSelected) Color.Black else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Gender Filter Row: [ Men ] [ Women ]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recommended for you",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedGender == Gender.MEN) HairLookBlueAccent else HairLookCardBg,
                        modifier = Modifier.clickable { selectedGender = Gender.MEN }
                    ) {
                        Text(
                            text = "Men",
                            color = if (selectedGender == Gender.MEN) Color.Black else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedGender == Gender.WOMEN) HairLookPinkAccent else HairLookCardBg,
                        modifier = Modifier.clickable { selectedGender = Gender.WOMEN }
                    ) {
                        Text(
                            text = "Women",
                            color = if (selectedGender == Gender.WOMEN) Color.Black else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Horizontal Recommended Styles
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(recommendedStyles, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier
                            .width(135.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, HairLookCardBorder, RoundedCornerShape(18.dp))
                            .clickable { onSelectHairstyle(item.id) },
                        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Image(
                                painter = painterResource(id = item.imageRes),
                                contentDescription = item.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = item.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = HairLookPurpleGradientEnd,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onTryLive(item) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HairLookPurpleGradientEnd),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Try",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Best Hairstyles Bullet Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, HairLookCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Best Hairstyles for $selectedFaceShape",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val bullets = when (selectedFaceShape) {
                        "Oval" -> listOf("Textured French Crop", "Natural Quiff", "Classic Side Part", "Low/Mid Taper Fade", "French Bob")
                        "Square" -> listOf("Soft Side-Swept Quiff", "Undercut with Volume", "Tousled Curls", "Textured Layer Cut")
                        "Round" -> listOf("High Fade Pompadour", "Vertical Quiff", "Spiky Crop", "Long Layers with Volume")
                        "Heart" -> listOf("Mid Fade with Fringe", "Curtain Bangs", "Textured Shag", "Tousled Beach Waves")
                        "Diamond" -> listOf("Textured Crop with Fringe", "Side-Swept Bangs", "Layered Chin-Length Bob", "Medium Textured Quiff", "Soft Wispy Waves")
                        else -> listOf("Textured Fringe", "Classic Side Part", "Chin-Length Bob", "Waves with Side Part")
                    }

                    bullets.forEach { b ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(HairLookPurpleGradientEnd)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = b,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom CTA: Try All in Live AR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .height(54.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(purpleGradient)
        ) {
            Button(
                onClick = {
                    recommendedStyles.firstOrNull()?.let { onTryLive(it) }
                },
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Try Live",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Try Recommended Styles in Live AR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
