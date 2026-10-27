package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OutfitAudience
import com.example.ui.theme.HairLookBlueAccent
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person

enum class ClothesTab {
    HOME,
    MEN,
    WOMEN,
    BABY,
    SAVED,
    PROFILE
}

data class ClothesNavItem(
    val tab: ClothesTab,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

/**
 * Dedicated Bottom Navigation Bar for Clothes & Outfit Studio.
 * Offers: [ Home ] [ Men ] [ Women ] [ Baby ] [ Saved ]
 * (Profile omitted as requested by the user)
 */
@Composable
fun ClothesBottomNavBar(
    selectedTab: ClothesTab,
    onTabSelected: (ClothesTab) -> Unit,
    savedCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        ClothesNavItem(
            tab = ClothesTab.HOME,
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            testTag = "clothes_nav_home"
        ),
        ClothesNavItem(
            tab = ClothesTab.MEN,
            title = "Men",
            selectedIcon = Icons.Filled.Male,
            unselectedIcon = Icons.Outlined.Male,
            testTag = "clothes_nav_men"
        ),
        ClothesNavItem(
            tab = ClothesTab.WOMEN,
            title = "Women",
            selectedIcon = Icons.Filled.Female,
            unselectedIcon = Icons.Outlined.Female,
            testTag = "clothes_nav_women"
        ),
        ClothesNavItem(
            tab = ClothesTab.BABY,
            title = "Baby",
            selectedIcon = Icons.Filled.ChildCare,
            unselectedIcon = Icons.Outlined.ChildCare,
            testTag = "clothes_nav_baby"
        ),
        ClothesNavItem(
            tab = ClothesTab.SAVED,
            title = "Saved",
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.FavoriteBorder,
            testTag = "clothes_nav_saved"
        ),
        ClothesNavItem(
            tab = ClothesTab.PROFILE,
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            testTag = "clothes_nav_profile"
        )
    )

    Box(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(HairLookCardBg.copy(alpha = 0.95f))
            .border(1.dp, HairLookCardBorder, RoundedCornerShape(26.dp))
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            items.forEach { item ->
                val isSelected = selectedTab == item.tab
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(item.tab) },
                    icon = {
                        if (item.tab == ClothesTab.SAVED && savedCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = HairLookBlueAccent,
                                        contentColor = Color.Black
                                    ) {
                                        Text(text = savedCount.toString(), fontSize = 10.sp)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = HairLookBlueAccent,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = HairLookBlueAccent
                    ),
                    modifier = Modifier.testTag(item.testTag)
                )
            }
        }
    }
}
