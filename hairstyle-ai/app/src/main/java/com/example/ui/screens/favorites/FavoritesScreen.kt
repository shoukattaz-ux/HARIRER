package com.example.ui.screens.favorites

import android.content.Intent
import android.graphics.Bitmap
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.repository.HairstyleRepository
import com.example.ui.components.HairstyleCard
import com.example.ui.theme.HairLookBlueAccent
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.HairLookDarkBg
import com.example.ui.theme.HairLookPinkAccent
import com.example.ui.theme.HairLookPurple
import com.example.ui.theme.HairLookPurpleGradientEnd
import com.example.ui.theme.HairLookPurpleGradientStart
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Screen 10 — Saved & Favorites Screen
 * Features tab switcher: [ Images ] vs [ Favorites ] matching the reference design.
 */
@Composable
fun FavoritesScreen(
    favoriteIds: Set<String>,
    onToggleFavorite: (String) -> Unit,
    onSelectHairstyle: (String) -> Unit,
    onQuickTryOn: (HairstyleItem) -> Unit,
    onExploreStyles: () -> Unit,
    savedPhotos: List<Bitmap> = emptyList(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeTab by remember { mutableStateOf("Images") } // "Images" or "Favorites"

    val savedFavorites = remember(favoriteIds) {
        HairstyleRepository.getAllHairstyles().filter { favoriteIds.contains(it.id) }
    }

    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(HairLookPurpleGradientStart, HairLookPurpleGradientEnd)
    )

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
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Saved",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Your personalized hairstyle portfolio",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Surface(
                color = HairLookCardBg,
                shape = CircleShape,
                border = BorderStroke(1.dp, HairLookCardBorder),
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = SunsetRose,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Tab Switcher: [ Images ] [ Favorites ] (Screen 10 in reference)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = HairLookCardBg,
                border = BorderStroke(1.dp, HairLookCardBorder)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    SavedTabPill(
                        label = "Images",
                        icon = Icons.Outlined.Image,
                        isSelected = activeTab == "Images",
                        onClick = { activeTab = "Images" }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    SavedTabPill(
                        label = "Favorites (${savedFavorites.size})",
                        icon = Icons.Outlined.FavoriteBorder,
                        isSelected = activeTab == "Favorites",
                        onClick = { activeTab = "Favorites" }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area based on Active Tab
        if (activeTab == "Images") {
            // IMAGES TAB: Saved try-on look photos or showcase looks
            val showcaseImages = listOf(
                Pair(R.drawable.img_short_1, "Short Crop Look"),
                Pair(R.drawable.img_medium_1, "Medium Taper Look"),
                Pair(R.drawable.overlay_w_bob, "French Bob Look"),
                Pair(R.drawable.img_women_hairstyles, "Butterfly Layers Look")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        text = "Captured Looks & Try-On Photos",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                    )
                }

                // If user took custom photos, show them first
                items(savedPhotos) { bitmap ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, HairLookPurpleGradientEnd, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Captured look",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                // Default showcase saved portfolio images
                items(showcaseImages) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.dp, HairLookCardBorder, RoundedCornerShape(18.dp)),
                        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Image(
                                painter = painterResource(id = item.first),
                                contentDescription = item.second,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = item.second,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Saved Look",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = HairLookPurpleGradientEnd,
                                    fontSize = 10.sp
                                )
                                IconButton(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, "Check out my new hairstyle look from HairLook: ${item.second}!")
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share Look"))
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // FAVORITES TAB: Hearted hairstyle items
            if (savedFavorites.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Favorites Yet",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the heart icon on any haircut to save it to your personal favorites.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onExploreStyles,
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HairLookPurpleGradientEnd)
                        ) {
                            Text(
                                text = "Explore Hairstyles",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(savedFavorites, key = { it.id }) { item ->
                        HairstyleCard(
                            item = item,
                            isFavorite = true,
                            onCardClick = { onSelectHairstyle(item.id) },
                            onFavoriteClick = { onToggleFavorite(item.id) },
                            onTryOnClick = { onQuickTryOn(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedTabPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) HairLookPurpleGradientEnd else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.Black else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isSelected) Color.Black else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 13.sp
            )
        }
    }
}
