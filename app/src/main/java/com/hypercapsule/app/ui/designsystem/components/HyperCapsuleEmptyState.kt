package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

/**
 * Minimalist empty state with neon accenting.
 */
@Composable
fun HyperCapsuleEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val colors = LocalHyperCapsuleColors.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.huge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(colors.primary.copy(alpha = 0.12f))
                .border(1.dp, colors.primary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(spacing.large))

        Text(
            text = title,
            style = typography.titleMedium,
            color = colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(spacing.small))

        Text(
            text = description,
            style = typography.bodyMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center
        )

        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(spacing.large))
            HyperCapsuleActionButton(
                text = actionButtonText,
                onClick = onActionClick,
                isPrimary = true
            )
        }
    }
}
