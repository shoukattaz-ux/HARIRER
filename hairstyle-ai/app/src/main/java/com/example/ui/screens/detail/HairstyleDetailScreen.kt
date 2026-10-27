package com.example.ui.screens.detail

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Texture
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.model.hairTexture
import com.example.data.model.stylingDifficulty
import com.example.data.model.toVariationItem
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.GoldStar
import com.example.ui.theme.HairLookBlueAccent
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.HairLookDarkBg
import com.example.ui.theme.HairLookPurpleGradientEnd
import com.example.ui.theme.HairLookPurpleGradientStart
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Professional Hairstyle Details Screen
 * Displays large preview, name, category, length, texture, suitable face shapes,
 * maintenance, styling difficulty, description, and direct actions ("Try This Style",
 * "Add to Favorites", "Show to Barber").
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HairstyleDetailScreen(
    item: HairstyleItem,
    isFavorite: Boolean,
    isPaidSubscribed: Boolean = false,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onTryThisStyle: (HairstyleItem) -> Unit,
    onShowToBarber: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accentColor = if (item.gender == Gender.MEN) ChampagneGold else HairLookPurpleGradientEnd

    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(HairLookPurpleGradientStart, HairLookPurpleGradientEnd)
    )

    var selectedVariationIndex by remember(item.id) { mutableIntStateOf(0) }

    val activeVariation = if (item.variations.isNotEmpty() && selectedVariationIndex in item.variations.indices) {
        item.variations[selectedVariationIndex]
    } else null

    val currentImageRes = activeVariation?.imageRes ?: item.imageRes
    val currentName = activeVariation?.name ?: item.name
    val currentDescription = activeVariation?.description ?: item.description
    val currentStylingTips = activeVariation?.stylingTips ?: item.stylingTips
    val currentFaceShapes = activeVariation?.faceShapes ?: item.faceShapes
    val currentLength = activeVariation?.hairLength ?: item.hairLength
    val currentTexture = activeVariation?.hairTexture ?: item.hairTexture
    val currentMaintenance = activeVariation?.maintenance ?: item.maintenance
    val currentDifficulty = activeVariation?.stylingDifficulty ?: item.stylingDifficulty

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HairLookDarkBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // space for fixed bottom action buttons
        ) {
            // Large Hairstyle Preview Box (Framed from top of hair crown down)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.85f)
            ) {
                Image(
                    painter = painterResource(id = currentImageRes),
                    contentDescription = currentName,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier.fillMaxSize()
                )

                // Top gradient scrim (softened to preserve hair crown clarity)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.40f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Bottom gradient scrim
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(120.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    HairLookDarkBg.copy(alpha = 0.95f),
                                    HairLookDarkBg
                                )
                            )
                        )
                )

                // Top Bar Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                            .border(1.dp, HairLookCardBorder, CircleShape)
                            .testTag("detail_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Share Button with Safe Chooser Fallback
                        IconButton(
                            onClick = {
                                try {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(
                                            Intent.EXTRA_SUBJECT,
                                            "Check out this hairstyle: ${item.name}"
                                        )
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "I found this great ${item.gender.displayName} haircut on HairLook: ${item.name} (${item.category})! Try it on virtually."
                                        )
                                    }
                                    context.startActivity(
                                        Intent.createChooser(shareIntent, "Share Hairstyle")
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "${item.name} specs ready to share", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                                .border(1.dp, HairLookCardBorder, CircleShape)
                                .testTag("detail_share_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Favorite Button
                        IconButton(
                            onClick = onToggleFavorite,
                            modifier = Modifier
                                .size(42.dp)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                                .border(1.dp, HairLookCardBorder, CircleShape)
                                .testTag("detail_favorite_btn")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from Favorites" else "Add to Favorites",
                                tint = if (isFavorite) SunsetRose else Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Category & Gender Badge
                Surface(
                    color = accentColor,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 10.dp)
                ) {
                    Text(
                        text = "${item.gender.displayName.uppercase()} • ${item.category.uppercase()}",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Hairstyle Header Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = currentName,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = GoldStar,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${item.rating} (500+ reviews)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = if (item.variations.isNotEmpty())
                            "Variation ${selectedVariationIndex + 1} of ${item.variations.size}"
                        else
                            "${item.tryOnCount} virtual try-ons",
                        style = MaterialTheme.typography.labelMedium,
                        color = HairLookPurpleGradientEnd,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Dedicated Style Variations Selector
                if (item.variations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(HairLookCardBg.copy(alpha = 0.6f))
                            .border(1.dp, HairLookCardBorder, RoundedCornerShape(18.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Style Variations",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Surface(
                                color = ChampagneGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "${selectedVariationIndex + 1} / ${item.variations.size} Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 2.dp)
                        ) {
                            itemsIndexed(item.variations) { index, variation ->
                                val isSelected = index == selectedVariationIndex
                                Card(
                                    modifier = Modifier
                                        .width(110.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) ChampagneGold else HairLookCardBorder,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable { selectedVariationIndex = index },
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) HairLookCardBorder.copy(alpha = 0.3f) else HairLookCardBg
                                    )
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(115.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(id = variation.imageRes),
                                                contentDescription = variation.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            if (isSelected) {
                                                Surface(
                                                    color = ChampagneGold,
                                                    shape = CircleShape,
                                                    modifier = Modifier
                                                        .align(Alignment.TopEnd)
                                                        .padding(6.dp)
                                                        .size(20.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = variation.name,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSelected) ChampagneGold else TextPrimary,
                                                maxLines = 1,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = variation.fadeLevel,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextMuted,
                                                maxLines = 1,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Key Hair Attributes Grid: Length, Texture, Maintenance, Difficulty
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AttributeCard(
                        icon = Icons.Default.ContentCut,
                        title = "Length",
                        value = currentLength,
                        modifier = Modifier.weight(1f)
                    )
                    AttributeCard(
                        icon = Icons.Default.Texture,
                        title = "Texture",
                        value = currentTexture,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AttributeCard(
                        icon = Icons.Default.Speed,
                        title = "Maintenance",
                        value = currentMaintenance,
                        modifier = Modifier.weight(1f)
                    )
                    AttributeCard(
                        icon = Icons.Default.AutoAwesome,
                        title = "Difficulty",
                        value = currentDifficulty,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description
                Text(
                    text = "About This Style",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = currentDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Recommended Face Shapes
                Text(
                    text = "Suitable Face Shapes",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentFaceShapes.forEach { shape ->
                        Surface(
                            color = HairLookCardBg,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, HairLookCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Face,
                                    contentDescription = null,
                                    tint = HairLookPurpleGradientEnd,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$shape Face",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Barber / Stylist Tips Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, HairLookCardBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = accentColor.copy(alpha = 0.2f),
                                shape = CircleShape,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCut,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Stylist Pro Tips",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentStylingTips,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tags
                if (item.tags.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item.tags.forEach { tag ->
                            Text(
                                text = "#$tag",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Floating Bottom Sticky Bar with Two Major Action Buttons:
        // [ Show to Barber ] and [ Try This Style ]
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = HairLookDarkBg.copy(alpha = 0.96f),
            tonalElevation = 8.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = HairLookCardBorder,
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Show to Barber" Button
                    OutlinedButton(
                        onClick = {
                            val idToShow = activeVariation?.let { "${item.id}_${it.id}" } ?: item.id
                            onShowToBarber(idToShow)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .testTag("show_to_barber_btn"),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, HairLookPurpleGradientEnd),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = HairLookCardBg,
                            contentColor = TextPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCut,
                            contentDescription = "Show to Barber",
                            tint = HairLookPurpleGradientEnd,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Show to Barber",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    // "Try This Style" Button (uses selected variation)
                    Button(
                        onClick = {
                            val itemToTry = activeVariation?.let { item.toVariationItem(it) } ?: item
                            onTryThisStyle(itemToTry)
                        },
                        modifier = Modifier
                            .weight(1.15f)
                            .height(52.dp)
                            .testTag("try_this_style_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ChampagneGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Try This Style",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Try This Style",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttributeCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, HairLookCardBorder, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HairLookPurpleGradientEnd,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}
