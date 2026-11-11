package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

/**
 * Standardized premium settings row for HyperCapsule.
 */
@Composable
fun HyperCapsuleSettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color? = null,
    iconBackground: Color? = null,
    trailing: (@Composable () -> Unit)? = null,
    showChevron: Boolean = false,
    valueText: String? = null,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val finalIconTint = iconTint ?: colors.primary
    val finalIconBg = iconBackground ?: finalIconTint.copy(alpha = 0.12f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (onClick != null && enabled) Modifier.tactilePress(pressedScale = 0.98f, onClick = onClick)
                else Modifier
            )
            .padding(vertical = spacing.medium, horizontal = spacing.default),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(shapes.medium)
                        .background(finalIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) finalIconTint else colors.textTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(spacing.default))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.titleSmall,
                    color = if (enabled) colors.textPrimary else colors.textTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            if (valueText != null) {
                Text(
                    text = valueText,
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )
            }
            if (trailing != null) {
                trailing()
            } else if (showChevron) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colors.textTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
