package com.hypercapsule.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Reply
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Layers
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Sensors
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypercapsule.app.R
import com.hypercapsule.app.models.IslandConfig
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSlider
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import com.hypercapsule.app.ui.theme.HyperCapsuleTheme

// Snap steps (in seconds) for auto-hide timeout
val timeoutSteps = listOf(
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 45,
    60, 300, 900, 1800, 3600
)

// Snap steps for floating heads-up timeout
private val timePopUpSteps = listOf(
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30
)

/**
 * Redesigned, Human-Crafted Island Behavior Settings Control.
 * Clear 3-tier visual hierarchy, live preview simulator, fluid spring animations,
 * zero redundant cards, and strict preservation of persistence and callbacks.
 */
@Composable
fun IslandSettingsControl(
    config: IslandConfig,
    defaultConfig: IslandConfig? = null,
    onUpdate: (IslandConfig) -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val isOverridden = config.isFloat != null
    val displayConfig = if (isOverridden) config else (defaultConfig ?: config)

    val currentTimeout = displayConfig.timeout ?: 10
    val isTimeoutEnabled = currentTimeout > 0

    val isFloatEnabled = displayConfig.isFloat ?: true
    val currentFloatTimeout = displayConfig.floatTimeout ?: 5
    val isShadeEnabled = displayConfig.isShowShade ?: false

    val removeOriginalOn = displayConfig.removeOriginalNotification == true
    val dismissWithOriginal = displayConfig.dismissWithOriginal ?: true
    val enableInlineReply = displayConfig.enableInlineReply ?: true

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.large)
    ) {
        // --- 1. LIVE BEHAVIOR PREVIEW SIMULATOR ---
        IslandBehaviorPreviewCard(
            isFloatEnabled = isFloatEnabled,
            isTimeoutEnabled = isTimeoutEnabled,
            timeoutSeconds = currentTimeout,
            enableInlineReply = enableInlineReply
        )

        // --- 2. DISPLAY & AUTO-DISMISSAL GROUP ---
        Column(modifier = Modifier.fillMaxWidth()) {
            HyperCapsuleSectionHeader(
                title = stringResource(R.string.global_behavior),
                icon = Icons.Rounded.Tune
            )

            HyperCapsuleGlassCard(
                shape = shapes.extraLarge,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(spacing.default)) {
                    // Master Auto-Hide Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BehaviorIconContainer(
                            icon = Icons.Rounded.AccessTime,
                            active = isTimeoutEnabled,
                            tint = colors.primary
                        )
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.auto_hide_island),
                                style = typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isTimeoutEnabled) {
                                    stringResource(R.string.hides_after_a_set_time)
                                } else {
                                    stringResource(R.string.behavior_hide_desc)
                                },
                                style = typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        HyperCapsuleSwitch(
                            checked = isTimeoutEnabled,
                            onCheckedChange = { enabled ->
                                val newTimeout = if (enabled) 5 else 0
                                val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                                onUpdate(config.copy(timeout = newTimeout, isFloat = currentIsFloat))
                            }
                        )
                    }

                    // Expandable Duration Slider
                    AnimatedVisibility(
                        visible = isTimeoutEnabled,
                        enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                        exit = shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = spacing.medium)) {
                            HorizontalDivider(
                                color = colors.borderSubtle,
                                modifier = Modifier.padding(bottom = spacing.small)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Auto-hide Duration",
                                    style = typography.labelMedium,
                                    color = colors.textSecondary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(shapes.pill)
                                        .background(colors.primary.copy(alpha = 0.14f))
                                        .border(1.dp, colors.primary.copy(alpha = 0.35f), shapes.pill)
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = formatSeconds(currentTimeout),
                                        style = typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.primary
                                    )
                                }
                            }

                            val currentIndex = timeoutSteps.indexOf(currentTimeout).coerceAtLeast(0).toFloat()

                            HyperCapsuleSlider(
                                value = currentIndex,
                                onValueChange = { index ->
                                    val selectedSeconds = timeoutSteps[index.toInt()]
                                    val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                                    onUpdate(config.copy(timeout = selectedSeconds, isFloat = currentIsFloat))
                                },
                                valueRange = 0f..(timeoutSteps.size - 1).toFloat(),
                                steps = timeoutSteps.size - 2,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Text(
                                text = stringResource(R.string.behavior_desc_hide_long),
                                style = typography.bodySmall,
                                color = colors.textTertiary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // --- 3. FLOATING POPUP (HEADS-UP) PRESENTATION GROUP ---
        Column(modifier = Modifier.fillMaxWidth()) {
            HyperCapsuleSectionHeader(
                title = stringResource(R.string.xiaomi_featured_notifications),
                icon = Icons.Rounded.NotificationsActive
            )

            HyperCapsuleGlassCard(
                shape = shapes.extraLarge,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(spacing.default)) {
                    // Floating Heads-Up Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BehaviorIconContainer(
                            icon = Icons.Rounded.Visibility,
                            active = isFloatEnabled,
                            tint = colors.secondary
                        )
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.setting_float),
                                style = typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.setting_float_desc),
                                style = typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        HyperCapsuleSwitch(
                            checked = isFloatEnabled,
                            onCheckedChange = { onUpdate(config.copy(isFloat = it)) }
                        )
                    }

                    // Expandable Floating Duration Slider
                    AnimatedVisibility(
                        visible = isFloatEnabled,
                        enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                        exit = shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
                    ) {
                        Column(modifier = Modifier.padding(top = spacing.medium)) {
                            HorizontalDivider(
                                color = colors.borderSubtle,
                                modifier = Modifier.padding(bottom = spacing.small)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.setting_float_timeout_desc),
                                    style = typography.labelMedium,
                                    color = colors.textSecondary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(shapes.pill)
                                        .background(colors.secondary.copy(alpha = 0.14f))
                                        .border(1.dp, colors.secondary.copy(alpha = 0.35f), shapes.pill)
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.seconds_suffix, currentFloatTimeout),
                                        style = typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.secondary
                                    )
                                }
                            }

                            val currentIndexPop = timePopUpSteps.indexOf(currentFloatTimeout).coerceAtLeast(1).toFloat()

                            HyperCapsuleSlider(
                                value = currentIndexPop,
                                onValueChange = { index ->
                                    val selectedSeconds = timePopUpSteps[index.toInt()]
                                    onUpdate(config.copy(floatTimeout = selectedSeconds))
                                },
                                valueRange = 0f..(timePopUpSteps.size - 1).toFloat(),
                                steps = timePopUpSteps.size - 2,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(
                        color = colors.borderSubtle,
                        modifier = Modifier.padding(vertical = spacing.medium)
                    )

                    // Show in Shade Toggle Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BehaviorIconContainer(
                            icon = Icons.Rounded.Layers,
                            active = isShadeEnabled,
                            tint = colors.tertiary
                        )
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.setting_shade),
                                style = typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.setting_shade_desc),
                                style = typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        HyperCapsuleSwitch(
                            checked = isShadeEnabled,
                            onCheckedChange = {
                                val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                                onUpdate(config.copy(isShowShade = it, isFloat = currentIsFloat))
                            }
                        )
                    }
                }
            }
        }

        // --- 4. NOTIFICATION INTERCEPTION & INLINE REPLY GROUP ---
        Column(modifier = Modifier.fillMaxWidth()) {
            HyperCapsuleSectionHeader(
                title = stringResource(R.string.notification_management),
                icon = Icons.Rounded.Sensors
            )

            HyperCapsuleGlassCard(
                shape = shapes.extraLarge,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(spacing.default)) {
                    // Suppress original notification
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BehaviorIconContainer(
                            icon = Icons.Rounded.DeleteSweep,
                            active = removeOriginalOn,
                            tint = colors.error
                        )
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.remove_original_notification),
                                style = typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.remove_original_notification_desc),
                                style = typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        HyperCapsuleSwitch(
                            checked = removeOriginalOn,
                            onCheckedChange = {
                                val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                                onUpdate(config.copy(removeOriginalNotification = it, isFloat = currentIsFloat))
                            }
                        )
                    }

                    // Warning Alert for Suppressed Notification
                    AnimatedVisibility(
                        visible = removeOriginalOn,
                        enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                        exit = shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = spacing.small)
                                .clip(shapes.medium)
                                .background(colors.warning.copy(alpha = 0.12f))
                                .border(1.dp, colors.warning.copy(alpha = 0.35f), shapes.medium)
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Info,
                                    contentDescription = null,
                                    tint = colors.warning,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.remove_original_notification_hidden_warning),
                                    style = typography.bodySmall,
                                    color = colors.warning,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    HorizontalDivider(
                        color = colors.borderSubtle,
                        modifier = Modifier.padding(vertical = spacing.medium)
                    )

                    // Dismiss with original
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BehaviorIconContainer(
                            icon = Icons.Rounded.DeleteSweep,
                            active = dismissWithOriginal && !removeOriginalOn,
                            tint = colors.primary
                        )
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.dismiss_with_original),
                                style = typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (!removeOriginalOn) colors.textPrimary else colors.textTertiary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.dismiss_with_original_desc),
                                style = typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        HyperCapsuleSwitch(
                            checked = dismissWithOriginal,
                            enabled = !removeOriginalOn,
                            onCheckedChange = {
                                val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                                onUpdate(config.copy(dismissWithOriginal = it, isFloat = currentIsFloat))
                            }
                        )
                    }

                    HorizontalDivider(
                        color = colors.borderSubtle,
                        modifier = Modifier.padding(vertical = spacing.medium)
                    )

                    // Enable inline quick reply
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BehaviorIconContainer(
                            icon = Icons.AutoMirrored.Rounded.Reply,
                            active = enableInlineReply && !removeOriginalOn,
                            tint = colors.success
                        )
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.enable_inline_reply),
                                style = typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (!removeOriginalOn) colors.textPrimary else colors.textTertiary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.enable_inline_reply_desc),
                                style = typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                        HyperCapsuleSwitch(
                            checked = enableInlineReply,
                            enabled = !removeOriginalOn,
                            onCheckedChange = {
                                val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                                onUpdate(config.copy(enableInlineReply = it, isFloat = currentIsFloat))
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Live Island Behavior Preview Card showing dynamic pill simulation.
 */
@Composable
private fun IslandBehaviorPreviewCard(
    isFloatEnabled: Boolean,
    isTimeoutEnabled: Boolean,
    timeoutSeconds: Int,
    enableInlineReply: Boolean
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val typography = LocalHyperCapsuleTypography.current
    val spacing = LocalHyperCapsuleSpacing.current

    HyperCapsuleGlassCard(
        shape = shapes.extraLarge,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.default),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE BEHAVIOR PREVIEW",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        letterSpacing = 1.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(shapes.pill)
                        .background(if (isFloatEnabled) colors.success.copy(alpha = 0.15f) else colors.surfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isFloatEnabled) "Float Active" else "Docked Mode",
                        style = typography.labelSmall,
                        color = if (isFloatEnabled) colors.success else colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // Simulated Hardware Screen Notch Area & Dynamic Island
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .clip(shapes.large)
                    .background(Color(0xFF030509))
                    .border(1.dp, colors.borderSubtle, shapes.large),
                contentAlignment = Alignment.Center
            ) {
                // Notch hole punch
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                )

                // Simulated Dynamic Island Pill
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(shapes.pill)
                        .background(Color(0xFF0F131D))
                        .border(1.dp, if (isFloatEnabled) colors.primary.copy(alpha = 0.5f) else colors.borderHighlight, shapes.pill)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(colors.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsActive,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Text(
                            text = "New Message",
                            style = typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )

                        if (enableInlineReply) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.Reply,
                                contentDescription = null,
                                tint = colors.success,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        if (isTimeoutEnabled) {
                            Box(
                                modifier = Modifier
                                    .clip(shapes.pill)
                                    .background(colors.borderSubtle)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = formatSeconds(timeoutSeconds),
                                    style = typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Standardized icon container with smooth active tint transition.
 */
@Composable
private fun BehaviorIconContainer(
    icon: ImageVector,
    active: Boolean,
    tint: Color
) {
    val shapes = LocalHyperCapsuleShapes.current
    val colors = LocalHyperCapsuleColors.current

    val bgColor by animateColorAsState(
        targetValue = if (active) tint.copy(alpha = 0.14f) else colors.surfaceElevated,
        label = "behaviorIconBg"
    )
    val iconColor by animateColorAsState(
        targetValue = if (active) tint else colors.textTertiary,
        label = "behaviorIconTint"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(shapes.medium)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Reusable toggle card with tactile press and fluid spring switch.
 */
@Composable
fun SettingsToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    shape: androidx.compose.ui.graphics.Shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val typography = LocalHyperCapsuleTypography.current

    HyperCapsuleGlassCard(
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .tactilePress(enabled = enabled, onClick = { onCheckedChange(!checked) })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(shapes.medium)
                    .background(
                        if (checked) colors.primary.copy(alpha = 0.12f)
                        else colors.surfaceElevated
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) colors.primary else colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) colors.textPrimary else colors.textTertiary
                )
                Text(
                    text = subtitle,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            HyperCapsuleSwitch(
                checked = checked,
                enabled = enabled,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

/**
 * Formats seconds into a human-readable string (e.g. "5s", "1m", "1h").
 */
fun formatSeconds(seconds: Int): String {
    return when {
        seconds < 60 -> "${seconds}s"
        seconds < 3600 -> "${seconds / 60}m"
        else -> "${seconds / 3600}h"
    }
}

@Preview(showBackground = true)
@Composable
fun IslandSettingsControlPreview() {
    HyperCapsuleTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            IslandSettingsControl(
                config = IslandConfig(
                    isFloat = true,
                    timeout = 10,
                    floatTimeout = 5,
                    isShowShade = false,
                    removeOriginalNotification = false,
                    dismissWithOriginal = true,
                    enableInlineReply = true
                ),
                onUpdate = {}
            )
        }
    }
}
