package com.hypercapsule.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.AppInfo
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

/**
 * Redesigned Liquid Glass App List Item for HyperCapsule
 */
@Composable
fun AppListItem(
    app: AppInfo,
    onToggle: (Boolean) -> Unit,
    onSettingsClick: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .tactilePress(pressedScale = 0.99f, onClick = onSettingsClick)
            .padding(horizontal = spacing.default, vertical = spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Icon inside a squircle with border highlight
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(shapes.medium)
                .background(colors.surfaceElevatedGlass)
                .border(1.dp, colors.borderHighlight, shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            if (app.icon != null) {
                Image(
                    bitmap = app.icon.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(shapes.medium)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Android,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(spacing.default))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                Text(
                    text = app.name,
                    style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                    color = if (app.isInstalled) colors.textPrimary else colors.textSecondary.copy(alpha = 0.6f)
                )

                if (!app.isInstalled) {
                    Box(
                        modifier = Modifier
                            .clip(shapes.extraSmall)
                            .background(colors.error.copy(alpha = 0.15f))
                            .border(1.dp, colors.error.copy(alpha = 0.3f), shapes.extraSmall)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.app_uninstalled_badge),
                            style = typography.labelSmall,
                            color = colors.error
                        )
                    }
                }
            }

            Text(
                text = app.packageName,
                style = typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(spacing.small))

        if (app.isInstalled) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(shapes.small)
                    .background(colors.surfaceElevatedGlass)
                    .border(1.dp, colors.borderHighlight, shapes.small)
                    .tactilePress(onClick = onSettingsClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = stringResource(R.string.settings_action),
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.width(spacing.medium))

        HyperCapsuleSwitch(
            checked = app.isBridged,
            onCheckedChange = onToggle
        )
    }
}
