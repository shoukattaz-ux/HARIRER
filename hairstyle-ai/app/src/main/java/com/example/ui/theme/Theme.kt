package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = DarkObsidianBackground,
    primaryContainer = DarkSurfaceCardElevated,
    onPrimaryContainer = ChampagneGoldLight,
    secondary = ElectricViolet,
    onSecondary = DarkObsidianBackground,
    secondaryContainer = DarkSurfaceCardElevated,
    onSecondaryContainer = ElectricVioletSoft,
    tertiary = NeonCyan,
    onTertiary = DarkObsidianBackground,
    background = DarkObsidianBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurfaceDark,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkCardBorderStroke,
    outlineVariant = DarkSurfaceCardElevated
)

private val LightColorScheme = lightColorScheme(
    primary = ChampagneGoldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFDF8EE),
    onPrimaryContainer = Color(0xFF5B4300),
    secondary = ElectricViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5EEFD),
    onSecondaryContainer = Color(0xFF4A154B),
    tertiary = Color(0xFF0D9488),
    onTertiary = Color.White,
    background = LightCanvasBackground,
    onBackground = LightTextPrimary,
    surface = LightSurfaceDark,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceCardElevated,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorderStroke,
    outlineVariant = Color(0xFFE5E7EB)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val barBg = if (darkTheme) DarkObsidianBackground else LightCanvasBackground
                window.statusBarColor = barBg.toArgb()
                window.navigationBarColor = barBg.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !darkTheme
                controller.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalThemeIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

