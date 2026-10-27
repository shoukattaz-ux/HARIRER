package com.example.ui.screens.tryon

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.api.AiTryOnConfig
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.model.UserPhotoSource
import com.example.ui.components.UserPhotoDisplay
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

@Composable
fun TryOnResultScreen(
    hairstyle: HairstyleItem?,
    userPhoto: UserPhotoSource?,
    aiResultBitmap: Bitmap? = null,
    remainingFreeQuota: Int = 3,
    maxDailyQuota: Int = 3,
    selectedColorTint: String,
    onColorTintSelected: (String) -> Unit,
    onSaveResult: () -> Unit,
    onDeleteResult: () -> Unit,
    onTryAnotherStyle: () -> Unit,
    onChangeHairstyle: () -> Unit,
    onTryAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenStyleScore: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isShowingBefore by remember { mutableStateOf(false) }
    var isSplitView by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    val currentStyleName = hairstyle?.name ?: "Hairstyle"
    val isMen = hairstyle?.gender != Gender.WOMEN
    val themeAccent = if (isMen) ChampagneGold else ElectricViolet

    val colorTints = listOf(
        Pair("Natural Black", Color(0xFF1E1E1E)),
        Pair("Dark Brown", Color(0xFF4A3525)),
        Pair("Honey Blonde", Color(0xFFD4AF37)),
        Pair("Ash Grey", Color(0xFF8E9299)),
        Pair("Burgundy", Color(0xFF6B1D2F))
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
                .padding(bottom = 24.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("result_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                // Header with "AI Hairstyle Preview" Label
                Text(
                    text = "AI Hairstyle Preview",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                // Share Button in Top Bar
                IconButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "My HairStyle AI Preview")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out my virtual hairstyle preview: $currentStyleName on HairStyle AI!"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Hairstyle Preview"))
                    },
                    modifier = Modifier.testTag("result_share_top_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        tint = themeAccent
                    )
                }
            }

            // Quota Remaining Pill
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("result_quota_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Free previews remaining: $remainingFreeQuota/$maxDailyQuota",
                        color = themeAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Free Tier",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // View Mode Selector (Flip vs Side-by-Side Split)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .background(SurfaceDark, RoundedCornerShape(12.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isSplitView) themeAccent.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { isSplitView = false }
                        .padding(vertical = 8.dp)
                        .testTag("view_mode_flip_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = null,
                            tint = if (!isSplitView) themeAccent else TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Interactive Flip",
                            color = if (!isSplitView) themeAccent else TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSplitView) themeAccent.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { isSplitView = true }
                        .padding(vertical = 8.dp)
                        .testTag("view_mode_split_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ViewColumn,
                            contentDescription = null,
                            tint = if (isSplitView) themeAccent else TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Side-by-Side Split",
                            color = if (isSplitView) themeAccent else TextSecondary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Transformation Comparison Section: BEFORE | AFTER
            if (isSplitView) {
                // Side-by-Side Split View
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // BEFORE Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.82f)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, CardBorderStroke, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            UserPhotoDisplay(
                                photoSource = userPhoto,
                                contentDescription = "Before Photo",
                                modifier = Modifier.fillMaxSize()
                            )
                            Surface(
                                color = Color.Black.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "BEFORE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // AFTER Card
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(0.82f)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.5.dp, themeAccent, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (aiResultBitmap != null) {
                                Image(
                                    bitmap = aiResultBitmap.asImageBitmap(),
                                    contentDescription = "After Transformation",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.img_tryon_result),
                                    contentDescription = "After Transformation",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            Surface(
                                color = themeAccent,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "AFTER",
                                    color = ObsidianBackground,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Interactive Flip View (Full Card)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.5.dp, CardBorderStroke, RoundedCornerShape(22.dp))
                        .testTag("transformation_preview_card"),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.05f)
                        ) {
                            Crossfade(targetState = isShowingBefore, label = "before_after_fade") { showBefore ->
                                if (showBefore) {
                                    // Before: User photo
                                    UserPhotoDisplay(
                                        photoSource = userPhoto,
                                        contentDescription = "Before Photo",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    // After: Actual AI Result Bitmap
                                    if (aiResultBitmap != null) {
                                        Image(
                                            bitmap = aiResultBitmap.asImageBitmap(),
                                            contentDescription = "AI Generated Hairstyle",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().testTag("ai_result_image")
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.img_tryon_result),
                                            contentDescription = "After Transformation",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize().testTag("ai_result_placeholder")
                                        )
                                    }
                                }
                            }

                            // Gradient Scrim Bottom
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .height(74.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                            )

                            // Mode Indicator Pill (Before vs After)
                            Surface(
                                color = if (isShowingBefore) Color.Black.copy(alpha = 0.75f) else themeAccent,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (isShowingBefore) "ORIGINAL (BEFORE)" else "NEW STYLE (AFTER)",
                                    color = if (isShowingBefore) Color.White else ObsidianBackground,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }

                            // AI Hairstyle Preview Label
                            Surface(
                                color = Color.Black.copy(alpha = 0.75f),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderStroke),
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = themeAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AI Hairstyle Preview",
                                        color = TextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Style Name Pill on Bottom
                            Text(
                                text = if (isShowingBefore) "Original Photo" else "$currentStyleName • $selectedColorTint",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(14.dp)
                            )
                        }

                        // Before / After Toggle Buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .background(SurfaceDark, RoundedCornerShape(14.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (!isShowingBefore) themeAccent else Color.Transparent)
                                    .clickable { isShowingBefore = false }
                                    .padding(vertical = 10.dp)
                                    .testTag("toggle_after_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = if (!isShowingBefore) ObsidianBackground else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "After Makeover",
                                        color = if (!isShowingBefore) ObsidianBackground else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isShowingBefore) themeAccent else Color.Transparent)
                                    .clickable { isShowingBefore = true }
                                    .padding(vertical = 10.dp)
                                    .testTag("toggle_before_btn"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Compare,
                                        contentDescription = null,
                                        tint = if (isShowingBefore) ObsidianBackground else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Before",
                                        color = if (isShowingBefore) ObsidianBackground else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Realistic Hair Styling Disclaimer Notice
            Surface(
                color = SurfaceCard.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("hairstyle_disclaimer_notice")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp).padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AiTryOnConfig.HAIRSTYLE_DISCLAIMER,
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Hair Color Tone Selector
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = themeAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Color Tone",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = selectedColorTint,
                        color = themeAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    colorTints.forEach { (tintName, tintColor) ->
                        val isSelected = tintName == selectedColorTint
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.2f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(tintColor)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) themeAccent else CardBorderStroke,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onColorTintSelected(tintName) }
                                .testTag("tint_$tintName"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = if (tintName == "Honey Blonde" || tintName == "Ash Grey") Color.Black else Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action Row: [Save] & [Share] Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // [Save] Button
                Button(
                    onClick = {
                        if (aiResultBitmap != null) {
                            ImageSaver.saveBitmapToGallery(context, aiResultBitmap, currentStyleName)
                        }
                        isSaved = true
                        onSaveResult()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("save_result_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSaved) SuccessGreen else themeAccent,
                        contentColor = ObsidianBackground
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSaved) "Saved" else "Save",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // [Share] Button
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "My HairStyle AI Preview")
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out my new virtual look: $currentStyleName on HairStyle AI!"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Try-On Result"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("result_share_bottom_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CardBorderStroke),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        tint = themeAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Share",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // [Style Score] Button: score + lifestyle scenes for this look
            if (onOpenStyleScore != null && aiResultBitmap != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onOpenStyleScore,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp)
                        .testTag("result_style_score_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricViolet,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "See My Style Score",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Action Row: [Change Hairstyle] & [Delete Result / Discard]
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // [Change Hairstyle] Button
                Button(
                    onClick = onChangeHairstyle,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("result_change_style_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SurfaceCardElevated,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "Change Hairstyle",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // [Delete Result] Button (Privacy: allow deleting saved/active results)
                OutlinedButton(
                    onClick = {
                        onDeleteResult()
                        onBack()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("result_delete_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SunsetRose),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SunsetRose.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = null,
                        tint = SunsetRose,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Delete",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // [Try Another Style] Full-Width Button
            OutlinedButton(
                onClick = onTryAnotherStyle,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(48.dp)
                    .testTag("try_another_style_btn"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = themeAccent),
                border = androidx.compose.foundation.BorderStroke(1.dp, themeAccent),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "Try Another Style",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
