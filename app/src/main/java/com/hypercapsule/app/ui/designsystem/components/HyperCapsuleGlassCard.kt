package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing

/**
 * Premium Liquid Glass Card
 * Features squircle geometry, subtle specular highlight border, and tactile micro-press.
 */
@Composable
fun HyperCapsuleGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = LocalHyperCapsuleShapes.current.large,
    backgroundColor: Color? = null,
    tint: Color? = backgroundColor,
    borderColor: Color? = null,
    borderWidth: Dp = 1.dp,
    contentPadding: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val surfaceColor = tint ?: colors.surfaceCardGlass
    val outlineColor = borderColor ?: colors.borderHighlight

    val cardModifier = modifier
        .fillMaxWidth()
        .clip(shape)
        .then(
            if (onClick != null) Modifier.tactilePress(pressedScale = 0.98f, onClick = onClick)
            else Modifier
        )
        .background(surfaceColor, shape)
        .border(
            border = BorderStroke(borderWidth, outlineColor),
            shape = shape
        )
        .padding(contentPadding)

    Box(
        modifier = cardModifier,
        content = content
    )
}

/**
 * Elevated variant for prominent cards (hero carousel, active status cards).
 */
@Composable
fun HyperCapsuleElevatedCard(
    modifier: Modifier = Modifier,
    shape: Shape = LocalHyperCapsuleShapes.current.large,
    borderColor: Color? = null,
    contentPadding: Dp = LocalHyperCapsuleSpacing.current.default,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    HyperCapsuleGlassCard(
        modifier = modifier,
        shape = shape,
        backgroundColor = colors.surfaceElevatedGlass,
        borderColor = borderColor ?: colors.borderHighlight,
        contentPadding = contentPadding,
        onClick = onClick,
        content = content
    )
}
