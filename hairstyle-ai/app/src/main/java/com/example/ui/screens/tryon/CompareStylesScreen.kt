package com.example.ui.screens.tryon

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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Texture
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Gender
import com.example.data.model.HairstyleItem
import com.example.data.model.hairTexture
import com.example.data.model.stylingDifficulty
import com.example.data.repository.HairstyleRepository
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.GoldStar
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
 * Screen 9 — Compare Hairstyles
 * Allows users to select 2 or 3 hairstyles from the existing collection,
 * compare them side-by-side with preview images, detailed attribute table
 * (Length, Texture, Maintenance, Difficulty, Face Shapes), open their individual
 * detail pages, try them on, or remove them.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CompareStylesScreen(
    initialStyleId1: String? = null,
    initialStyleId2: String? = null,
    onTryStyle: (HairstyleItem) -> Unit,
    onSelectHairstyle: (String) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allStyles = remember { HairstyleRepository.getAllHairstyles() }

    val default1 = remember {
        initialStyleId1?.let { HairstyleRepository.getById(it) }
            ?: allStyles.firstOrNull { it.id == "m_natural_quiff" }
            ?: allStyles.first()
    }
    val default2 = remember {
        initialStyleId2?.let { HairstyleRepository.getById(it) }
            ?: allStyles.firstOrNull { it.id == "m_fade_01" }
            ?: allStyles.getOrNull(1)
            ?: allStyles.first()
    }

    // List of currently compared styles: 2 or 3 items
    var selectedStyles by remember {
        mutableStateOf(listOf(default1, default2))
    }

    // Currently focused slot to replace (0, 1, or 2)
    var activeSlotIndex by remember { mutableIntStateOf(0) }
    var selectedGenderFilter by remember { mutableStateOf<Gender?>(default1.gender) }

    val filteredList = remember(selectedGenderFilter) {
        if (selectedGenderFilter == null) allStyles
        else allStyles.filter { it.gender == selectedGenderFilter }
    }

    val purpleGradient = Brush.horizontalGradient(
        colors = listOf(HairLookPurpleGradientStart, HairLookPurpleGradientEnd)
    )

    val slotAccentColors = listOf(
        HairLookBlueAccent,
        HairLookPinkAccent,
        ChampagneGold
    )

    // Sharing action with safe fallback
    val shareComparison: () -> Unit = {
        val names = selectedStyles.joinToString(" vs ") { it.name }
        val shareText = "Comparing hairstyles on HairLook: $names!\n\n" +
                selectedStyles.mapIndexed { idx, style ->
                    "Style ${idx + 1} (${style.name}):\n" +
                            "• Category: ${style.category}\n" +
                            "• Length: ${style.hairLength} | Texture: ${style.hairTexture}\n" +
                            "• Maintenance: ${style.maintenance} | Difficulty: ${style.stylingDifficulty}\n" +
                            "• Best Face Shapes: ${style.faceShapes.joinToString(", ")}"
                }.joinToString("\n\n")

        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Hairstyle Comparison: $names")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            context.startActivity(Intent.createChooser(intent, "Share Comparison"))
        } catch (e: Exception) {
            try {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                val clip = ClipData.newPlainText("Comparison Specs", shareText)
                clipboard?.setPrimaryClip(clip)
                Toast.makeText(context, "Comparison specs copied to clipboard!", Toast.LENGTH_LONG).show()
            } catch (err: Exception) {
                Toast.makeText(context, "Comparison ready to view", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                        .testTag("compare_back_btn")
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
                        text = "Compare Hairstyles",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Compare 2 or 3 styles side by side",
                        style = MaterialTheme.typography.bodySmall,
                        color = HairLookPurpleGradientEnd
                    )
                }
            }

            // Share Action Button
            IconButton(
                onClick = shareComparison,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(HairLookCardBg)
                    .border(1.dp, HairLookCardBorder, CircleShape)
                    .testTag("compare_share_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share",
                    tint = TextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Slot Switcher Row: [ Style 1 ] [ Style 2 ] [ Style 3 / + Add 3rd Style ]
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = HairLookCardBg,
                border = BorderStroke(1.dp, HairLookCardBorder)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    selectedStyles.forEachIndexed { index, style ->
                        val isSelected = activeSlotIndex == index
                        val accent = slotAccentColors.getOrElse(index) { HairLookPurpleGradientEnd }
                        SlotTabPill(
                            label = "Style ${index + 1}: ${style.name.take(10)}",
                            isSelected = isSelected,
                            accentColor = accent,
                            onClick = { activeSlotIndex = index }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    // Add 3rd Style Pill (if currently only 2 styles)
                    if (selectedStyles.size < 3) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.Transparent,
                            border = BorderStroke(1.dp, ChampagneGold.copy(alpha = 0.6f)),
                            modifier = Modifier.clickable {
                                // Add 3rd style from repository
                                val nextStyle = allStyles.firstOrNull { candidate ->
                                    selectedStyles.none { it.id == candidate.id } &&
                                            (selectedGenderFilter == null || candidate.gender == selectedGenderFilter)
                                } ?: allStyles.first()
                                selectedStyles = selectedStyles + nextStyle
                                activeSlotIndex = 2
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add 3rd Style",
                                    tint = ChampagneGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "3rd Style",
                                    color = ChampagneGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Scrollable Body: Comparison Cards + Full Attributes Comparison Table + Style Replacer
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            // 1. Side-by-Side Cards (Responsive: 2 equal columns or 3 columns in horizontal scroll)
            if (selectedStyles.size == 2) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    selectedStyles.forEachIndexed { index, style ->
                        ComparisonCard(
                            style = style,
                            slotNumber = index + 1,
                            isActive = activeSlotIndex == index,
                            canRemove = false,
                            accentColor = slotAccentColors.getOrElse(index) { HairLookPurpleGradientEnd },
                            onTry = { onTryStyle(style) },
                            onViewDetails = { onSelectHairstyle(style.id) },
                            onClickCard = { activeSlotIndex = index },
                            onRemove = {},
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // 3 Styles side-by-side
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    selectedStyles.forEachIndexed { index, style ->
                        ComparisonCard(
                            style = style,
                            slotNumber = index + 1,
                            isActive = activeSlotIndex == index,
                            canRemove = selectedStyles.size > 2,
                            accentColor = slotAccentColors.getOrElse(index) { HairLookPurpleGradientEnd },
                            onTry = { onTryStyle(style) },
                            onViewDetails = { onSelectHairstyle(style.id) },
                            onClickCard = { activeSlotIndex = index },
                            onRemove = {
                                if (selectedStyles.size > 2) {
                                    val updated = selectedStyles.filterIndexed { i, _ -> i != index }
                                    selectedStyles = updated
                                    if (activeSlotIndex >= updated.size) {
                                        activeSlotIndex = updated.size - 1
                                    }
                                }
                            },
                            modifier = Modifier.width(175.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Comprehensive Side-by-Side Attributes Comparison Table
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, HairLookCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Detailed Comparison Matrix",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Contrasting length, texture, upkeep & face shapes",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Row: Category
                    ComparisonTableRow(
                        title = "Category",
                        icon = Icons.Default.Info,
                        values = selectedStyles.map { it.category }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row: Hair Length
                    ComparisonTableRow(
                        title = "Hair Length",
                        icon = Icons.Default.ContentCut,
                        values = selectedStyles.map { it.hairLength }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row: Texture
                    ComparisonTableRow(
                        title = "Texture",
                        icon = Icons.Default.Texture,
                        values = selectedStyles.map { it.hairTexture }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row: Maintenance
                    ComparisonTableRow(
                        title = "Maintenance",
                        icon = Icons.Default.Speed,
                        values = selectedStyles.map { it.maintenance }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row: Styling Difficulty
                    ComparisonTableRow(
                        title = "Difficulty",
                        icon = Icons.Default.Star,
                        values = selectedStyles.map { it.stylingDifficulty }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Row: Face Shapes
                    ComparisonTableFaceShapesRow(
                        selectedStyles = selectedStyles
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Bottom Carousel Header: Replace Active Slot
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Replace Style ${activeSlotIndex + 1}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tap any haircut below to load it into Slot ${activeSlotIndex + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                // Gender Filter Chips: [ All ] [ Men ] [ Women ]
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    GenderFilterChip(
                        label = "All",
                        isSelected = selectedGenderFilter == null,
                        color = HairLookPurpleGradientEnd,
                        onClick = { selectedGenderFilter = null }
                    )
                    GenderFilterChip(
                        label = "Men",
                        isSelected = selectedGenderFilter == Gender.MEN,
                        color = HairLookBlueAccent,
                        onClick = { selectedGenderFilter = Gender.MEN }
                    )
                    GenderFilterChip(
                        label = "Women",
                        isSelected = selectedGenderFilter == Gender.WOMEN,
                        color = HairLookPinkAccent,
                        onClick = { selectedGenderFilter = Gender.WOMEN }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal Carousel of selectable styles
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredList, key = { it.id }) { item ->
                    val isCurrentlySelected = selectedStyles.any { it.id == item.id }

                    Card(
                        modifier = Modifier
                            .width(115.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(
                                width = if (isCurrentlySelected) 2.dp else 1.dp,
                                color = if (isCurrentlySelected) ChampagneGold else HairLookCardBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                // Replace style in active slot
                                val current = selectedStyles.toMutableList()
                                if (activeSlotIndex < current.size) {
                                    current[activeSlotIndex] = item
                                } else {
                                    current.add(item)
                                }
                                selectedStyles = current
                            },
                        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Box {
                                Image(
                                    painter = painterResource(id = item.imageRes),
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(95.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                if (isCurrentlySelected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = ChampagneGold,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(20.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.Black,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
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
                                color = TextMuted,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Individual Card in the side-by-side comparison view
 */
@Composable
private fun ComparisonCard(
    style: HairstyleItem,
    slotNumber: Int,
    isActive: Boolean,
    canRemove: Boolean,
    accentColor: Color,
    onTry: () -> Unit,
    onViewDetails: () -> Unit,
    onClickCard: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) accentColor else HairLookCardBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClickCard() },
        colors = CardDefaults.cardColors(containerColor = HairLookCardBg)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header with Slot Badge and Remove (✕) Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.20f),
                    border = BorderStroke(1.dp, accentColor)
                ) {
                    Text(
                        text = "Style $slotNumber",
                        color = accentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (canRemove) {
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Style",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Hairstyle Image Preview
            Image(
                painter = painterResource(id = style.imageRes),
                contentDescription = style.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = style.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1
            )

            Text(
                text = "${style.category} • ${style.gender.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = accentColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action 1: "Try This Style"
            Button(
                onClick = onTry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor)
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Try Look",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action 2: "View Details"
            OutlinedButton(
                onClick = onViewDetails,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, HairLookCardBorder)
            ) {
                Text(
                    text = "Details",
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

/**
 * Comparative row in the attributes matrix
 */
@Composable
private fun ComparisonTableRow(
    title: String,
    icon: ImageVector,
    values: List<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HairLookDarkBg)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = HairLookPurpleGradientEnd,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            values.forEachIndexed { idx, value ->
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Suitable Face Shapes comparison row
 */
@Composable
private fun ComparisonTableFaceShapesRow(
    selectedStyles: List<HairstyleItem>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HairLookDarkBg)
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                tint = HairLookPurpleGradientEnd,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Suitable Face Shapes",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            selectedStyles.forEach { style ->
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = style.faceShapes.joinToString(", "),
                        style = MaterialTheme.typography.labelSmall,
                        color = HairLookBlueAccent,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SlotTabPill(
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) accentColor else Color.Transparent,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.Black else TextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun GenderFilterChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) color.copy(alpha = 0.25f) else Color.Transparent,
        border = BorderStroke(1.dp, if (isSelected) color else HairLookCardBorder),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) color else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
