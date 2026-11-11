package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.motion.HyperCapsuleMotion
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

data class HyperCapsuleNavItem(
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
)

/**
 * Floating glass pill bottom navigation bar for HyperOS aesthetic.
 */
@Composable
fun HyperCapsuleBottomNavigation(
    items: List<HyperCapsuleNavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current
    val haptic = LocalHapticFeedback.current
    val navBarBottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = navBarBottomPadding + spacing.small, start = spacing.default, end = spacing.default),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(shapes.extraLarge)
                .background(colors.surfaceElevatedGlass)
                .border(1.dp, colors.borderHighlight, shapes.extraLarge)
                .padding(horizontal = spacing.medium, vertical = spacing.small)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex

                    val targetIcon = if (isSelected) item.selectedIcon else item.icon
                    val iconTint by animateColorAsState(
                        targetValue = if (isSelected) colors.primary else colors.textSecondary,
                        label = "iconTint"
                    )
                    val bgAlpha by animateFloatAsState(
                        targetValue = if (isSelected) 0.15f else 0.0f,
                        animationSpec = HyperCapsuleMotion.TactileSpring,
                        label = "bgAlpha"
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.05f else 1.0f,
                        animationSpec = HyperCapsuleMotion.BouncySpring,
                        label = "navScale"
                    )

                    Box(
                        modifier = Modifier
                            .clip(shapes.large)
                            .background(colors.primary.copy(alpha = bgAlpha))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                if (!isSelected) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onItemSelected(index)
                                }
                            }
                            .padding(horizontal = spacing.default, vertical = spacing.small),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                            }
                        ) {
                            Icon(
                                imageVector = targetIcon,
                                contentDescription = item.title,
                                tint = iconTint,
                                modifier = Modifier.size(22.dp)
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(spacing.small))
                                Text(
                                    text = item.title,
                                    style = typography.labelLarge,
                                    color = colors.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
