package com.hypercapsule.app.ui.designsystem.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * HyperCapsule Semantic Color Palette
 * Designed for "Liquid Glass × Material 3 Expressive × HyperOS Native × AMOLED Premium"
 */
@Immutable
data class HyperCapsuleColors(
    val isDark: Boolean,
    val background: Color,
    val surfaceBase: Color,
    val surfaceCard: Color,
    val surfaceCardGlass: Color,
    val surfaceElevated: Color,
    val surfaceElevatedGlass: Color,
    val borderHighlight: Color,
    val borderSubtle: Color,
    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,
    val tertiary: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accentGradient: Brush,
    val cardGlowBrush: Brush
) {
    val accentPrimary: Color get() = primary
    val accentSecondary: Color get() = secondary
    val accentSuccess: Color get() = success
    val accentWarning: Color get() = warning
    val accentError: Color get() = error
    val cardSurface: Color get() = surfaceCard
    val cardBorder: Color get() = borderHighlight
    val backgroundElevated: Color get() = surfaceElevated
}

val DarkHyperCapsuleColors = HyperCapsuleColors(
    isDark = true,
    background = Color(0xFF07080A),
    surfaceBase = Color(0xFF10131A),
    surfaceCard = Color(0xFF161A24),
    surfaceCardGlass = Color(0xCC161A24),
    surfaceElevated = Color(0xFF1E2330),
    surfaceElevatedGlass = Color(0xD91E2330),
    borderHighlight = Color(0x24FFFFFF),
    borderSubtle = Color(0x10FFFFFF),
    primary = Color(0xFF00C7FF),
    primaryVariant = Color(0xFF0A84FF),
    secondary = Color(0xFF9D4EDD),
    tertiary = Color(0xFFFF7043),
    success = Color(0xFF30D158),
    warning = Color(0xFFFFD60A),
    error = Color(0xFFFF453A),
    textPrimary = Color(0xFFF5F6F8),
    textSecondary = Color(0xFFA0A5B5),
    textTertiary = Color(0xFF6B7280),
    accentGradient = Brush.horizontalGradient(
        listOf(Color(0xFF00C7FF), Color(0xFF0A84FF), Color(0xFF9D4EDD))
    ),
    cardGlowBrush = Brush.verticalGradient(
        listOf(Color(0x1A00C7FF), Color(0x00000000))
    )
)

val LightHyperCapsuleColors = HyperCapsuleColors(
    isDark = false,
    background = Color(0xFFF6F8FC),
    surfaceBase = Color(0xFFFFFFFF),
    surfaceCard = Color(0xFFEFF2F7),
    surfaceCardGlass = Color(0xEEFFFFFF),
    surfaceElevated = Color(0xFFE4E8F0),
    surfaceElevatedGlass = Color(0xF5FFFFFF),
    borderHighlight = Color(0x18000000),
    borderSubtle = Color(0x0C000000),
    primary = Color(0xFF007AFF),
    primaryVariant = Color(0xFF0056B3),
    secondary = Color(0xFF7B2CBF),
    tertiary = Color(0xFFE65100),
    success = Color(0xFF28A745),
    warning = Color(0xFFFF9500),
    error = Color(0xFFFF3B30),
    textPrimary = Color(0xFF1A1D24),
    textSecondary = Color(0xFF5F6573),
    textTertiary = Color(0xFF8F95A3),
    accentGradient = Brush.horizontalGradient(
        listOf(Color(0xFF007AFF), Color(0xFF5E5CE6))
    ),
    cardGlowBrush = Brush.verticalGradient(
        listOf(Color(0x14007AFF), Color(0x00000000))
    )
)

val LocalHyperCapsuleColors = staticCompositionLocalOf { DarkHyperCapsuleColors }
