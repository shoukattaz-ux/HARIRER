package com.example.ui.screens.outfit

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.OutfitAudience
import com.example.data.model.OutfitItem
import com.example.data.model.OutfitRepository
import com.example.ui.components.AppLanguage
import com.example.ui.components.ClothesBottomNavBar
import com.example.ui.components.ClothesTab
import com.example.ui.components.LanguagePickerDialog
import com.example.ui.theme.HairLookBlueAccent
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.HairLookDarkBg
import com.example.ui.theme.HairLookPinkAccent
import com.example.ui.theme.HairLookPurpleGradientEnd
import com.example.ui.theme.HairLookPurpleGradientStart
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Dedicated Home Screen for Outfit & Clothes Studio.
 * Designed with the exact same structure and visual polish as the Hairstyle Home Screen,
 * supporting Men, Women, and Baby categories, plus Saved outfits.
 */
@Composable
fun ClothesHomeScreen(
    onNavigateBack: () -> Unit,
    onTryOutfit: (OutfitItem) -> Unit,
    isDarkTheme: Boolean = true,
    onThemeChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    var selectedTab by remember { mutableStateOf(ClothesTab.HOME) }
    var selectedAudience by remember { mutableStateOf(OutfitAudience.MEN) }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchOpen by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(AppLanguage.ENGLISH) }

    val savedOutfitIds = remember { mutableStateListOf<String>("outfit_suit_navy", "outfit_baby_suit") }

    // Synchronize audience with bottom tab
    when (selectedTab) {
        ClothesTab.MEN -> selectedAudience = OutfitAudience.MEN
        ClothesTab.WOMEN -> selectedAudience = OutfitAudience.WOMEN
        ClothesTab.BABY -> selectedAudience = OutfitAudience.BABY
        else -> {}
    }

    val currentCategories = remember(selectedAudience) {
        OutfitRepository.getCategoriesForAudience(selectedAudience)
    }

    val filteredOutfits = remember(selectedTab, selectedAudience, selectedCategory, searchQuery, savedOutfitIds.size) {
        if (selectedTab == ClothesTab.SAVED) {
            OutfitRepository.outfits.filter { savedOutfitIds.contains(it.id) }
        } else {
            OutfitRepository.filterOutfits(
                audience = selectedAudience,
                category = selectedCategory,
                query = searchQuery
            )
        }
    }

    val activeColor = when (selectedAudience) {
        OutfitAudience.MEN -> HairLookBlueAccent
        OutfitAudience.WOMEN -> HairLookPinkAccent
        OutfitAudience.BABY -> Color(0xFFFBBF24)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            ClothesBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab
                    selectedCategory = "All"
                },
                savedCount = savedOutfitIds.size
            )
        }
    ) { innerPadding ->
        if (selectedTab == ClothesTab.PROFILE) {
            ClothesProfileView(
                isDarkTheme = isDarkTheme,
                onThemeChanged = onThemeChanged,
                currentLanguage = currentLanguage,
                onLanguageClick = { showLanguageDialog = true },
                savedCount = savedOutfitIds.size,
                onViewSaved = { selectedTab = ClothesTab.SAVED },
                onBack = onNavigateBack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HairLookDarkBg)
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
            // 1. Top Header Bar (Back button, Logo, Language, Search)
            item {
                ClothesHeaderBar(
                    onBackClick = onNavigateBack,
                    onSearchClick = { isSearchOpen = !isSearchOpen },
                    onLanguageClick = { showLanguageDialog = true },
                    currentLanguage = currentLanguage,
                    isSearchActive = isSearchOpen || searchQuery.isNotEmpty()
                )
            }

            // 2. Expandable Search Input
            item {
                AnimatedVisibility(
                    visible = isSearchOpen || searchQuery.isNotEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("clothes_search_input"),
                            placeholder = {
                                Text(
                                    text = "Search suits, blazers, kurtas, jackets...",
                                    color = TextMuted,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = activeColor)
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = HairLookCardBg,
                                unfocusedContainerColor = HairLookCardBg,
                                focusedBorderColor = activeColor,
                                unfocusedBorderColor = HairLookCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }
            }

            // 3. Three Large Category Buttons: [ 👨 Men ] [ 👩 Women ] [ 👶 Baby ]
            if (selectedTab != ClothesTab.SAVED) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    ThreeAudienceButtonsRow(
                        selectedAudience = selectedAudience,
                        onSelectMen = {
                            selectedAudience = OutfitAudience.MEN
                            selectedTab = ClothesTab.MEN
                            selectedCategory = "All"
                        },
                        onSelectWomen = {
                            selectedAudience = OutfitAudience.WOMEN
                            selectedTab = ClothesTab.WOMEN
                            selectedCategory = "All"
                        },
                        onSelectBaby = {
                            selectedAudience = OutfitAudience.BABY
                            selectedTab = ClothesTab.BABY
                            selectedCategory = "All"
                        }
                    )
                }

                // 4. Hero Live Fitting Banner
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    ClothesHeroBanner(
                        onOpenFitting = {
                            val first = filteredOutfits.firstOrNull() ?: OutfitRepository.outfits.first()
                            onTryOutfit(first)
                        },
                        accentColor = activeColor,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                // 5. Horizontal Category Filter Chips
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentCategories) { category ->
                            val isSelected = selectedCategory == category
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) activeColor else HairLookCardBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) activeColor else HairLookCardBorder
                                ),
                                modifier = Modifier.clickable { selectedCategory = category }
                            ) {
                                Text(
                                    text = category,
                                    color = if (isSelected) Color.Black else TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Saved Header
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                        Text(
                            text = "Saved Outfits & Clothes",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Your favorite suits, jackets and dresses ready to try",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            }

            // Section Title
            item {
                Spacer(modifier = Modifier.height(18.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTab == ClothesTab.SAVED) "Favorite Wardrobe"
                        else when (selectedAudience) {
                            OutfitAudience.MEN -> "Men's Collection"
                            OutfitAudience.WOMEN -> "Women's Collection"
                            OutfitAudience.BABY -> "Baby & Kids Outfits"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${filteredOutfits.size} items",
                        style = MaterialTheme.typography.labelSmall,
                        color = activeColor
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // 6. Outfits Cards (Horizontal scroll of featured cards when on Home)
            if (selectedTab == ClothesTab.HOME && searchQuery.isEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredOutfits.take(5)) { outfit ->
                            val isSaved = savedOutfitIds.contains(outfit.id)
                            OutfitFeaturedCard(
                                outfit = outfit,
                                isSaved = isSaved,
                                onToggleSave = {
                                    if (isSaved) savedOutfitIds.remove(outfit.id)
                                    else savedOutfitIds.add(outfit.id)
                                },
                                onTryOn = { onTryOutfit(outfit) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // 7. Outfits Grid / Vertical List
            items(filteredOutfits.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (outfit in rowItems) {
                        val isSaved = savedOutfitIds.contains(outfit.id)
                        Box(modifier = Modifier.weight(1f)) {
                            OutfitCompactCard(
                                outfit = outfit,
                                isSaved = isSaved,
                                onToggleSave = {
                                    if (isSaved) savedOutfitIds.remove(outfit.id)
                                    else savedOutfitIds.add(outfit.id)
                                },
                                onTryOn = { onTryOutfit(outfit) }
                            )
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            // Empty state if nothing matches
            if (filteredOutfits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Checkroom,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No clothes found",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try changing category or clear search terms",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = {
                currentLanguage = it
                showLanguageDialog = false
            },
            onDismissRequest = { showLanguageDialog = false }
        )
    }
}

@Composable
private fun ClothesHeaderBar(
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onLanguageClick: () -> Unit,
    currentLanguage: AppLanguage,
    isSearchActive: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(HairLookCardBg)
                    .border(1.dp, HairLookCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Welcome",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, HairLookBlueAccent, CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "StyleStudio",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Clothes & Outfits",
                    style = MaterialTheme.typography.labelSmall,
                    color = HairLookBlueAccent,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Language Pill
            Surface(
                onClick = onLanguageClick,
                modifier = Modifier
                    .height(38.dp)
                    .clip(RoundedCornerShape(19.dp)),
                color = HairLookCardBg,
                border = BorderStroke(1.dp, HairLookCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = currentLanguage.flagEmoji, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = currentLanguage.code.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HairLookBlueAccent
                    )
                }
            }

            // Search Icon
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSearchActive) HairLookBlueAccent.copy(alpha = 0.25f) else HairLookCardBg
                    )
                    .border(
                        1.dp,
                        if (isSearchActive) HairLookBlueAccent else HairLookCardBorder,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Search",
                    tint = if (isSearchActive) HairLookBlueAccent else TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ThreeAudienceButtonsRow(
    selectedAudience: OutfitAudience,
    onSelectMen: () -> Unit,
    onSelectWomen: () -> Unit,
    onSelectBaby: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // [ 👨 Men ] Button
        AudienceCardButton(
            emoji = "👨",
            title = "Men",
            subtitle = "Suits & Kurtas",
            isSelected = selectedAudience == OutfitAudience.MEN,
            accentColor = HairLookBlueAccent,
            onClick = onSelectMen,
            modifier = Modifier.weight(1f)
        )

        // [ 👩 Women ] Button
        AudienceCardButton(
            emoji = "👩",
            title = "Women",
            subtitle = "Gowns & Blazers",
            isSelected = selectedAudience == OutfitAudience.WOMEN,
            accentColor = HairLookPinkAccent,
            onClick = onSelectWomen,
            modifier = Modifier.weight(1f)
        )

        // [ 👶 Baby ] Button
        AudienceCardButton(
            emoji = "👶",
            title = "Baby",
            subtitle = "Rompers & Kurtas",
            isSelected = selectedAudience == OutfitAudience.BABY,
            accentColor = Color(0xFFFBBF24),
            onClick = onSelectBaby,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AudienceCardButton(
    emoji: String,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(72.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.5.dp,
                color = if (isSelected) accentColor else HairLookCardBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) accentColor.copy(alpha = 0.22f) else HairLookCardBg
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = emoji, fontSize = 16.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) accentColor else TextPrimary,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ClothesHeroBanner(
    onOpenFitting: () -> Unit,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                1.5.dp,
                Brush.horizontalGradient(listOf(accentColor, HairLookPurpleGradientEnd)),
                RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onOpenFitting),
        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(accentColor, HairLookPurpleGradientEnd))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Live Fitting",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Virtual Fitting Room",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = accentColor.copy(alpha = 0.20f)
                        ) {
                            Text(
                                text = "LIVE AR",
                                color = accentColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Try suits, blazers & dresses in real-time camera",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun OutfitFeaturedCard(
    outfit: OutfitItem,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onTryOn: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, HairLookCardBorder, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF0F172A))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = outfit.drawableRes),
                    contentDescription = outfit.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isSaved) Color(0xFFEF4444) else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.70f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                ) {
                    Text(
                        text = outfit.tag,
                        color = HairLookBlueAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = outfit.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )

            Text(
                text = "${outfit.category} • ${outfit.colorName}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onTryOn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HairLookBlueAccent)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Try On",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun OutfitCompactCard(
    outfit: OutfitItem,
    isSaved: Boolean,
    onToggleSave: () -> Unit,
    onTryOn: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, HairLookCardBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = outfit.drawableRes),
                    contentDescription = outfit.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onToggleSave,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.65f))
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isSaved) Color(0xFFEF4444) else Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = outfit.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )

            Text(
                text = outfit.category,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onTryOn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = HairLookBlueAccent),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Try On",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun ClothesProfileView(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    currentLanguage: AppLanguage,
    onLanguageClick: () -> Unit,
    savedCount: Int,
    onViewSaved: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HairLookDarkBg),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(HairLookCardBg)
                        .border(1.dp, HairLookCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Profile & Settings",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Clothes & Showroom Studio Preferences",
                        style = MaterialTheme.typography.labelSmall,
                        color = HairLookBlueAccent
                    )
                }
            }
        }

        // Profile Avatar Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
                border = BorderStroke(1.dp, HairLookCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(HairLookBlueAccent, HairLookPurpleGradientEnd))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fashion Member",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Virtual Showroom & Mannequin Try-On",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        // App Theme Toggle (Dark Mode / Light Mode)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
                border = BorderStroke(1.dp, HairLookBlueAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(HairLookBlueAccent.copy(alpha = 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                    contentDescription = null,
                                    tint = HairLookBlueAccent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isDarkTheme) "Dark Theme" else "Light Theme",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (isDarkTheme) "Obsidian Luxury Dark Mode" else "Crisp Elegant Light Mode",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { onThemeChanged(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = HairLookBlueAccent,
                                uncheckedThumbColor = HairLookBlueAccent,
                                uncheckedTrackColor = HairLookCardBorder
                            )
                        )
                    }
                }
            }
        }

        // Saved Wardrobe Counter
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onViewSaved() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
                border = BorderStroke(1.dp, HairLookCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444).copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Saved Wardrobe",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "$savedCount items saved for try-on",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = TextSecondary
                    )
                }
            }
        }

        // Language Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onLanguageClick() },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
                border = BorderStroke(1.dp, HairLookCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(HairLookPurpleGradientEnd.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = HairLookPurpleGradientEnd,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "App Language",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${currentLanguage.flagEmoji} ${currentLanguage.englishName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = HairLookPurpleGradientEnd
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Change Language",
                        tint = TextSecondary
                    )
                }
            }
        }

        // About & Version Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg),
                border = BorderStroke(1.dp, HairLookCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Clothes & Showroom Studio",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 2.0 • Real Mannequin Fit & Clean Studio Try-On",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}
