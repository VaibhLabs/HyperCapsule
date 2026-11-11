package com.hypercapsule.app.ui.designsystem.spacing

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class HyperCapsuleSpacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val default: Dp = 16.dp,
    val large: Dp = 20.dp,
    val extraLarge: Dp = 24.dp,
    val huge: Dp = 32.dp,
    val massive: Dp = 48.dp
) {
    val screenPadding: Dp get() = default
    val cardPadding: Dp get() = default
    val itemSpacing: Dp get() = medium
    val sectionSpacing: Dp get() = extraLarge
}

val DefaultHyperCapsuleSpacing = HyperCapsuleSpacing()

val LocalHyperCapsuleSpacing = staticCompositionLocalOf { DefaultHyperCapsuleSpacing }
