package com.example.ui.screens.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Texture
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.example.data.repository.HairstyleAngleResolver
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HairstyleItem
import com.example.data.model.hairTexture
import com.example.data.repository.HairstyleRepository
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.HairLookBlueAccent
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.HairLookDarkBg
import com.example.ui.theme.HairLookPurpleGradientEnd
import com.example.ui.theme.HairLookPurpleGradientStart
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Screen 13 — Professional Barber Guide & Salon Specifications
 * Designed specifically for users to show their barber or hairstylist in the salon chair.
 * Contains:
 * - Hairstyle name & large preview image
 * - Hair length and texture
 * - Suitable face shapes
 * - Available haircut details (top length, sides, neckline, styling specs)
 * - Optional personal haircut notes
 * - Robust Share button with sensible fallback (Toast & Clipboard)
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BarberGuideScreen(
    hairstyleId: String? = null,
    onTryLive: (HairstyleItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val item = remember {
        hairstyleId?.let { HairstyleRepository.getById(it) }
            ?: HairstyleRepository.getAllHairstyles().first()
    }

    var selectedAngle by remember { mutableStateOf("Front") } // Front, Side, Back
    var customNotes by remember { mutableStateOf("") }
    var isEditingNotes by remember { mutableStateOf(false) }

    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(HairLookPurpleGradientStart, HairLookPurpleGradientEnd)
    )

    // Sharing action with safe fallback
    val shareBarberGuide: () -> Unit = {
        val notesSnippet = if (customNotes.isNotBlank()) "\n• Client Notes: $customNotes" else ""
        val shareMsg = "Barber Specs for ${item.name} (${HairstyleAngleResolver.getAngleLabel(selectedAngle, item.gender)}):\n" +
                "• Category: ${item.category} • ${item.gender.displayName}\n" +
                "• Hair Length: ${item.hairLength}\n" +
                "• Hair Texture: ${item.hairTexture}\n" +
                "• Active Angle Focus: ${HairstyleAngleResolver.getAngleBarberAdvice(item, selectedAngle)}\n" +
                "• Suitable Face Shapes: ${item.faceShapes.joinToString(", ")}\n" +
                "• Top: Point-cut for natural separation and movement\n" +
                "• Sides: ${if (item.category.contains("Fade", ignoreCase = true)) "Clean taper fade mid-temple" else "Clean tapered edges"}\n" +
                "• Neckline: Soft natural tapered blend\n" +
                "• Styling: ${item.stylingTips}" +
                notesSnippet

        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Barber Specs: ${item.name}")
                putExtra(Intent.EXTRA_TEXT, shareMsg)
            }
            context.startActivity(Intent.createChooser(intent, "Share with Barber"))
        } catch (e: Exception) {
            // Sensible fallback: Copy specs to clipboard and notify user
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val clip = ClipData.newPlainText("Barber Specs", shareMsg)
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(context, "Barber specifications copied to clipboard!", Toast.LENGTH_LONG).show()
            } catch (err: Exception) {
                Toast.makeText(context, "Ready to show to your barber", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HairLookDarkBg)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(HairLookCardBg)
                        .border(1.dp, HairLookCardBorder, CircleShape)
                        .testTag("barber_guide_back_btn")
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
                        text = "Show to Barber",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Show this screen to your stylist",
                        style = MaterialTheme.typography.bodySmall,
                        color = HairLookPurpleGradientEnd
                    )
                }
            }

            // Share Button in Header
            IconButton(
                onClick = shareBarberGuide,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(HairLookCardBg)
                    .border(1.dp, HairLookCardBorder, CircleShape)
                    .testTag("barber_guide_header_share_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share Specs",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Scrollable Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // 1. Main Hairstyle Showcase Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, HairLookCardBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val activeImageRes = HairstyleAngleResolver.getAngleImage(item, selectedAngle)
                    val backPillLabel = HairstyleAngleResolver.getAngleLabel("Back", item.gender)

                    val isCustomSideOrBack = when (selectedAngle) {
                        "Side" -> item.sideImageRes != null
                        "Back" -> item.backImageRes != null
                        else -> true
                    }

                    // Hairstyle Image Preview with 360° Angle Crossfade
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(255.dp)
                            .clip(RoundedCornerShape(18.dp))
                    ) {
                        Crossfade(
                            targetState = activeImageRes,
                            label = "HairstyleAngleCrossfade",
                            modifier = Modifier.fillMaxSize()
                        ) { targetRes ->
                            Image(
                                painter = painterResource(id = targetRes),
                                contentDescription = "${item.name} - $selectedAngle",
                                contentScale = ContentScale.Crop,
                                alignment = when {
                                    isCustomSideOrBack -> Alignment.Center
                                    selectedAngle == "Side" -> Alignment.CenterStart
                                    selectedAngle == "Back" -> Alignment.BottomCenter
                                    else -> Alignment.Center
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        if (!isCustomSideOrBack) {
                                            when (selectedAngle) {
                                                "Side" -> {
                                                    scaleX = 1.15f
                                                    scaleY = 1.15f
                                                    translationX = 20f
                                                }
                                                "Back" -> {
                                                    scaleX = 1.12f
                                                    scaleY = 1.12f
                                                    translationY = -15f
                                                }
                                                else -> {
                                                    scaleX = 1f
                                                    scaleY = 1f
                                                    translationX = 0f
                                                    translationY = 0f
                                                }
                                            }
                                        }
                                    }
                            )
                        }

                        // Bottom Angle Focus Guide HUD Banner on the Image
                        if (selectedAngle != "Front") {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(),
                                color = HairLookDarkBg.copy(alpha = 0.88f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCut,
                                        contentDescription = null,
                                        tint = ChampagneGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (selectedAngle == "Side")
                                            "📐 Side Profile: Temple taper & clean ear arch line"
                                        else
                                            "📐 ${backPillLabel}: Soft natural neckline blend (no box line)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ChampagneGold
                                    )
                                }
                            }
                        }

                        // Floating Active Angle Badge Indicator
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = HairLookDarkBg.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, HairLookPurpleGradientEnd.copy(alpha = 0.6f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = when (selectedAngle) {
                                        "Side" -> Icons.Default.RotateRight
                                        "Back" -> Icons.Default.RotateLeft
                                        else -> Icons.Default.Face
                                    },
                                    contentDescription = null,
                                    tint = HairLookPurpleGradientEnd,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = HairstyleAngleResolver.getAngleLabel(selectedAngle, item.gender),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Category: ${item.category} • ${item.gender.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = HairLookPurpleGradientEnd,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Angle Selector Pills: [ Front View ] [ Side Profile ] [ Back Taper / Back View ]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = HairLookDarkBg,
                            border = BorderStroke(1.dp, HairLookCardBorder)
                        ) {
                            Row(modifier = Modifier.padding(4.dp)) {
                                AnglePill(
                                    label = "Front View",
                                    isSelected = selectedAngle == "Front",
                                    onClick = { selectedAngle = "Front" }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                AnglePill(
                                    label = "Side Profile",
                                    isSelected = selectedAngle == "Side",
                                    onClick = { selectedAngle = "Side" }
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                AnglePill(
                                    label = backPillLabel,
                                    isSelected = selectedAngle == "Back",
                                    onClick = { selectedAngle = "Back" }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Hair Specs Summary Card (Length, Texture, Face Shapes)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, HairLookCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Haircut Overview",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = HairLookDarkBg,
                            border = BorderStroke(1.dp, HairLookCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCut,
                                        contentDescription = null,
                                        tint = HairLookPurpleGradientEnd,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Length", fontSize = 11.sp, color = TextMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.hairLength, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = HairLookDarkBg,
                            border = BorderStroke(1.dp, HairLookCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Texture,
                                        contentDescription = null,
                                        tint = HairLookPurpleGradientEnd,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Texture", fontSize = 11.sp, color = TextMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.hairTexture, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = HairLookDarkBg,
                            border = BorderStroke(1.dp, HairLookCardBorder)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = HairLookPurpleGradientEnd,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Upkeep", fontSize = 11.sp, color = TextMuted)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.maintenance, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Suitable Face Shapes:",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item.faceShapes.forEach { shape ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = HairLookDarkBg,
                                border = BorderStroke(1.dp, HairLookCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = null,
                                        tint = HairLookBlueAccent,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = shape,
                                        fontSize = 11.sp,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Technical Barber Instructions Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, HairLookCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = null,
                            tint = HairLookPurpleGradientEnd,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "What to Tell Your Barber",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dynamic Active Angle Guidance Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = HairLookDarkBg,
                        border = BorderStroke(1.dp, HairLookPurpleGradientEnd.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (selectedAngle) {
                                        "Side" -> Icons.Default.RotateRight
                                        "Back" -> Icons.Default.RotateLeft
                                        else -> Icons.Default.Visibility
                                    },
                                    contentDescription = null,
                                    tint = HairLookPurpleGradientEnd,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = HairstyleAngleResolver.getAngleSpecTitle(selectedAngle, item.gender),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = HairstyleAngleResolver.getAngleBarberAdvice(item, selectedAngle),
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    BarberSpecRow(
                        title = "Top Length & Texture Technique",
                        detail = "Keep length on top proportional to length category (${item.hairLength}). Request point-cutting or feather razor texturing for natural separation and movement."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BarberSpecRow(
                        title = "Sides & Temples",
                        detail = if (item.category.contains("Fade", ignoreCase = true))
                            "Graduated taper fade starting mid-temple with smooth transition to the occipital bone."
                        else
                            "Clean scissor-over-comb taper with soft natural sideburn blend."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BarberSpecRow(
                        title = "Neckline & Edges",
                        detail = "Tapered neckline (avoid blocked edges) to ensure the cut grows out naturally and gracefully."
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    BarberSpecRow(
                        title = "Styling & Finish",
                        detail = item.stylingTips
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Optional Personal Haircut Notes Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, HairLookCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Personal Haircut Notes",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Text(
                            text = if (isEditingNotes) "Done" else "Edit",
                            color = HairLookPurpleGradientEnd,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { isEditingNotes = !isEditingNotes }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isEditingNotes) {
                        OutlinedTextField(
                            value = customNotes,
                            onValueChange = { customNotes = it },
                            placeholder = { Text("e.g., Keep cowlick in front longer, leave sideburns natural...", color = TextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HairLookPurpleGradientEnd,
                                unfocusedBorderColor = HairLookCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Text(
                            text = if (customNotes.isNotBlank()) customNotes else "No personal notes added yet. Tap Edit to add your custom instructions (e.g. guard numbers, cowlick notes).",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (customNotes.isNotBlank()) TextPrimary else TextMuted,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom CTA Buttons: "Share with Barber" + "Try This Style"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(purpleGradient)
            ) {
                Button(
                    onClick = shareBarberGuide,
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share with Barber",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Share with Barber",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = { onTryLive(item) },
                modifier = Modifier
                    .weight(0.9f)
                    .height(52.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Try This Style",
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Try Style",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AnglePill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isSelected) HairLookPurpleGradientEnd else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun BarberSpecRow(title: String, detail: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = HairLookBlueAccent
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = detail,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            lineHeight = 18.sp
        )
    }
}
