package com.example.ui.screens.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import com.example.ui.components.AppLanguage
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TryOnHistoryItem
import com.example.data.model.UserHairProfile
import com.example.ui.theme.CardBorderStroke
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.SunsetRose
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    savedCount: Int,
    userProfile: UserHairProfile,
    tryOnHistory: List<TryOnHistoryItem>,
    isDarkTheme: Boolean = true,
    onThemeChanged: (Boolean) -> Unit = {},
    isPaidVersion: Boolean = true,
    onPaidVersionChanged: (Boolean) -> Unit = {},
    onOpenPaidAiStudio: () -> Unit = {},
    onNavigateToFavorites: () -> Unit,
    onFaceShapeChanged: (String) -> Unit,
    onHairTypeChanged: (String) -> Unit,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    onUpdateUserName: (String) -> Unit = {},
    onClearHistory: () -> Unit = {},
    onClearFavorites: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var highQualityAiMode by remember { mutableStateOf(true) }
    var showHairCareDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showClearFavoritesDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val faceShapes = listOf("Oval", "Square", "Round", "Heart", "Diamond")
    val hairTypes = listOf("Straight", "Wavy", "Curly", "Coily")

    // 1. Clear Try-On History Confirmation Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            containerColor = SurfaceCard,
            title = {
                Text(
                    text = "Clear Try-On History?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to clear your virtual try-on history? Your profile, face shape, and saved favorites will not be affected.",
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear History", color = SunsetRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    // 2. Clear All Favorites Confirmation Dialog
    if (showClearFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showClearFavoritesDialog = false },
            containerColor = SurfaceCard,
            title = {
                Text(
                    text = "Clear All Favorites?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove all saved hairstyles from your favorites? This action cannot be undone.",
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearFavorites()
                        showClearFavoritesDialog = false
                    }
                ) {
                    Text("Clear Favorites", color = SunsetRose, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearFavoritesDialog = false }) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    // 3. Help & FAQ Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            containerColor = SurfaceCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HairLook Help & FAQ",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "How to Browse Hairstyles",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "• Switch between Men and Women collections using the category tabs on the main screen.\n" +
                                "• Tap category pills (Fades, Crops, Bobs, Curls) to filter specific cuts.\n" +
                                "• Use the Search bar to find haircuts by name or key tags.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Face Shape Analysis",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "• Open 'Face Shape with AI Recommend' from the Features menu.\n" +
                                "• Take a front-facing selfie or pick an existing portrait from your photo library.\n" +
                                "• On-device geometry measures proportions (length, cheekbones, jawline) to estimate your shape (Oval, Round, Square, Heart, Oblong, Diamond) and curate flattering haircuts.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Virtual Try-On Options",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "• Live Camera Try-On: Uses real-time facial landmark tracking to preview hairstyles over your camera feed.\n" +
                                "• Photo Upload Try-On: Select a photo to fit haircuts directly onto your portrait.\n" +
                                "• Compare Hairstyles: Compare 2 or 3 haircuts side by side before deciding.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Barber Guide at the Salon",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "• Tap 'Show to Barber' on any hairstyle detail page to display 360° views, clipper numbers, and haircut specifications directly to your barber.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Support: HairLook Support",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Done", color = ChampagneGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 4. Privacy Policy & Data Transparency Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            containerColor = SurfaceCard,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PrivacyTip,
                        contentDescription = null,
                        tint = ChampagneGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Privacy & Data Policy",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "1. Local Camera & Landmark Analysis",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Real-time AR hairstyle fitting and Face Shape geometry detection are processed locally on your device via Google ML Kit. Camera preview frames are analyzed in temporary device memory to detect alignment points and are immediately discarded. Live camera video is never transmitted to any external server or saved without your explicit action.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "2. Optional Cloud AI Studio",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "The app includes an optional AI Studio mode where users may manually configure an external Hair AI REST API endpoint. When that optional mode is explicitly invoked, image requests are sent securely via HTTPS directly to your specified API host.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "3. Local Preferences & Storage",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Your saved favorites, display name, chosen face shape, and app theme are saved strictly in your device's private local app storage (SharedPreferences). You can clear your favorites or try-on history at any time from this Settings screen.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "4. No Account Tracking",
                        color = ChampagneGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "HairLook does not require account creation, email registration, passwords, or third-party advertising trackers.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Support: HairLook Support",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Understood", color = ChampagneGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showHairCareDialog) {
        AlertDialog(
            onDismissRequest = { showHairCareDialog = false },
            containerColor = SurfaceCard,
            title = {
                Text(
                    text = "Professional Salon & Barber Guide",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "1. Always bring a clear screenshot of your favorite HairStyle AI try-on to your barber or hair stylist.\n\n" +
                            "2. Discuss your hair texture and daily styling time with your stylist.\n\n" +
                            "3. Maintain short fades every 2-3 weeks; medium and long styles every 6-8 weeks.\n\n" +
                            "4. Use quality heat protectants and sulfate-free shampoos for long-lasting vibrancy.",
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showHairCareDialog = false }) {
                    Text("Got it", color = ChampagneGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = SurfaceCard,
            title = {
                Text(
                    text = "About HairStyle AI",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "HairStyle AI v1.0.0\n\n" +
                            "The modern AI hairstyle companion for men and women. Explore curated cuts, preview before/after transformations, and discover styles that fit your face shape perfectly.\n\n" +
                            "Crafted with modern Jetpack Compose & Material 3.",
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = ChampagneGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBackground),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Header & Avatar Card
        item {
            ProfileHeaderCard(
                userProfile = userProfile,
                onUpdateUserName = onUpdateUserName,
                savedCount = savedCount,
                historyCount = tryOnHistory.size,
                isPaidVersion = isPaidVersion
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(
                    title = "Saved Styles",
                    value = savedCount.toString(),
                    icon = Icons.Default.Favorite,
                    iconColor = SunsetRose,
                    onClick = onNavigateToFavorites,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    title = "Try-Ons",
                    value = tryOnHistory.size.toString(),
                    icon = Icons.Default.AutoAwesome,
                    iconColor = ChampagneGold,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    title = "Face Profile",
                    value = userProfile.faceShape,
                    icon = Icons.Default.Face,
                    iconColor = ElectricViolet,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Try-On History Section
        item {
            Text(
                text = "Recent Try-On History",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (tryOnHistory.isEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, CardBorderStroke, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "No Try-On History Yet",
                                style = MaterialTheme.typography.titleSmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Try on any haircut using Live AR or upload a photo to see your recent looks saved here.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(tryOnHistory) { item ->
                        Card(
                            modifier = Modifier
                                .width(180.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, CardBorderStroke, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
                        ) {
                            Column {
                                Image(
                                    painter = painterResource(id = item.imageRes),
                                    contentDescription = item.hairstyleName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                )
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = item.hairstyleName,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = item.date,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextMuted
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = ChampagneGold,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${item.matchScore}% Match",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ChampagneGold,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Personal Hair Profile Section (Face Shape & Hair Type Selectors)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, CardBorderStroke, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "My Hair Profile",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tailors AI recommendations and styling fit",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Face Shape",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(faceShapes) { shape ->
                            val isSelected = shape == userProfile.faceShape
                            Surface(
                                color = if (isSelected) ChampagneGold else SurfaceCardElevated,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ChampagneGold else CardBorderStroke
                                ),
                                modifier = Modifier.clickable { onFaceShapeChanged(shape) }
                            ) {
                                Text(
                                    text = shape,
                                    color = if (isSelected) ObsidianBackground else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Natural Hair Texture",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(hairTypes) { type ->
                            val isSelected = type == userProfile.hairType
                            Surface(
                                color = if (isSelected) ElectricViolet else SurfaceCardElevated,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) ElectricViolet else CardBorderStroke
                                ),
                                modifier = Modifier.clickable { onHairTypeChanged(type) }
                            ) {
                                Text(
                                    text = type,
                                    color = if (isSelected) ObsidianBackground else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // App Theme & Appearance Section (Light vs Dark)
        item {
            Text(
                text = "App Theme & Appearance",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, CardBorderStroke, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Interface Theme",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isDarkTheme) "Currently active: Dark Mode" else "Currently active: Light Mode",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isDarkTheme) ChampagneGold else ElectricViolet,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (isDarkTheme) ChampagneGold else Color(0xFFF59E0B),
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ThemeOptionCard(
                            title = "Light Mode",
                            subtitle = "Clean studio brightness",
                            icon = Icons.Default.LightMode,
                            isSelected = !isDarkTheme,
                            accentColor = Color(0xFFF59E0B),
                            onClick = { onThemeChanged(false) },
                            testTag = "theme_option_light",
                            modifier = Modifier.weight(1f)
                        )

                        ThemeOptionCard(
                            title = "Dark Mode",
                            subtitle = "Luxury obsidian elegance",
                            icon = Icons.Default.DarkMode,
                            isSelected = isDarkTheme,
                            accentColor = ChampagneGold,
                            onClick = { onThemeChanged(true) },
                            testTag = "theme_option_dark",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Settings Section
        item {
            Text(
                text = "Preferences & App Settings",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, CardBorderStroke, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column {
                    // Push Notifications
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Style Trends & Notifications", color = TextPrimary, fontSize = 14.sp)
                                Text(text = "Weekly trending hairstyle alerts", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBackground,
                                checkedTrackColor = ChampagneGold,
                                uncheckedTrackColor = SurfaceDark
                            )
                        )
                    }

                    // High Quality AI Model Mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "High Fidelity Render Mode", color = TextPrimary, fontSize = 14.sp)
                                Text(text = "Photo-realistic 4K strands & highlights", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = highQualityAiMode,
                            onCheckedChange = { highQualityAiMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ObsidianBackground,
                                checkedTrackColor = ElectricViolet,
                                uncheckedTrackColor = SurfaceDark
                            )
                        )
                    }

                    // Data & History Management
                    SettingRow(
                        icon = Icons.Default.DeleteOutline,
                        iconTint = SunsetRose,
                        title = "Clear Try-On History",
                        subtitle = "Safely remove all virtual try-on previews",
                        onClick = { showClearHistoryDialog = true }
                    )

                    SettingRow(
                        icon = Icons.Default.Favorite,
                        iconTint = SunsetRose,
                        title = "Clear All Favorites",
                        subtitle = "Remove all saved hairstyles from favorites",
                        onClick = { showClearFavoritesDialog = true }
                    )

                    // Help & FAQ
                    SettingRow(
                        icon = Icons.Default.HelpOutline,
                        iconTint = ChampagneGold,
                        title = "Help & FAQ Guide",
                        subtitle = "Browsing, face shape & virtual try-on instructions",
                        onClick = { showHelpDialog = true }
                    )

                    // Privacy Policy
                    SettingRow(
                        icon = Icons.Default.PrivacyTip,
                        iconTint = ElectricViolet,
                        title = "Privacy & Data Policy",
                        subtitle = "On-device processing & data transparency",
                        onClick = { showPrivacyDialog = true }
                    )

                    // Hair Care & Barber Consultation Guide Dialog
                    SettingRow(
                        icon = Icons.Default.ContentCut,
                        iconTint = ChampagneGold,
                        title = "Barber & Salon Consultation Guide",
                        subtitle = "Tips on asking your stylist for this exact cut",
                        onClick = { showHairCareDialog = true }
                    )

                    // About HairLook
                    SettingRow(
                        icon = Icons.Default.Info,
                        iconTint = TextSecondary,
                        title = "About HairLook",
                        subtitle = "Version 1.2.0 • HairLook Support",
                        onClick = { showAboutDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeaderCard(
    userProfile: UserHairProfile,
    onUpdateUserName: (String) -> Unit,
    savedCount: Int,
    historyCount: Int,
    isPaidVersion: Boolean
) {
    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userProfile.userName) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(
                1.dp,
                if (isPaidVersion) ChampagneGold.copy(alpha = 0.6f) else CardBorderStroke,
                RoundedCornerShape(22.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (isPaidVersion) ChampagneGold else TextMuted, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon),
                    contentDescription = "User Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Clickable Name / Username with Edit Pencil Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            tempName = userProfile.userName
                            showEditNameDialog = true
                        }
                        .padding(vertical = 2.dp)
                ) {
                    Text(
                        text = userProfile.userName,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Name",
                        tint = ChampagneGold,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Member since ${userProfile.memberSince}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }

    // Interactive Edit Name / Username Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            containerColor = SurfaceCard,
            title = {
                Text(
                    text = "Edit Profile Name",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your display name or studio username. It will appear on your profile and try-on portfolio.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Name / Username") },
                        placeholder = { Text("HairStyle Studio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChampagneGold,
                            unfocusedBorderColor = CardBorderStroke,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = ChampagneGold,
                            cursorColor = ChampagneGold
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = tempName.trim().ifBlank { "HairStyle Studio" }
                        onUpdateUserName(clean)
                        showEditNameDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold)
                ) {
                    Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun ProfileStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderStroke, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(text = subtitle, color = TextMuted, fontSize = 11.sp)
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ThemeOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) accentColor else CardBorderStroke,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.14f) else SurfaceCardElevated
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) accentColor.copy(alpha = 0.22f) else CardBorderStroke.copy(alpha = 0.5f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (isSelected) accentColor else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}


