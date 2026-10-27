package com.example.ui.screens.home

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.example.ui.components.AllKeyFeatures
import com.example.ui.components.AppLanguage
import com.example.ui.components.CategoryChipRow
import com.example.ui.components.HairstyleCard
import com.example.ui.components.KeyFeatureItem
import com.example.ui.components.KeyFeaturesViewAllDialog
import com.example.ui.components.LanguagePickerDialog
import com.example.ui.components.SectionHeader
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

/**
 * Screen 2 — Home Screen
 * Matches Screen 2 in the reference image:
 * - HairLook top header + search icon
 * - "Find Your Perfect Hairstyle" heading & "Try different hairstyles with AI" subtitle
 * - Two large category buttons: [ 👨 Men ] [ 👩 Women ]
 * - Horizontally scrollable "Popular Styles" cards
 * - Quick action banners: Live Camera Try-On, AI Face Shape Analysis, Compare Styles, Barber Guide.
 */
@Composable
fun HomeScreen(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    isSearchOpen: Boolean,
    onToggleSearch: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onSelectHairstyle: (String) -> Unit,
    onQuickTryOn: (HairstyleItem) -> Unit,
    onToggleFavorite: (String) -> Unit,
    isFavorite: (String) -> Boolean,
    onNavigateToMen: () -> Unit = {},
    onNavigateToWomen: () -> Unit = {},
    onNavigateToLiveAr: () -> Unit = {},
    onNavigateToFaceShape: () -> Unit = {},
    onNavigateToCompare: () -> Unit = {},
    onNavigateToBarberGuide: () -> Unit = {},
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    onLanguageSelected: (AppLanguage) -> Unit = {},
    isPaidSubscribed: Boolean = false,
    onUpgradeClick: () -> Unit = {},
    onNavigateToPhotoUpload: () -> Unit = {},
    onNavigateToFavorites: () -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
    userName: String = "HairStyle Studio",
    modifier: Modifier = Modifier
) {
    var showKeyFeaturesDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    val currentHorizontalCategories = remember(selectedGender) {
        if (selectedGender == Gender.MEN) {
            HairstyleRepository.homeMenCategories
        } else {
            HairstyleRepository.homeWomenCategories
        }
    }

    val popularList = remember(selectedGender) {
        HairstyleRepository.getTrendingByGender(selectedGender)
    }

    val filteredHairstyles by remember(selectedGender, selectedCategory, searchQuery) {
        derivedStateOf {
            HairstyleRepository.filterHome(
                gender = selectedGender,
                category = selectedCategory,
                query = searchQuery
            )
        }
    }

    val activeAccentColor = if (selectedGender == Gender.MEN) HairLookBlueAccent else HairLookPinkAccent

    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(HairLookPurpleGradientStart, HairLookPurpleGradientEnd)
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HairLookDarkBg),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 1. Top App Bar (Logo, Studio Name, and Search Button in Corner)
        item {
            HomeHeaderBar(
                userName = userName,
                onSearchClick = onToggleSearch,
                isSearchActive = isSearchOpen || searchQuery.isNotEmpty(),
                onNavigateBack = onNavigateBack
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
                        onValueChange = onSearchQueryChanged,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input"),
                        placeholder = {
                            Text(
                                text = if (selectedGender == Gender.MEN)
                                    "Search men's fades, crops, quiffs..."
                                else
                                    "Search women's bobs, layers, braids...",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = HairLookPurpleGradientEnd
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchQueryChanged("") },
                                    modifier = Modifier.testTag("clear_search_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear search",
                                        tint = TextMuted
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = HairLookCardBg,
                            unfocusedContainerColor = HairLookCardBg,
                            focusedBorderColor = HairLookPurpleGradientEnd,
                            unfocusedBorderColor = HairLookCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }
        }

        // 3. Main Hero Heading & Subtitle (Screen 2 Reference)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Find Your Perfect Hairstyle",
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Try different hairstyles with AI",
                    style = MaterialTheme.typography.titleMedium,
                    color = HairLookPurpleGradientEnd,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 4. Three Action Buttons: [ 👨 Men ] [ 👩 Women ] [ ✨ Key Features (View All) ]
        item {
            ThreeCategoryButtonsRow(
                selectedGender = selectedGender,
                onSelectMen = {
                    onGenderSelected(Gender.MEN)
                    onCategorySelected("All")
                    onNavigateToMen()
                },
                onSelectWomen = {
                    onGenderSelected(Gender.WOMEN)
                    onCategorySelected("All")
                    onNavigateToWomen()
                },
                onViewAllFeatures = {
                    showKeyFeaturesDialog = true
                },
                currentLanguage = currentLanguage
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 5. Popular Styles Horizontal Row (Screen 2 Reference)
        if (searchQuery.isBlank() && popularList.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Popular Styles",
                    subtitle = "Explore top rated ${selectedGender.displayName.lowercase()} haircuts"
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(popularList, key = { "popular_${it.id}" }) { item ->
                        HairstyleCard(
                            item = item,
                            isFavorite = isFavorite(item.id),
                            onCardClick = { onSelectHairstyle(item.id) },
                            onFavoriteClick = { onToggleFavorite(item.id) },
                            onTryOnClick = { onQuickTryOn(item) },
                            modifier = Modifier.width(200.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 7. Horizontal Category Filters Menu (Directly accessible without feed clutter)
        item {
            CategoryChipRow(
                categories = currentHorizontalCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected,
                activeColor = activeAccentColor
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // 9. Grid Section Header
        item {
            val gridSectionTitle = if (searchQuery.isNotBlank()) {
                "Results for \"$searchQuery\" (${filteredHairstyles.size})"
            } else if (selectedCategory == "All") {
                if (selectedGender == Gender.MEN) "All Men’s Hairstyles" else "All Women’s Hairstyles"
            } else {
                "$selectedCategory Styles"
            }

            SectionHeader(
                title = gridSectionTitle,
                subtitle = if (searchQuery.isBlank())
                    "Tap any style to inspect or try on"
                else
                    "Filtered in ${selectedGender.displayName} collection"
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 10. 2-Column Grid or Empty Search State
        if (filteredHairstyles.isEmpty()) {
            item {
                HomeEmptySearchState(
                    query = searchQuery,
                    selectedGender = selectedGender,
                    onResetSearch = {
                        onSearchQueryChanged("")
                        onCategorySelected("All")
                    }
                )
            }
        } else {
            val chunkedItems = filteredHairstyles.chunked(2)
            items(chunkedItems, key = { row -> row.joinToString { it.id } }) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (item in rowItems) {
                        HairstyleCard(
                            item = item,
                            isFavorite = isFavorite(item.id),
                            onCardClick = { onSelectHairstyle(item.id) },
                            onFavoriteClick = { onToggleFavorite(item.id) },
                            onTryOnClick = { onQuickTryOn(item) },
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

    if (showKeyFeaturesDialog) {
        KeyFeaturesViewAllDialog(
            currentLanguage = currentLanguage,
            onDismissRequest = { showKeyFeaturesDialog = false },
            onFeatureClick = { feature ->
                when (feature.id) {
                    "men_women_category" -> {
                        if (selectedGender == Gender.MEN) onNavigateToMen() else onNavigateToWomen()
                    }
                    "live_camera_tryon" -> onNavigateToLiveAr()
                    "upload_photo_tryon" -> onNavigateToPhotoUpload()
                    "face_shape_analysis" -> onNavigateToFaceShape()
                    "color_tinting" -> {
                        val sample = popularList.firstOrNull() ?: HairstyleRepository.getAllHairstyles().first()
                        onQuickTryOn(sample)
                    }
                    "compare_styles" -> onNavigateToCompare()
                    "barber_guide" -> onNavigateToBarberGuide()
                    "favorites_history" -> onNavigateToFavorites()
                    "multi_language" -> {
                        showLanguageDialog = true
                    }
                    else -> {}
                }
            }
        )
    }

    if (showLanguageDialog) {
        LanguagePickerDialog(
            currentLanguage = currentLanguage,
            onDismissRequest = { showLanguageDialog = false },
            onLanguageSelected = onLanguageSelected
        )
    }
}

@Composable
private fun HomeHeaderBar(
    userName: String,
    onSearchClick: () -> Unit,
    isSearchActive: Boolean,
    onNavigateBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Logo & User Studio Name
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onNavigateBack != null) {
                IconButton(
                    onClick = onNavigateBack,
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
                Spacer(modifier = Modifier.width(10.dp))
            }

            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "Studio Logo",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, HairLookPurpleGradientEnd, CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                    maxLines = 1
                )
                Text(
                    text = "Find Your Style",
                    style = MaterialTheme.typography.labelSmall,
                    color = HairLookPurpleGradientEnd,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Top Action: ONLY Search button in corner
        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    if (isSearchActive) HairLookPurpleGradientEnd.copy(alpha = 0.25f) else HairLookCardBg
                )
                .border(
                    1.dp,
                    if (isSearchActive) HairLookPurpleGradientEnd else HairLookCardBorder,
                    CircleShape
                )
                .testTag("home_search_btn")
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = if (isSearchActive) HairLookPurpleGradientEnd else TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Three Action Buttons: [ 👨 Men ] [ 👩 Women ] and [ ✨ Key Features (View All) ]
 * Clean, compact, and perfectly aligned as requested by the user.
 */
@Composable
private fun ThreeCategoryButtonsRow(
    selectedGender: Gender,
    onSelectMen: () -> Unit,
    onSelectWomen: () -> Unit,
    onViewAllFeatures: () -> Unit,
    currentLanguage: AppLanguage
) {
    val isUrdu = currentLanguage == AppLanguage.URDU
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // [ 👨 Men ] Button
        Card(
            modifier = Modifier
                .weight(1f)
                .height(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(
                    width = 1.5.dp,
                    color = if (selectedGender == Gender.MEN) HairLookBlueAccent else HairLookCardBorder,
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable { onSelectMen() }
                .testTag("gender_selector_men"),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedGender == Gender.MEN)
                    HairLookBlueAccent.copy(alpha = 0.22f)
                else
                    HairLookCardBg
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Men",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedGender == Gender.MEN) HairLookBlueAccent else TextPrimary,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Styles",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }

        // [ 👩 Women ] Button
        Card(
            modifier = Modifier
                .weight(1f)
                .height(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(
                    width = 1.5.dp,
                    color = if (selectedGender == Gender.WOMEN) HairLookPinkAccent else HairLookCardBorder,
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable { onSelectWomen() }
                .testTag("gender_selector_women"),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedGender == Gender.WOMEN)
                    HairLookPinkAccent.copy(alpha = 0.22f)
                else
                    HairLookCardBg
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "👩", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Women",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedGender == Gender.WOMEN) HairLookPinkAccent else TextPrimary,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Styles",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }

        // [ ✨ Key Features (View All) ] 3rd Button
        Card(
            modifier = Modifier
                .weight(1.15f)
                .height(68.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(
                    width = 1.5.dp,
                    color = HairLookPurpleGradientEnd.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(18.dp)
                )
                .clickable { onViewAllFeatures() }
                .testTag("btn_key_features_view_all"),
            colors = CardDefaults.cardColors(
                containerColor = HairLookPurpleGradientEnd.copy(alpha = 0.16f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = HairLookPurpleGradientEnd,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Step-by-Step",
                        style = MaterialTheme.typography.labelSmall,
                        color = HairLookPurpleGradientEnd,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveCameraHeroCard(
    onOpenLiveAr: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .border(
                1.5.dp,
                Brush.horizontalGradient(listOf(HairLookPurpleGradientStart, HairLookPurpleGradientEnd)),
                RoundedCornerShape(22.dp)
            )
            .clickable { onOpenLiveAr() }
            .testTag("home_live_ar_hero_card"),
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
                            Brush.linearGradient(listOf(HairLookPurpleGradientStart, HairLookPurpleGradientEnd))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Live AR",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Live Camera Try-On",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF00E676).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "LIVE AR",
                                color = Color(0xFF00E676),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Instant front-camera face tracking filter",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(HairLookDarkBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = HairLookPurpleGradientEnd,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun QuickFeatureCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, HairLookCardBorder, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun HomeEmptySearchState(
    query: String,
    selectedGender: Gender,
    onResetSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No haircuts found for \"$query\"",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Try searching for fades, quiffs, bobs, layers, or clear search filters.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
            onClick = onResetSearch,
            colors = ButtonDefaults.buttonColors(containerColor = HairLookPurpleGradientEnd),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Clear Search", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
