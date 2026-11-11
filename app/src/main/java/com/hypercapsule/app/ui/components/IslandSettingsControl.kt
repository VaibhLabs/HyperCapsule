package com.hypercapsule.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.models.IslandConfig
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSlider
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.theme.HyperCapsuleTheme

// Define our snap points (in seconds) for the auto-hide island
val timeoutSteps = listOf(
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 45,
    60, 300, 900, 1800, 3600
)
private val timePopUpSteps = listOf(
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30
)

@Composable
fun IslandSettingsControl(
    config: IslandConfig,
    defaultConfig: IslandConfig? = null,
    onUpdate: (IslandConfig) -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    val isOverridden = config.isFloat != null
    val displayConfig = if (isOverridden) config else (defaultConfig ?: config)

    // Timeout is "Enabled" if it's > 0
    val currentTimeout = displayConfig.timeout ?: 10
    val isTimeoutEnabled = currentTimeout > 0

    Column(verticalArrangement = Arrangement.spacedBy(spacing.itemSpacing)) {

        HyperCapsuleSectionHeader(
            title = stringResource(R.string.global_behavior),
            icon = Icons.Outlined.Tune
        )

        // --- TIMEOUT CARD ---
        HyperCapsuleGlassCard(
            shape = shapes.squircleLarge,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(spacing.cardPadding)) {
                // Header with Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shapes.squircleSmall)
                            .background(colors.accentPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.auto_hide_island),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = if (isTimeoutEnabled) stringResource(R.string.hides_after_a_set_time) else stringResource(
                                R.string.behavior_hide_desc
                            ),
                            style = MaterialTheme.typography.bodySmall,
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

                // Expandable Slider Section
                AnimatedVisibility(
                    visible = isTimeoutEnabled,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.auto_hide_island),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textTertiary
                            )
                            Text(
                                text = formatSeconds(currentTimeout),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.accentPrimary,
                                fontWeight = FontWeight.Bold
                            )
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
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textTertiary
                        )
                    }
                }
            }
        }

        HyperCapsuleSectionHeader(
            title = stringResource(R.string.xiaomi_featured_notifications),
            icon = Icons.Outlined.NotificationsActive
        )

        // --- FLOAT SETTINGS (Heads-up Popup) ---
        val isFloatEnabled = displayConfig.isFloat ?: true
        val currentFloatTimeout = displayConfig.floatTimeout ?: 10

        HyperCapsuleGlassCard(
            shape = shapes.squircleLarge,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(spacing.cardPadding)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shapes.squircleSmall)
                            .background(colors.accentSecondary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = colors.accentSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.setting_float),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = stringResource(R.string.setting_float_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                    HyperCapsuleSwitch(
                        checked = isFloatEnabled,
                        onCheckedChange = { onUpdate(config.copy(isFloat = it)) }
                    )
                }

                AnimatedVisibility(
                    visible = isFloatEnabled,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.setting_float_timeout_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textTertiary
                            )
                            Text(
                                text = stringResource(R.string.seconds_suffix, currentFloatTimeout),
                                style = MaterialTheme.typography.titleMedium,
                                color = colors.accentSecondary,
                                fontWeight = FontWeight.Bold
                            )
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
            }
        }

        SettingsToggleCard(
            title = stringResource(R.string.setting_shade),
            subtitle = stringResource(R.string.setting_shade_desc),
            icon = Icons.Default.Layers,
            checked = displayConfig.isShowShade ?: false,
            onCheckedChange = {
                val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                onUpdate(config.copy(isShowShade = it, isFloat = currentIsFloat))
            },
            shape = shapes.squircleLarge
        )

        HyperCapsuleSectionHeader(
            title = stringResource(R.string.notification_management),
            icon = Icons.Outlined.ManageAccounts
        )

        val removeOriginalOn = displayConfig.removeOriginalNotification == true

        SettingsToggleCard(
            title = stringResource(R.string.remove_original_notification),
            subtitle = stringResource(R.string.remove_original_notification_desc),
            icon = Icons.Default.DeleteSweep,
            checked = removeOriginalOn,
            onCheckedChange = {
                val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                onUpdate(config.copy(removeOriginalNotification = it, isFloat = currentIsFloat))
            },
            shape = shapes.squircleLarge
        )

        SettingsToggleCard(
            title = stringResource(R.string.dismiss_with_original),
            subtitle = stringResource(R.string.dismiss_with_original_desc),
            icon = Icons.Default.DeleteSweep,
            checked = displayConfig.dismissWithOriginal ?: false,
            enabled = !removeOriginalOn,
            onCheckedChange = {
                val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                onUpdate(config.copy(dismissWithOriginal = it, isFloat = currentIsFloat))
            },
            shape = shapes.squircleLarge
        )

        SettingsToggleCard(
            title = stringResource(R.string.enable_inline_reply),
            subtitle = stringResource(R.string.enable_inline_reply_desc),
            icon = Icons.AutoMirrored.Filled.Reply,
            checked = displayConfig.enableInlineReply ?: true,
            enabled = !removeOriginalOn,
            onCheckedChange = {
                val currentIsFloat = config.isFloat ?: defaultConfig?.isFloat ?: true
                onUpdate(config.copy(enableInlineReply = it, isFloat = currentIsFloat))
            },
            shape = shapes.squircleLarge
        )

        AnimatedVisibility(
            visible = removeOriginalOn,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            HyperCapsuleGlassCard(
                shape = shapes.squircleMedium,
                tint = colors.accentWarning.copy(alpha = 0.1f),
                borderColor = colors.accentWarning.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.remove_original_notification_hidden_warning),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.accentWarning,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/**
 * Formats seconds into a readable string (e.g. "10s", "5m", "1h")
 */
fun formatSeconds(seconds: Int): String {
    return when {
        seconds < 60 -> "${seconds}s"
        seconds < 3600 -> "${seconds / 60}m"
        else -> "${seconds / 3600}h"
    }
}

@Composable
fun SettingsToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    enabled: Boolean = true,
    shape: Shape,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    HyperCapsuleGlassCard(
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .tactilePress(enabled = enabled, onClick = { onCheckedChange(!checked) })
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(shapes.squircleSmall)
                    .background(
                        if (checked) colors.accentPrimary.copy(alpha = 0.12f)
                        else colors.cardBorder.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) colors.accentPrimary else colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) colors.textPrimary else colors.textTertiary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
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

@Preview(showBackground = true)
@Composable
fun IslandSettingsControlPreview() {
    HyperCapsuleTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            IslandSettingsControl(
                config = IslandConfig(
                    isFloat = true,
                    timeout = 5,
                    floatTimeout = 5,
                    isShowShade = true,
                    removeOriginalNotification = false,
                    dismissWithOriginal = false
                ),
                onUpdate = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsToggleCardPreview() {
    HyperCapsuleTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            SettingsToggleCard(
                title = "Example Title",
                subtitle = "Example subtitle for the toggle card",
                icon = Icons.Default.Layers,
                checked = true,
                shape = RoundedCornerShape(24.dp),
                onCheckedChange = {}
            )
        }
    }
}
