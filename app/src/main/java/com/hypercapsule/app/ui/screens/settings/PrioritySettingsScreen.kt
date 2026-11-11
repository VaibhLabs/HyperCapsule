package com.hypercapsule.app.ui.screens.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Filter1
import androidx.compose.material.icons.rounded.FormatListNumbered
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.models.IslandLimitMode
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrioritySettingsScreen(
    onBack: () -> Unit,
    onNavigateToPriorityList: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context) }

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val currentMode by preferences.limitModeFlow.collectAsState(initial = IslandLimitMode.MOST_RECENT)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.island_behavior),
                subtitle = stringResource(R.string.limit_strategy),
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.default, vertical = spacing.small)
            ) {
                // 1. REFINED TOP SIMULATOR AREA
                BehaviorSimulatorArea(mode = currentMode)

                Spacer(modifier = Modifier.height(spacing.large))

                // 2. SECTION HEADER
                Text(
                    text = stringResource(R.string.limit_strategy),
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.limit_desc),
                    style = typography.bodySmall,
                    color = colors.textSecondary,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                // 3. REFINED STRATEGY CARDS (First Come First Served, Most Recent, Custom Priority)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IslandLimitMode.entries.forEach { mode ->
                        RefinedBehaviorOptionCard(
                            mode = mode,
                            isSelected = currentMode == mode,
                            onClick = { scope.launch { preferences.setLimitMode(mode) } }
                        )
                    }
                }

                // 4. PRIORITY CONFIG BUTTON (Shows when Custom Priority is active)
                AnimatedVisibility(
                    visible = currentMode == IslandLimitMode.PRIORITY,
                    enter = expandVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(),
                    exit = shrinkVertically(spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut()
                ) {
                    Column(modifier = Modifier.padding(top = spacing.medium)) {
                        HyperCapsuleActionButton(
                            text = stringResource(R.string.configure_order),
                            icon = Icons.AutoMirrored.Filled.List,
                            onClick = onNavigateToPriorityList,
                            isPrimary = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.huge))
            }
        }
    }
}

/**
 * Top Visualizer Area: Displays dynamic pill notch simulator demonstrating active strategy.
 */
@Composable
private fun BehaviorSimulatorArea(mode: IslandLimitMode) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val typography = LocalHyperCapsuleTypography.current
    val spacing = LocalHyperCapsuleSpacing.current

    HyperCapsuleGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes.extraLarge,
        backgroundColor = colors.surfaceElevatedGlass,
        borderColor = colors.primary.copy(alpha = 0.35f)
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
                        text = "ACTIVE QUEUE STRATEGY",
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        letterSpacing = 1.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(shapes.pill)
                        .background(colors.primary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = stringResource(mode.titleRes),
                        style = typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // Notch and dynamic pill preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(shapes.large)
                    .background(Color(0xFF040406))
                    .border(1.dp, colors.borderSubtle, shapes.large),
                contentAlignment = Alignment.Center
            ) {
                // Notch dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                )

                // Simulated Island Pill
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .clip(shapes.pill)
                        .background(Color(0xFF141419))
                        .border(1.dp, colors.primary.copy(alpha = 0.6f), shapes.pill)
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
                                imageVector = when (mode) {
                                    IslandLimitMode.FIRST_COME -> Icons.Rounded.Filter1
                                    IslandLimitMode.MOST_RECENT -> Icons.Rounded.Schedule
                                    IslandLimitMode.PRIORITY -> Icons.Rounded.FormatListNumbered
                                },
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(12.dp)
                            )
                        }

                        Text(
                            text = when (mode) {
                                IslandLimitMode.FIRST_COME -> "First Notification Locked"
                                IslandLimitMode.MOST_RECENT -> "Latest Event Displayed"
                                IslandLimitMode.PRIORITY -> "High-Priority Notification"
                            },
                            style = typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Redesigned Strategy Option Card:
 * Adequate height, red accent borders when selected, clear descriptions, no text clipping.
 */
@Composable
fun RefinedBehaviorOptionCard(
    mode: IslandLimitMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) colors.primary else colors.borderHighlight,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "strategyBorder"
    )

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) colors.primary.copy(alpha = 0.10f) else colors.surfaceCardGlass,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "strategyBg"
    )

    val icon = when (mode) {
        IslandLimitMode.FIRST_COME -> Icons.Rounded.Filter1
        IslandLimitMode.MOST_RECENT -> Icons.Rounded.Schedule
        IslandLimitMode.PRIORITY -> Icons.Rounded.FormatListNumbered
    }

    HyperCapsuleGlassCard(
        onClick = onClick,
        shape = shapes.extraLarge,
        borderColor = borderColor,
        backgroundColor = bgColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.default),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Strategy Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(shapes.medium)
                    .background(if (isSelected) colors.primary.copy(alpha = 0.16f) else colors.surfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) colors.primary else colors.textSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(spacing.medium))

            // Strategy Title & Description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(mode.titleRes),
                    fontWeight = FontWeight.Bold,
                    style = typography.titleSmall,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(mode.descRes),
                    style = typography.bodySmall,
                    color = colors.textSecondary,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(spacing.small))

            // Radio Button
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = colors.primary,
                    unselectedColor = colors.textSecondary
                )
            )
        }
    }
}
