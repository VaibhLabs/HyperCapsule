package com.hypercapsule.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSettingsRow
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalSettingsScreen(
    onBack: () -> Unit,
    onNavSettingsClick: () -> Unit,
    onInlineReplyClick: () -> Unit,
    onIslandSettingsClick: () -> Unit,
    onEngineSettingsClick: () -> Unit,
    onDndSettingsClick: () -> Unit,
    onPermanentIslandClick: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val spacing = LocalHyperCapsuleSpacing.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.global_settings),
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.default, vertical = spacing.small)
                    .verticalScroll(rememberScrollState())
            ) {
                HyperCapsuleSectionHeader(title = stringResource(R.string.global_settings))

                HyperCapsuleGlassCard(
                    contentPadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        HyperCapsuleSettingsRow(
                            title = stringResource(R.string.engine),
                            subtitle = stringResource(R.string.engine_desc),
                            icon = Icons.Outlined.Memory,
                            showChevron = true,
                            onClick = onEngineSettingsClick
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                        HyperCapsuleSettingsRow(
                            title = stringResource(R.string.island_behavior_title),
                            subtitle = stringResource(R.string.island_behavior_desc),
                            icon = Icons.Outlined.DisplaySettings,
                            showChevron = true,
                            onClick = onIslandSettingsClick
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                        HyperCapsuleSettingsRow(
                            title = stringResource(R.string.dnd_mode_title),
                            subtitle = stringResource(R.string.dnd_mode_desc),
                            icon = Icons.Outlined.DoNotDisturbOn,
                            showChevron = true,
                            onClick = onDndSettingsClick
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                        HyperCapsuleSettingsRow(
                            title = stringResource(R.string.nav_layout_title),
                            subtitle = stringResource(R.string.nav_layout_desc),
                            icon = Icons.Outlined.Navigation,
                            showChevron = true,
                            onClick = onNavSettingsClick
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                        HyperCapsuleSettingsRow(
                            title = stringResource(R.string.inline_reply_title),
                            subtitle = stringResource(R.string.customize_inline_reply),
                            icon = Icons.Outlined.Edit,
                            showChevron = true,
                            onClick = onInlineReplyClick
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                        HyperCapsuleSettingsRow(
                            title = stringResource(R.string.permanent_island_title),
                            subtitle = stringResource(R.string.permanent_island_desc),
                            icon = Icons.Outlined.PushPin,
                            showChevron = true,
                            onClick = onPermanentIslandClick
                        )
                    }
                }
                Spacer(Modifier.height(spacing.huge))
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    HyperCapsuleSettingsRow(
        title = title,
        subtitle = subtitle,
        icon = icon,
        trailing = {
            HyperCapsuleSwitch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}
