package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

/**
 * Premium Card displaying system permission or service health status.
 */
@Composable
fun HyperCapsulePermissionCard(
    title: String,
    description: String,
    isGranted: Boolean,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    actionButtonText: String = "Grant"
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val statusColor = if (isGranted) colors.success else colors.warning
    val statusBg = statusColor.copy(alpha = 0.10f)

    HyperCapsuleGlassCard(
        modifier = modifier,
        borderColor = if (isGranted) colors.borderHighlight else colors.warning.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                                .size(40.dp)
                                .clip(shapes.medium)
                                .background(statusBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(spacing.default))
                    }

                    Column {
                        Text(
                            text = title,
                            style = typography.titleSmall,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = description,
                            style = typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }

                Box(modifier = Modifier.padding(start = spacing.small)) {
                    if (isGranted) {
                        HyperCapsuleStatusIndicator(
                            text = "Active",
                            statusType = HyperCapsuleStatusType.ACTIVE,
                            pulse = false
                        )
                    } else {
                        HyperCapsuleActionButton(
                            text = actionButtonText,
                            onClick = onGrantClick,
                            isPrimary = true
                        )
                    }
                }
            }
        }
    }
}
