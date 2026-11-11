package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

/**
 * Floating glass Top App Bar for HyperCapsule
 */
@Composable
fun HyperCapsuleTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = onBack,
    navigationIcon: ImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
    actions: @Composable RowScope.() -> Unit = {}
) {
    val colors = LocalHyperCapsuleColors.current
    val typography = LocalHyperCapsuleTypography.current
    val spacing = LocalHyperCapsuleSpacing.current
    val shapes = LocalHyperCapsuleShapes.current
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background.copy(alpha = 0.85f))
            .padding(top = statusBarPadding)
            .padding(horizontal = spacing.default, vertical = spacing.medium)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackClick != null) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shapes.small)
                            .background(colors.surfaceElevatedGlass)
                            .border(1.dp, colors.borderHighlight, shapes.small)
                            .tactilePress(onClick = onBackClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = navigationIcon,
                            contentDescription = "Back",
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.medium))
                }

                Column {
                    Text(
                        text = title,
                        style = typography.titleLarge,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = typography.bodySmall,
                            color = colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                actions()
            }
        }
    }
}
