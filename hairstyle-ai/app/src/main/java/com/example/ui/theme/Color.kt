package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

// Base Dark Palette (Luxury Studio Obsidian)
val DarkObsidianBackground = Color(0xFF0D0E15)
val DarkSurfaceDark = Color(0xFF151722)
val DarkSurfaceCard = Color(0xFF1D2030)
val DarkSurfaceCardElevated = Color(0xFF262A3E)
val DarkCardBorderStroke = Color(0xFF2D3249)
val DarkTextPrimary = Color(0xFFF8F9FA)
val DarkTextSecondary = Color(0xFF9CA3AF)
val DarkTextMuted = Color(0xFF6B7280)

// Base Light Palette (Crisp, Elegant Light Studio)
val LightCanvasBackground = Color(0xFFF6F8FB)
val LightSurfaceDark = Color(0xFFFFFFFF)
val LightSurfaceCard = Color(0xFFFFFFFF)
val LightSurfaceCardElevated = Color(0xFFEFF2F7)
val LightCardBorderStroke = Color(0xFFE2E6EE)
val LightTextPrimary = Color(0xFF111827)
val LightTextSecondary = Color(0xFF4B5563)
val LightTextMuted = Color(0xFF9CA3AF)

// CompositionLocal tracking active theme
val LocalThemeIsDark = compositionLocalOf { true }

// Dynamic theme-aware color properties that automatically switch across the entire app
val ObsidianBackground: Color
    @Composable
    get() = if (LocalThemeIsDark.current) DarkObsidianBackground else LightCanvasBackground

val SurfaceDark: Color
    @Composable
    get() = if (LocalThemeIsDark.current) DarkSurfaceDark else LightSurfaceDark

val SurfaceCard: Color
    @Composable
    get() = if (LocalThemeIsDark.current) DarkSurfaceCard else LightSurfaceCard

val SurfaceCardElevated: Color
    @Composable
    get() = if (LocalThemeIsDark.current) DarkSurfaceCardElevated else LightSurfaceCardElevated

val CardBorderStroke: Color
    @Composable
    get() = if (LocalThemeIsDark.current) DarkCardBorderStroke else LightCardBorderStroke

val TextPrimary: Color
    @Composable
    get() = if (LocalThemeIsDark.current) DarkTextPrimary else LightTextPrimary

val TextSecondary: Color
    @Composable
    get() = if (LocalThemeIsDark.current) DarkTextSecondary else LightTextSecondary

val TextMuted: Color
    @Composable
    get() = if (LocalThemeIsDark.current) DarkTextMuted else LightTextMuted

// Accent Colors
val ChampagneGold = Color(0xFFE5B842)
val ChampagneGoldDark = Color(0xFFB8860B)
val ChampagneGoldLight = Color(0xFFF9D87C)
val ElectricViolet = Color(0xFFA855F7)
val ElectricVioletSoft = Color(0xFFC084FC)
val NeonCyan = Color(0xFF00F5D4)
val SunsetRose = Color(0xFFF43F5E)

// Semantic
val SuccessGreen = Color(0xFF10B981)
val GoldStar = Color(0xFFFBBF24)

// HairLook Brand Colors (matching UI Reference Image)
val HairLookPurple = Color(0xFF6C5CE7)
val HairLookPurpleGradientStart = Color(0xFF5D5FEF)
val HairLookPurpleGradientEnd = Color(0xFF8C7CFF)
val HairLookBlueAccent = Color(0xFF38BDF8)
val HairLookPinkAccent = Color(0xFFEC4899)
val HairLookCardBg: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF131627) else Color(0xFFFFFFFF)

val HairLookCardBorder: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF262B45) else Color(0xFFE2E6EE)

val HairLookDarkBg: Color
    @Composable
    get() = if (LocalThemeIsDark.current) Color(0xFF0C0E17) else LightCanvasBackground

