package com.hypercapsule.app.ui.screens.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LowPriority
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.models.IslandLimitMode
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
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
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.default, vertical = spacing.small)
            ) {
                // 1. VISUALIZER ANIMATION AREA
                HyperCapsuleGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = shapes.large,
                    backgroundColor = colors.surfaceElevatedGlass
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        BehaviorVisualizer(currentMode)
                    }
                }

                Spacer(modifier = Modifier.height(spacing.large))

                // 2. BIG TITLE
                Text(
                    text = stringResource(R.string.limit_strategy),
                    style = typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.limit_desc),
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(spacing.default))

                // 3. OPTION CARDS
                IslandLimitMode.entries.forEach { mode ->
                    BehaviorOptionCard(
                        mode = mode,
                        isSelected = currentMode == mode,
                        onClick = { scope.launch { preferences.setLimitMode(mode) } }
                    )
                    Spacer(modifier = Modifier.height(spacing.small))
                }

                // 4. PRIORITY CONFIG BUTTON
                if (currentMode == IslandLimitMode.PRIORITY) {
                    Spacer(modifier = Modifier.height(spacing.default))
                    HyperCapsuleActionButton(
                        text = stringResource(R.string.configure_order),
                        icon = Icons.AutoMirrored.Filled.List,
                        onClick = onNavigateToPriorityList,
                        isPrimary = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun BehaviorVisualizer(mode: IslandLimitMode) {
    val colors = LocalHyperCapsuleColors.current
    val color = colors.primary

    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        when (mode) {
            IslandLimitMode.FIRST_COME -> {
                Dot(color); Spacer(Modifier.width(6.dp)); Dot(color); Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.Block, null, tint = colors.error, modifier = Modifier.size(20.dp))
            }
            IslandLimitMode.MOST_RECENT -> {
                Dot(color.copy(0.3f)); Spacer(Modifier.width(6.dp)); Dot(color.copy(0.6f)); Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.History, null, tint = color, modifier = Modifier.size(20.dp))
            }
            IslandLimitMode.PRIORITY -> {
                Icon(Icons.Default.LowPriority, null, tint = color, modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Composable
fun Dot(color: Color) {
    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(color))
}

@Composable
fun BehaviorOptionCard(
    mode: IslandLimitMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    HyperCapsuleGlassCard(
        onClick = onClick,
        shape = shapes.large,
        borderColor = if (isSelected) colors.primary else colors.borderHighlight,
        backgroundColor = if (isSelected) colors.primary.copy(alpha = 0.08f) else colors.surfaceCardGlass,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = colors.primary,
                    unselectedColor = colors.textSecondary
                )
            )
            Spacer(modifier = Modifier.width(spacing.default))
            Column {
                Text(
                    text = stringResource(mode.titleRes),
                    fontWeight = FontWeight.Bold,
                    style = typography.titleSmall,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(mode.descRes),
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}
