package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.GoldStar
import com.example.ui.theme.HairLookBlueAccent
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.HairLookDarkBg
import com.example.ui.theme.HairLookPinkAccent
import com.example.ui.theme.HairLookPurple
import com.example.ui.theme.HairLookPurpleGradientEnd
import com.example.ui.theme.HairLookPurpleGradientStart
import com.example.ui.theme.LocalThemeIsDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * 6 Supported Languages as specified in the reference image
 */
enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val flagEmoji: String
) {
    ENGLISH("en", "English", "UK English", "🇬🇧"),
    URDU("ur", "Urdu", "Urdu", "🇵🇰"),
    HINDI("hi", "हिन्दी", "Hindi", "🇮🇳"),
    SPANISH("es", "Español", "Spanish", "🇪🇸"),
    ARABIC("ar", "Arabic", "Arabic", "🇸🇦"),
    FRENCH("fr", "Français", "French", "🇫🇷");

    val flag: String get() = flagEmoji
    val displayName: String get() = englishName
}

/**
 * 10 Key Features metadata matching the reference image specification
 */
data class KeyFeatureItem(
    val id: String,
    val titleEn: String,
    val titleUrdu: String = titleEn,
    val descriptionEn: String,
    val descriptionUrdu: String = descriptionEn,
    val badgeText: String,
    val icon: ImageVector,
    val accentColor: Color,
    val gradientStart: Color,
    val gradientEnd: Color
)

val AllKeyFeatures: List<KeyFeatureItem> = listOf(
    KeyFeatureItem(
        id = "men_women_category",
        titleEn = "Men & Women Category",
        titleUrdu = "Men & Women Category",
        descriptionEn = "40+ curated haircuts separated by gender and hair length.",
        descriptionUrdu = "40+ curated haircuts separated by gender and hair length.",
        badgeText = "40+ Styles",
        icon = Icons.Default.Face,
        accentColor = HairLookPurpleGradientEnd,
        gradientStart = HairLookPurpleGradientStart,
        gradientEnd = HairLookPurpleGradientEnd
    ),
    KeyFeatureItem(
        id = "live_camera_tryon",
        titleEn = "Live Camera Try-On",
        titleUrdu = "Live Camera Try-On",
        descriptionEn = "Real-time front camera AR tracking with hairline alignment.",
        descriptionUrdu = "Real-time front camera AR tracking with hairline alignment.",
        badgeText = "Live AR",
        icon = Icons.Default.Videocam,
        accentColor = HairLookBlueAccent,
        gradientStart = Color(0xFF0284C7),
        gradientEnd = HairLookBlueAccent
    ),
    KeyFeatureItem(
        id = "upload_photo_tryon",
        titleEn = "Upload Photo Try-On",
        titleUrdu = "Upload Photo Try-On",
        descriptionEn = "Choose photo from gallery or take portrait for try-on.",
        descriptionUrdu = "Choose photo from gallery or take portrait for try-on.",
        badgeText = "Photo Picker",
        icon = Icons.Default.AddAPhoto,
        accentColor = HairLookPinkAccent,
        gradientStart = Color(0xFFDB2777),
        gradientEnd = HairLookPinkAccent
    ),
    KeyFeatureItem(
        id = "face_shape_analysis",
        titleEn = "AI Face Scan",
        titleUrdu = "AI Face Scan",
        descriptionEn = "Scan your face shape & skin tone for personalized cuts and colors.",
        descriptionUrdu = "Scan your face shape & skin tone for personalized cuts and colors.",
        badgeText = "Smart AI",
        icon = Icons.Default.AutoAwesome,
        accentColor = GoldStar,
        gradientStart = Color(0xFFD97706),
        gradientEnd = GoldStar
    ),
    KeyFeatureItem(
        id = "color_tinting",
        titleEn = "Real-Time Hair Color",
        titleUrdu = "Real-Time Hair Color",
        descriptionEn = "Preview Black, Brown, Blonde, Platinum, and Auburn shades.",
        descriptionUrdu = "Preview Black, Brown, Blonde, Platinum, and Auburn shades.",
        badgeText = "8+ Colors",
        icon = Icons.Default.Palette,
        accentColor = ElectricViolet,
        gradientStart = Color(0xFF7C3AED),
        gradientEnd = ElectricViolet
    ),
    KeyFeatureItem(
        id = "compare_styles",
        titleEn = "Compare Two Styles",
        titleUrdu = "Compare Two Styles",
        descriptionEn = "Side-by-side split screen to contrast two haircuts.",
        descriptionUrdu = "Side-by-side split screen to contrast two haircuts.",
        badgeText = "Split View",
        icon = Icons.Default.Compare,
        accentColor = NeonCyan,
        gradientStart = Color(0xFF0D9488),
        gradientEnd = NeonCyan
    ),
    KeyFeatureItem(
        id = "barber_guide",
        titleEn = "360° Barber Guide",
        titleUrdu = "360° Barber Guide",
        descriptionEn = "Exact clipper guard numbers, taper fade and scissor specs.",
        descriptionUrdu = "Exact clipper guard numbers, taper fade and scissor specs.",
        badgeText = "Pro Specs",
        icon = Icons.Default.ContentCut,
        accentColor = SunsetRose,
        gradientStart = Color(0xFFE11D48),
        gradientEnd = SunsetRose
    ),
    KeyFeatureItem(
        id = "favorites_history",
        titleEn = "Favorites & History",
        titleUrdu = "Favorites & History",
        descriptionEn = "Bookmark top looks and quickly access past try-on results.",
        descriptionUrdu = "Bookmark top looks and quickly access past try-on results.",
        badgeText = "Saved Looks",
        icon = Icons.Default.Favorite,
        accentColor = Color(0xFFEF4444),
        gradientStart = Color(0xFFDC2626),
        gradientEnd = Color(0xFFF87171)
    ),
    KeyFeatureItem(
        id = "multi_language",
        titleEn = "Multi-Language",
        titleUrdu = "Multi-Language",
        descriptionEn = "English, Urdu, Hindi, Spanish, Arabic, and French.",
        descriptionUrdu = "English, Urdu, Hindi, Spanish, Arabic, and French.",
        badgeText = "6 Languages",
        icon = Icons.Default.Language,
        accentColor = HairLookPurpleGradientEnd,
        gradientStart = HairLookPurpleGradientStart,
        gradientEnd = HairLookPurpleGradientEnd
    ),
    KeyFeatureItem(
        id = "monetization_tier",
        titleEn = "Monetization & VIP",
        titleUrdu = "Monetization & VIP",
        descriptionEn = "Free tier (3 daily AI try-ons) vs $2.00/mo VIP Unlimited.",
        descriptionUrdu = "Free tier (3 daily AI try-ons) vs $2.00/mo VIP Unlimited.",
        badgeText = "VIP $2.00",
        icon = Icons.Default.WorkspacePremium,
        accentColor = GoldStar,
        gradientStart = Color(0xFFB45309),
        gradientEnd = GoldStar
    )
)

/**
 * The Key Features Grid Section prominently placed on HomeScreen
 */
@Composable
fun KeyFeaturesSection(
    currentLanguage: AppLanguage,
    onFeatureClick: (KeyFeatureItem) -> Unit,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = currentLanguage == AppLanguage.URDU

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Section Header with Glowing Badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = HairLookPurpleGradientEnd,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Key Features",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "All 10 capabilities from the design specification",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Surface(
                color = HairLookPurple.copy(alpha = 0.18f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, HairLookPurpleGradientEnd.copy(alpha = 0.4f)),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onViewAllClick() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = HairLookPurpleGradientEnd,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelSmall,
                        color = HairLookPurpleGradientEnd,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2-Column Grid for all 10 Key Features
        val chunkedFeatures = AllKeyFeatures.chunked(2)
        chunkedFeatures.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { item ->
                    KeyFeatureCard(
                        item = item,
                        isUrdu = isUrdu,
                        onClick = { onFeatureClick(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun KeyFeatureCard(
    item: KeyFeatureItem,
    isUrdu: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("key_feature_${item.id}")
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
        border = BorderStroke(1.dp, item.accentColor.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Top Row: Icon with Gradient Background + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    item.gradientStart.copy(alpha = 0.85f),
                                    item.gradientEnd.copy(alpha = 0.95f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.titleEn,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Surface(
                    color = item.accentColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.8.dp, item.accentColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = item.badgeText,
                        color = item.accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = if (isUrdu) item.titleUrdu else item.titleEn,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle Description
            Text(
                text = if (isUrdu) item.descriptionUrdu else item.descriptionEn,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Supported Languages Card with interactive chips for all 6 languages
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SupportedLanguagesCard(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrdu = currentLanguage == AppLanguage.URDU

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
        border = BorderStroke(1.dp, HairLookCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = HairLookPurpleGradientEnd,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Supported Languages",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = HairLookPurple.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "6 Languages",
                        color = HairLookPurpleGradientEnd,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Switch language instantly to update UI headings and descriptions",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Chips row for 6 languages
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppLanguage.values().forEach { lang ->
                    val isSelected = lang == currentLanguage
                    Surface(
                        color = if (isSelected) HairLookPurple.copy(alpha = 0.25f) else SurfaceCardElevated,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) HairLookPurpleGradientEnd else HairLookCardBorder
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onLanguageSelected(lang) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = lang.flagEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = lang.nativeName,
                                color = if (isSelected) (if (LocalThemeIsDark.current) Color.White else HairLookPurple) else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = HairLookPurpleGradientEnd,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Monetization & Roadmap Card: Free vs $2.00 Pro Plan
 */
@Composable
fun MonetizationRoadmapCard(
    isPaidSubscribed: Boolean,
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
        border = BorderStroke(1.2.dp, GoldStar.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldStar.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = GoldStar,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Monetization Plan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Free Tier vs VIP Pro ($2.00)",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldStar
                        )
                    }
                }

                Surface(
                    color = if (isPaidSubscribed) GoldStar.copy(alpha = 0.2f) else HairLookPurple.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isPaidSubscribed) "PRO ACTIVE" else "FREEMIUM",
                        color = if (isPaidSubscribed) GoldStar else HairLookPurpleGradientEnd,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two columns: Free vs Premium
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Free Plan Column
                Surface(
                    color = SurfaceCardElevated,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, HairLookCardBorder),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Free Tier",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "• 3 AI try-ons / day", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "• Unlimited AR preview", color = TextSecondary, fontSize = 11.sp)
                        Text(text = "• Barber guides & styles", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                // Premium VIP Column
                Surface(
                    color = if (LocalThemeIsDark.current) Color(0xFF201B12) else Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, GoldStar.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "VIP ($2.00/mo)",
                            fontWeight = FontWeight.Bold,
                            color = GoldStar,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "• Unlimited AI try-ons", color = Color(0xFFFDE68A), fontSize = 11.sp)
                        Text(text = "• Ultra HD AI Rendering", color = Color(0xFFFDE68A), fontSize = 11.sp)
                        Text(text = "• Ad-free & VIP styles", color = Color(0xFFFDE68A), fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onUpgradeClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPaidSubscribed) Color(0xFF10B981) else GoldStar,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Icon(
                    imageVector = if (isPaidSubscribed) Icons.Default.Check else Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isPaidSubscribed) "VIP Plan Active — Thank You!" else "Upgrade to VIP ($2.00 / month)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Complete Roadmap / Feature List Dialog
 */
@Composable
fun CompleteRoadmapDialog(
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
            border = BorderStroke(1.dp, HairLookPurpleGradientEnd.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
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
                            tint = HairLookPurpleGradientEnd,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All Key Features",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Surface(
                        color = HairLookPurple.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "10 Features",
                            color = HairLookPurpleGradientEnd,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AllKeyFeatures.forEachIndexed { index, feature ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(feature.accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = feature.accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = feature.titleEn,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = feature.titleUrdu,
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                        Surface(
                            color = feature.accentColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = feature.badgeText,
                                color = feature.accentColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HairLookPurpleGradientEnd)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Model for 4-column icon grid tile in Key Features
 */
data class KeyFeatureGridItem(
    val id: String,
    val titleEn: String,
    val titleUrdu: String,
    val icon: ImageVector,
    val tileColor: Color
)

/**
 * Key Features Dialog matching user's reference image:
 * - 4-Column Grid layout
 * - Square rounded colored icon tiles
 * - Centered 2-line title below each tile
 * - Men & Women Category removed (accessible on main screen)
 * - Premium (No Ads, More Styles) removed completely
 */
@Composable
fun KeyFeaturesViewAllDialog(
    currentLanguage: AppLanguage,
    onDismissRequest: () -> Unit,
    onFeatureClick: (KeyFeatureItem) -> Unit
) {
    val isUrdu = currentLanguage == AppLanguage.URDU

    val gridItems = remember {
        listOf(
            KeyFeatureGridItem(
                id = "live_camera_tryon",
                titleEn = "Live Camera\nTry-On",
                titleUrdu = "Live Camera\nTry-On",
                icon = Icons.Default.PhotoCamera,
                tileColor = Color(0xFF1E3A5F)
            ),
            KeyFeatureGridItem(
                id = "upload_photo_tryon",
                titleEn = "Photo Upload\nTry-On",
                titleUrdu = "Photo Upload\nTry-On",
                icon = Icons.Default.AddAPhoto,
                tileColor = Color(0xFF0F4C5C)
            ),
            KeyFeatureGridItem(
                id = "face_shape_analysis",
                titleEn = "AI Face Scan\n& Style Advice",
                titleUrdu = "AI Face Scan\n& Style Advice",
                icon = Icons.Default.Face,
                tileColor = Color(0xFF3B1F75)
            ),
            KeyFeatureGridItem(
                id = "color_tinting",
                titleEn = "Real-Time\nHair Color",
                titleUrdu = "Real-Time\nHair Color",
                icon = Icons.Default.Palette,
                tileColor = Color(0xFF5B21B6)
            ),
            KeyFeatureGridItem(
                id = "favorites_history",
                titleEn = "Favorites\n& History",
                titleUrdu = "Favorites\n& History",
                icon = Icons.Default.Favorite,
                tileColor = Color(0xFFB45309)
            ),
            KeyFeatureGridItem(
                id = "search_filters",
                titleEn = "Search\n& Filters",
                titleUrdu = "Search\n& Filters",
                icon = Icons.Default.Search,
                tileColor = Color(0xFF1D4ED8)
            ),
            KeyFeatureGridItem(
                id = "save_share",
                titleEn = "Save\n& Share",
                titleUrdu = "Save\n& Share",
                icon = Icons.Default.Share,
                tileColor = Color(0xFF4C1D95)
            ),
            KeyFeatureGridItem(
                id = "barber_guide",
                titleEn = "Barber\nGuide",
                titleUrdu = "Barber\nGuide",
                icon = Icons.Default.ContentCut,
                tileColor = Color(0xFF0F766E)
            ),
            KeyFeatureGridItem(
                id = "multi_language",
                titleEn = "Multi-\nLanguage",
                titleUrdu = "Multi-\nLanguage",
                icon = Icons.Default.Language,
                tileColor = Color(0xFF0284C7)
            ),
            KeyFeatureGridItem(
                id = "compare_styles",
                titleEn = "Compare\nTwo Styles",
                titleUrdu = "Compare\nTwo Styles",
                icon = Icons.Default.Compare,
                tileColor = Color(0xFF047857)
            )
        )
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = HairLookDarkBg),
            border = BorderStroke(1.5.dp, HairLookPurpleGradientEnd.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                // Header with "Key Features" exactly matching the reference image
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Key Features",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 20.sp
                    )

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4-Column Grid of square icon tiles
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    gridItems.chunked(4).forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { item ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable {
                                            val featureItem = AllKeyFeatures.firstOrNull { it.id == item.id }
                                                ?: KeyFeatureItem(
                                                    id = item.id,
                                                    titleEn = item.titleEn.replace("\n", " "),
                                                    titleUrdu = item.titleUrdu.replace("\n", " "),
                                                    descriptionEn = "",
                                                    descriptionUrdu = "",
                                                    badgeText = "",
                                                    icon = item.icon,
                                                    accentColor = item.tileColor,
                                                    gradientStart = item.tileColor,
                                                    gradientEnd = item.tileColor
                                                )
                                            onDismissRequest()
                                            onFeatureClick(featureItem)
                                        }
                                        .padding(vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(item.tileColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.titleEn,
                                            tint = Color.White,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = item.titleEn,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 14.sp,
                                        maxLines = 2
                                    )
                                }
                            }

                            if (rowItems.size < 4) {
                                repeat(4 - rowItems.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Clean Language Selection Dialog:
 * Select between English, Urdu, Hindi, Spanish, Arabic, French.
 */
@Composable
fun LanguagePickerDialog(
    currentLanguage: AppLanguage,
    onDismissRequest: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = HairLookDarkBg),
            border = BorderStroke(1.5.dp, HairLookPurpleGradientEnd.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🌐", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select Language",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                    }
                    IconButton(onClick = onDismissRequest, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = lang == currentLanguage
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) HairLookPurpleGradientEnd.copy(alpha = 0.20f) else HairLookCardBg
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) HairLookPurpleGradientEnd else HairLookCardBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onLanguageSelected(lang)
                                    onDismissRequest()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = lang.flagEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = lang.nativeName,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) HairLookPurpleGradientEnd else TextPrimary,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = lang.englishName,
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = HairLookPurpleGradientEnd,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismissRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HairLookCardBg)
                ) {
                    Text("Close", color = TextPrimary)
                }
            }
        }
    }
}
