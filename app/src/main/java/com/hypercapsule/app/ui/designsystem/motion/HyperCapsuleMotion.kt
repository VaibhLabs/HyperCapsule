package com.hypercapsule.app.ui.designsystem.motion

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

object HyperCapsuleMotion {
    val TactileSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.78f,
        stiffness = 400f
    )

    val FluidSpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.85f,
        stiffness = 300f
    )

    val BouncySpring: AnimationSpec<Float> = spring(
        dampingRatio = 0.65f,
        stiffness = 500f
    )

    const val DurationFast = 200
    const val DurationMedium = 350
    const val DurationSlow = 500
}

/**
 * Modifier that applies HyperOS-style tactile compression feedback on touch.
 */
fun Modifier.tactilePress(
    pressedScale: Float = 0.97f,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    if (!enabled) return@composed this

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1.0f,
        animationSpec = HyperCapsuleMotion.TactileSpring,
        label = "tactilePressScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    }
                )
            } else Modifier
        )
}
