package com.hypercapsule.app.ui.designsystem.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * HyperCapsule Semantic Color Palette
 * Designed for "Red AMOLED × Liquid Glass × Premium Android Experience"
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
    val softRed: Color,
    val deepRedTint: Color,
    val secondaryCyan: Color,
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
    background = Color(0xFF080809),
    surfaceBase = Color(0xFF141419),
    surfaceCard = Color(0xFF141419),
    surfaceCardGlass = Color(0xF0141419),
    surfaceElevated = Color(0xFF1B1B22),
    surfaceElevatedGlass = Color(0xF21B1B22),
    borderHighlight = Color(0xFF34343D),
    borderSubtle = Color(0xFF22222A),
    primary = Color(0xFFFF454F), // Primary Red
    primaryVariant = Color(0xFFE85D68), // Soft Red
    secondary = Color(0xFF35C8F5), // Secondary Cyan
    tertiary = Color(0xFF35191E), // Deep Red Tint
    softRed = Color(0xFFE85D68),
    deepRedTint = Color(0xFF35191E),
    secondaryCyan = Color(0xFF35C8F5),
    success = Color(0xFF30D158),
    warning = Color(0xFFFFD60A),
    error = Color(0xFFFF454F),
    textPrimary = Color(0xFFF5F5F7),
    textSecondary = Color(0xFFA4A4B0),
    textTertiary = Color(0xFF6C6C7A),
    accentGradient = Brush.horizontalGradient(
        listOf(Color(0xFFFF454F), Color(0xFFE85D68), Color(0xFF35C8F5))
    ),
    cardGlowBrush = Brush.verticalGradient(
        listOf(Color(0x18FF454F), Color(0x00000000))
    )
)

val LightHyperCapsuleColors = HyperCapsuleColors(
    isDark = false,
    background = Color(0xFFF7F7FA),
    surfaceBase = Color(0xFFFFFFFF),
    surfaceCard = Color(0xFFF0F0F5),
    surfaceCardGlass = Color(0xF5FFFFFF),
    surfaceElevated = Color(0xFFE6E6ED),
    surfaceElevatedGlass = Color(0xFAFFFFFF),
    borderHighlight = Color(0xFFD6D6E0),
    borderSubtle = Color(0xFFE8E8F0),
    primary = Color(0xFFE53945),
    primaryVariant = Color(0xFFD32F2F),
    secondary = Color(0xFF0097A7),
    tertiary = Color(0xFFFFEBEE),
    softRed = Color(0xFFE57373),
    deepRedTint = Color(0xFFFFEBEE),
    secondaryCyan = Color(0xFF0097A7),
    success = Color(0xFF28A745),
    warning = Color(0xFFFF9500),
    error = Color(0xFFE53945),
    textPrimary = Color(0xFF16161C),
    textSecondary = Color(0xFF5D5D6E),
    textTertiary = Color(0xFF8E8E9F),
    accentGradient = Brush.horizontalGradient(
        listOf(Color(0xFFE53945), Color(0xFF0097A7))
    ),
    cardGlowBrush = Brush.verticalGradient(
        listOf(Color(0x10E53945), Color(0x00000000))
    )
)

val LocalHyperCapsuleColors = staticCompositionLocalOf { DarkHyperCapsuleColors }
