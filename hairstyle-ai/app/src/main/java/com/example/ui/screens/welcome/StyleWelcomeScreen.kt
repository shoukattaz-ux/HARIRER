package com.example.ui.screens.welcome

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldDark
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HairLookBlueAccent
import com.example.ui.theme.HairLookDarkBg
import com.example.ui.theme.HairLookPurple
import com.example.ui.theme.LocalThemeIsDark
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Modern, high-impact Landing / Welcome screen for Look & Style AI Studio.
 * Offers two primary try-on modes:
 * 1. Hairstyle Try-On (Live Camera & Photos)
 * 2. Outfit & Clothes Try-On (Suits, Blazers, Jackets, Traditional)
 */
@Composable
fun StyleWelcomeScreen(
    onSelectHairstyles: () -> Unit,
    onSelectClothes: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
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
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. App Emblem & Logo Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(ChampagneGold, HairLookPurple, HairLookBlueAccent)
                        )
                    )
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(HairLookDarkBg),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = null,
                        tint = HairLookBlueAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Look & Style AI Studio",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Instant Virtual Hairstyle & Outfit Try-On",
                style = MaterialTheme.typography.bodyMedium,
                color = ChampagneGold,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Trust Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrustBadge(text = "Live AR Camera", icon = Icons.Default.CameraAlt)
                Spacer(modifier = Modifier.width(8.dp))
                TrustBadge(text = "Real Human Hair", icon = Icons.Default.Face)
                Spacer(modifier = Modifier.width(8.dp))
                TrustBadge(text = "Designer Clothes", icon = Icons.Default.Checkroom)
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "SELECT TRY-ON EXPERIENCE",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.2.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. OPTION 1: HAIRSTYLE TRY-ON CARD
            val isDark = LocalThemeIsDark.current
            ModeSelectionCard(
                title = "Hairstyle Studio",
                subtitle = "Try realistic human hairstyles in real-time live camera or on uploaded photos",
                badgeText = "Live AR Hair",
                badgeColor = if (isDark) ChampagneGold else ChampagneGoldDark,
                icon = Icons.Default.ContentCut,
                features = listOf(
                    "100% Real human hair textures & natural shine",
                    "Real-time head tilt & movement tracking",
                    "Fades, buzz cuts, textured crops, curls & waves",
                    "Dual-layer complete original hair concealment"
                ),
                gradientColors = if (isDark) {
                    listOf(Color(0xFF2E2413), Color(0xFF1E1A14), Color(0xFF151412))
                } else {
                    listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7), Color(0xFFFDF0CD))
                },
                borderColor = if (isDark) ChampagneGold.copy(alpha = 0.55f) else ChampagneGoldDark.copy(alpha = 0.65f),
                buttonText = "Open Hairstyle Studio",
                buttonColor = if (isDark) ChampagneGold else ChampagneGoldDark,
                buttonTextColor = if (isDark) Color.Black else Color.White,
                onClick = onSelectHairstyles
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. OPTION 2: OUTFIT & CLOTHES STUDIO CARD
            ModeSelectionCard(
                title = "Outfit & Clothes Studio",
                subtitle = "See how formal suits, blazers, jackets & traditional wear look on your body",
                badgeText = "Fashion & Suits",
                badgeColor = if (isDark) HairLookBlueAccent else Color(0xFF0284C7),
                icon = Icons.Default.Checkroom,
                features = listOf(
                    "Italian 3-piece formal suits & black-tie dinner tuxedos",
                    "Executive charcoal blazers & casual denim jackets",
                    "Royal embroidered festive kurtas & modern hoodies",
                    "Natural shoulder & torso fitting on camera & photos"
                ),
                gradientColors = if (isDark) {
                    listOf(Color(0xFF14243B), Color(0xFF121B2B), Color(0xFF0F1522))
                } else {
                    listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE), Color(0xFFD6EEFF))
                },
                borderColor = if (isDark) HairLookBlueAccent.copy(alpha = 0.55f) else Color(0xFF0284C7).copy(alpha = 0.65f),
                buttonText = "Open Clothes Studio",
                buttonColor = if (isDark) HairLookBlueAccent else Color(0xFF0284C7),
                buttonTextColor = if (isDark) Color.Black else Color.White,
                onClick = onSelectClothes
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Footer note
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = ChampagneGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Fast on-device rendering • 100% Private & Secure",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ModeSelectionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    features: List<String>,
    gradientColors: List<Color>,
    borderColor: Color,
    buttonText: String,
    buttonColor: Color,
    buttonTextColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(gradientColors))
                .padding(20.dp)
        ) {
            Column {
                // Header with Icon & Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(buttonColor.copy(alpha = 0.15f))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = buttonColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = buttonColor.copy(alpha = 0.18f),
                        border = BorderStroke(1.dp, buttonColor.copy(alpha = 0.40f))
                    ) {
                        Text(
                            text = badgeText,
                            color = buttonColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Feature checkmarks
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    features.forEach { feature ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = feature,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary.copy(alpha = 0.90f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Button
                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(
                        text = buttonText,
                        color = buttonTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = buttonTextColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrustBadge(text: String, icon: ImageVector) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = SurfaceCard,
        border = BorderStroke(1.dp, CardBorderStroke)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (LocalThemeIsDark.current) ChampagneGold else ChampagneGoldDark,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
