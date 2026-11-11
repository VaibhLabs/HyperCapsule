package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.motion.HyperCapsuleMotion
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

enum class ButtonVariant {
    PRIMARY,
    SECONDARY,
    OUTLINE
}

/**
 * HyperOS-styled fluid toggle switch.
 */
@Composable
fun HyperCapsuleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val colors = LocalHyperCapsuleColors.current
    val haptic = LocalHapticFeedback.current

    val trackColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.surfaceElevated.copy(alpha = 0.5f)
            checked -> colors.primary
            else -> colors.surfaceElevated
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "switchTrackColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> colors.borderSubtle
            checked -> colors.primary.copy(alpha = 0.6f)
            else -> colors.borderHighlight
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "switchBorderColor"
    )

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 22.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "switchThumbOffset"
    )

    val thumbColor by animateColorAsState(
        targetValue = if (checked) Color.White else Color(0xFFE4E7EE),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "switchThumbColor"
    )

    Box(
        modifier = modifier
            .size(width = 48.dp, height = 28.dp)
            .clip(CircleShape)
            .background(trackColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(24.dp)
                .clip(CircleShape)
                .background(thumbColor)
        )
    }
}

/**
 * HyperCapsule Slider with value preview and tactile response.
 */
@Composable
fun HyperCapsuleSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    title: String? = null,
    displayValue: String? = null,
    enabled: Boolean = true
) {
    val colors = LocalHyperCapsuleColors.current
    val typography = LocalHyperCapsuleTypography.current
    val spacing = LocalHyperCapsuleSpacing.current

    Column(modifier = modifier.fillMaxWidth()) {
        if (title != null || displayValue != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.extraSmall, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (title != null) {
                    Text(
                        text = title,
                        style = typography.titleSmall,
                        color = colors.textPrimary
                    )
                }
                if (displayValue != null) {
                    Text(
                        text = displayValue,
                        style = typography.labelMedium,
                        color = colors.primary
                    )
                }
            }
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = colors.primary,
                activeTrackColor = colors.primary,
                inactiveTrackColor = colors.surfaceElevated
            )
        )
    }
}

/**
 * Premium Action Button with tactile feedback.
 */
@Composable
fun HyperCapsuleActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    variant: ButtonVariant = ButtonVariant.PRIMARY,
    isPrimary: Boolean = (variant == ButtonVariant.PRIMARY),
    enabled: Boolean = true
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val containerColor = when {
        !enabled -> colors.surfaceElevated
        variant == ButtonVariant.PRIMARY || isPrimary -> colors.primary
        variant == ButtonVariant.SECONDARY -> colors.surfaceElevatedGlass
        else -> Color.Transparent
    }
    val contentColor = when {
        !enabled -> colors.textTertiary
        variant == ButtonVariant.PRIMARY || isPrimary -> Color.Black
        else -> colors.textPrimary
    }
    val borderColor = when {
        !enabled -> colors.borderSubtle
        variant == ButtonVariant.PRIMARY || isPrimary -> colors.primary.copy(alpha = 0.4f)
        variant == ButtonVariant.OUTLINE -> colors.primary.copy(alpha = 0.5f)
        else -> colors.borderHighlight
    }

    Box(
        modifier = modifier
            .clip(shapes.large)
            .then(if (enabled) Modifier.tactilePress(onClick = onClick) else Modifier)
            .background(containerColor)
            .border(1.dp, borderColor, shapes.large)
            .padding(horizontal = spacing.large, vertical = spacing.medium),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) contentColor else colors.textTertiary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(spacing.small))
            }
            Text(
                text = text,
                style = typography.labelLarge,
                color = if (enabled) contentColor else colors.textTertiary
            )
        }
    }
}

/**
 * Segmented Control for tabs or switching modes.
 */
@Composable
fun HyperCapsuleSegmentedControl(
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shapes.pill)
            .background(colors.surfaceElevatedGlass)
            .border(1.dp, colors.borderHighlight, shapes.pill)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) colors.primary else Color.Transparent,
                    label = "segmentedBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.Black else colors.textSecondary,
                    label = "segmentedText"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shapes.pill)
                        .background(bgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onItemSelected(index)
                            }
                        }
                        .padding(vertical = spacing.small),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        style = typography.labelMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}
