package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon

/**
 * Section Header for grouping settings or cards.
 */
@Composable
fun HyperCapsuleSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    badgeText: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(shapes.small)
                        .background(colors.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(spacing.small))
            }
            Text(
                text = title.uppercase(),
                style = typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.primary
            )
            if (badgeText != null) {
                Spacer(modifier = Modifier.width(spacing.small))
                Box(
                    modifier = Modifier
                        .clip(shapes.pill)
                        .background(colors.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        style = typography.labelSmall,
                        color = colors.primary
                    )
                }
            }
        }

        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = typography.labelMedium,
                color = colors.primary,
                modifier = Modifier
                    .clip(shapes.small)
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = spacing.small, vertical = 4.dp)
            )
        }
    }
}
