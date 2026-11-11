package com.hypercapsule.app.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.AppCategory
import com.hypercapsule.app.ui.SortOption
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSearchField
import com.hypercapsule.app.ui.designsystem.motion.HyperCapsuleMotion
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

/**
 * Redesigned Liquid Glass App List Filter & Search Section
 */
@Composable
fun AppListFilterSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: AppCategory,
    onCategoryChange: (AppCategory) -> Unit,
    sortOption: SortOption,
    onSortChange: (SortOption) -> Unit
) {
    val scrollState = rememberScrollState()
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current
    val haptic = LocalHapticFeedback.current
    val categories = AppCategory.entries.toTypedArray()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.small)
    ) {
        // Search Field
        Box(modifier = Modifier.padding(horizontal = spacing.default, vertical = spacing.extraSmall)) {
            HyperCapsuleSearchField(
                query = searchQuery,
                onQueryChange = onSearchChange,
                placeholder = stringResource(R.string.search_hint)
            )
        }

        // Toolbar Row: Sort Button + Category Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = spacing.default, vertical = spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            // Sort Toggle Button
            val isAscending = sortOption == SortOption.NAME_AZ
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(shapes.pill)
                    .background(colors.surfaceElevatedGlass)
                    .border(1.dp, colors.borderHighlight, shapes.pill)
                    .tactilePress(onClick = {
                        onSortChange(if (isAscending) SortOption.NAME_ZA else SortOption.NAME_AZ)
                    }),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isAscending) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = stringResource(R.string.sort_order),
                    tint = colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Category Filter Pills
            categories.forEach { category ->
                val isSelected = selectedCategory == category
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) colors.primary else colors.surfaceElevatedGlass,
                    label = "catBgColor"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) Color.Black else colors.textSecondary,
                    label = "catTextColor"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) colors.primary else colors.borderHighlight,
                    label = "catBorderColor"
                )

                Box(
                    modifier = Modifier
                        .clip(shapes.pill)
                        .background(bgColor)
                        .border(1.dp, borderColor, shapes.pill)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!isSelected) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onCategoryChange(category)
                            }
                        }
                        .padding(horizontal = spacing.default, vertical = spacing.small),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(category, isSelected),
                            contentDescription = null,
                            tint = textColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(spacing.small))
                        Text(
                            text = stringResource(getCategoryLabelRes(category)),
                            style = typography.labelMedium,
                            color = textColor
                        )
                    }
                }
            }
        }
    }
}

@StringRes
private fun getCategoryLabelRes(category: AppCategory): Int {
    return when (category) {
        AppCategory.ALL -> R.string.cat_all
        AppCategory.MUSIC -> R.string.cat_music
        AppCategory.MAPS -> R.string.cat_nav
        AppCategory.TIMER -> R.string.cat_timer
        AppCategory.OTHER -> R.string.cat_other
    }
}

private fun getCategoryIcon(category: AppCategory, isSelected: Boolean): ImageVector {
    return if (isSelected) {
        when (category) {
            AppCategory.ALL -> Icons.Filled.Apps
            AppCategory.MUSIC -> Icons.Filled.MusicNote
            AppCategory.MAPS -> Icons.Filled.Place
            AppCategory.TIMER -> Icons.Filled.Timer
            AppCategory.OTHER -> Icons.Filled.Category
        }
    } else {
        when (category) {
            AppCategory.ALL -> Icons.Outlined.Apps
            AppCategory.MUSIC -> Icons.Outlined.MusicNote
            AppCategory.MAPS -> Icons.Outlined.Place
            AppCategory.TIMER -> Icons.Outlined.Timer
            AppCategory.OTHER -> Icons.Outlined.Category
        }
    }
}
