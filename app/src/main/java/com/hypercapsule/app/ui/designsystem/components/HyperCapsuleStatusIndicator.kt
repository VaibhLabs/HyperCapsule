package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

enum class HyperCapsuleStatusType {
    ACTIVE,
    STANDBY,
    WARNING,
    ERROR
}

/**
 * Pulsing status indicator pill for live services and system health.
 */
@Composable
fun HyperCapsuleStatusIndicator(
    text: String,
    statusType: HyperCapsuleStatusType,
    modifier: Modifier = Modifier,
    pulse: Boolean = true
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val statusColor = when (statusType) {
        HyperCapsuleStatusType.ACTIVE -> colors.success
        HyperCapsuleStatusType.STANDBY -> colors.primary
        HyperCapsuleStatusType.WARNING -> colors.warning
        HyperCapsuleStatusType.ERROR -> colors.error
    }

    val infiniteTransition = rememberInfiniteTransition(label = "statusPulse")
    val pulseScale by if (pulse) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.6f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1.0f) }
    }

    Box(
        modifier = modifier
            .clip(shapes.pill)
            .background(statusColor.copy(alpha = 0.12f))
            .border(1.dp, statusColor.copy(alpha = 0.25f), shapes.pill)
            .padding(horizontal = spacing.medium, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.size(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (pulse) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.35f))
                    )
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }

            Spacer(modifier = Modifier.width(spacing.small))

            Text(
                text = text,
                style = typography.labelMedium,
                color = statusColor
            )
        }
    }
}
