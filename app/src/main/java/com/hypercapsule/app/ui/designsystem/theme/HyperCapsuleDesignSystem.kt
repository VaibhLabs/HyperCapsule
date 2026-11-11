package com.hypercapsule.app.ui.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.hypercapsule.app.ui.designsystem.color.DarkHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.color.HyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.color.LightHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.shapes.DefaultHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.shapes.HyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.DefaultHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.spacing.HyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.DefaultHyperCapsuleTypography
import com.hypercapsule.app.ui.designsystem.typography.HyperCapsuleTypography
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

val LocalHyperCapsuleColors = com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
val LocalHyperCapsuleShapes = com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
val LocalHyperCapsuleSpacing = com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
val LocalHyperCapsuleTypography = com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

object HyperCapsuleDesignSystem {
    val colors: HyperCapsuleColors
        @Composable
        @ReadOnlyComposable
        get() = LocalHyperCapsuleColors.current

    val shapes: HyperCapsuleShapes
        @Composable
        @ReadOnlyComposable
        get() = LocalHyperCapsuleShapes.current

    val spacing: HyperCapsuleSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalHyperCapsuleSpacing.current

    val typography: HyperCapsuleTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalHyperCapsuleTypography.current
}

/**
 * Core design system provider. Provides both custom tokens and standard Material 3 colorScheme / typography.
 */
@Composable
fun HyperCapsuleDesignSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkHyperCapsuleColors else LightHyperCapsuleColors

    val m3ColorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colors.primary,
            secondary = colors.secondary,
            tertiary = colors.tertiary,
            background = colors.background,
            surface = colors.surfaceCard,
            surfaceVariant = colors.surfaceElevated,
            onPrimary = androidx.compose.ui.graphics.Color.Black,
            onSecondary = androidx.compose.ui.graphics.Color.White,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            error = colors.error
        )
    } else {
        lightColorScheme(
            primary = colors.primary,
            secondary = colors.secondary,
            tertiary = colors.tertiary,
            background = colors.background,
            surface = colors.surfaceCard,
            surfaceVariant = colors.surfaceElevated,
            onPrimary = androidx.compose.ui.graphics.Color.White,
            onSecondary = androidx.compose.ui.graphics.Color.White,
            onBackground = colors.textPrimary,
            onSurface = colors.textPrimary,
            error = colors.error
        )
    }

    CompositionLocalProvider(
        LocalHyperCapsuleColors provides colors,
        LocalHyperCapsuleShapes provides DefaultHyperCapsuleShapes,
        LocalHyperCapsuleSpacing provides DefaultHyperCapsuleSpacing,
        LocalHyperCapsuleTypography provides DefaultHyperCapsuleTypography
    ) {
        MaterialTheme(
            colorScheme = m3ColorScheme,
            typography = com.hypercapsule.app.ui.theme.Typography,
            content = content
        )
    }
}
