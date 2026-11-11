package com.hypercapsule.app.ui.screens.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.NotificationsPaused
import androidx.compose.material.icons.outlined.VolumeOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleScaffold
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import kotlinx.coroutines.launch

@Composable
fun DndSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val isDndModeEnabled by prefs.isDndModeEnabledFlow.collectAsState(initial = false)
    val autoDetectDnd by prefs.autoDetectDndFlow.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    HyperCapsuleScaffold(
        topBar = {
            HyperCapsuleTopBar(
                title = stringResource(R.string.dnd_mode_title),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.screenPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            HyperCapsuleGlassCard(
                shape = shapes.squircleMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(shapes.squircleSmall)
                            .background(colors.accentPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.VolumeOff,
                            contentDescription = null,
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.dnd_mode_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sectionSpacing))

            HyperCapsuleSectionHeader(
                title = stringResource(R.string.dnd_mode_title),
                icon = Icons.Outlined.DoNotDisturbOn
            )
            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            // Auto Detect DND
            HyperCapsuleGlassCard(
                shape = shapes.squircleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .tactilePress {
                        scope.launch { prefs.setAutoDetectDnd(!autoDetectDnd) }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.cardPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shapes.squircleSmall)
                            .background(
                                if (autoDetectDnd) colors.accentPrimary.copy(alpha = 0.15f)
                                else colors.cardBorder.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DoNotDisturbOn,
                            contentDescription = null,
                            tint = if (autoDetectDnd) colors.accentPrimary else colors.textSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.dnd_auto_detect),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = stringResource(R.string.dnd_auto_detect_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                    HyperCapsuleSwitch(
                        checked = autoDetectDnd,
                        onCheckedChange = {
                            scope.launch { prefs.setAutoDetectDnd(it) }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            // Manual DND
            HyperCapsuleGlassCard(
                shape = shapes.squircleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .tactilePress {
                        scope.launch { prefs.setDndModeEnabled(!isDndModeEnabled) }
                    }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.cardPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shapes.squircleSmall)
                            .background(
                                if (isDndModeEnabled) colors.accentPrimary.copy(alpha = 0.15f)
                                else colors.cardBorder.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsPaused,
                            contentDescription = null,
                            tint = if (isDndModeEnabled) colors.accentPrimary else colors.textSecondary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.dnd_manual_toggle),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = stringResource(R.string.dnd_manual_toggle_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                    HyperCapsuleSwitch(
                        checked = isDndModeEnabled,
                        onCheckedChange = {
                            scope.launch { prefs.setDndModeEnabled(it) }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sectionSpacing))
        }
    }
}
