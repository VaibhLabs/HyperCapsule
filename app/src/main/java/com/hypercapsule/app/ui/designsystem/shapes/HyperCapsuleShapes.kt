package com.hypercapsule.app.ui.designsystem.shapes

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class HyperCapsuleShapes(
    val extraSmall: Shape = RoundedCornerShape(8.dp),
    val small: Shape = RoundedCornerShape(12.dp),
    val medium: Shape = RoundedCornerShape(18.dp),
    val large: Shape = RoundedCornerShape(24.dp),
    val extraLarge: Shape = RoundedCornerShape(32.dp),
    val pill: Shape = CircleShape
) {
    val squircleSmall: Shape get() = small
    val squircleMedium: Shape get() = medium
    val squircleLarge: Shape get() = large
    val squircleXLarge: Shape get() = extraLarge
}

val DefaultHyperCapsuleShapes = HyperCapsuleShapes()

val LocalHyperCapsuleShapes = staticCompositionLocalOf { DefaultHyperCapsuleShapes }
