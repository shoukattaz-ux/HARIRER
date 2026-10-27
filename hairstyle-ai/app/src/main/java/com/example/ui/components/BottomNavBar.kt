package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.AppScreen
import com.example.ui.theme.HairLookCardBg
import com.example.ui.theme.HairLookCardBorder
import com.example.ui.theme.HairLookPurple
import com.example.ui.theme.HairLookPurpleGradientEnd
import com.example.ui.theme.HairLookPurpleGradientStart
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

sealed class NavItem(
    val screen: AppScreen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Home : NavItem(
        screen = AppScreen.Home,
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "nav_home"
    )

    object Explore : NavItem(
        screen = AppScreen.SearchExplore,
        title = "Explore",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
        testTag = "nav_explore"
    )

    object Favorites : NavItem(
        screen = AppScreen.Favorites,
        title = "Favorites",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        testTag = "nav_favorites"
    )

    object Profile : NavItem(
        screen = AppScreen.Profile,
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "nav_profile"
    )
}

/**
 * 4-Tab Bottom Navigation Bar matching the Reference Image:
 * [ Home ] [ Explore ] [ Favorites ] [ Profile ]
 */
@Composable
fun AppBottomNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    favoritesCount: Int = 0,
    isPaidVersion: Boolean = false,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem.Home,
        NavItem.Explore,
        NavItem.Favorites,
        NavItem.Profile
    )

    NavigationBar(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .border(
                width = 1.dp,
                color = HairLookCardBorder,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
            .windowInsetsPadding(WindowInsets.navigationBars),
        containerColor = HairLookCardBg,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = when (item) {
                NavItem.Home -> currentScreen is AppScreen.Home
                NavItem.Explore -> currentScreen is AppScreen.SearchExplore ||
                        currentScreen is AppScreen.ChooseStyle ||
                        currentScreen is AppScreen.MenGallery ||
                        currentScreen is AppScreen.WomenGallery
                NavItem.Favorites -> currentScreen is AppScreen.Favorites
                NavItem.Profile -> currentScreen is AppScreen.Profile
            }

            NavigationBarItem(
                modifier = Modifier.testTag(item.testTag),
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    if (item == NavItem.Favorites && favoritesCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = HairLookPurpleGradientEnd,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = favoritesCount.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = HairLookPurpleGradientEnd,
                    indicatorColor = HairLookPurple.copy(alpha = 0.35f),
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}
