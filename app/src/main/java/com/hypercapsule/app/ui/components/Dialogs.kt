package com.hypercapsule.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.models.IslandConfig
import com.hypercapsule.app.models.NotificationType
import com.hypercapsule.app.ui.AppInfo
import com.hypercapsule.app.ui.AppListViewModel
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppConfigBottomSheet(
    app: AppInfo,
    viewModel: AppListViewModel,
    onDismiss: () -> Unit,
    onNavConfigClick: () -> Unit
) {
    val effectiveConfig by viewModel.getEffectiveAppConfigFlow(app.packageName).collectAsState(initial = null)
    val activeTypes = effectiveConfig?.activeTypes ?: emptySet()
    val isManagedByTheme = effectiveConfig?.isManagedByTheme == true

    val appIslandConfig by viewModel.getAppIslandConfig(app.packageName).collectAsState(initial = IslandConfig())
    val globalConfig by viewModel.globalConfigFlow.collectAsState(
        initial = IslandConfig(isFloat = true, isShowShade = true, timeout = 5)
    )
    val blockedTerms by viewModel.getAppBlockedTerms(app.packageName).collectAsState(initial = emptySet())

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val navEditDesc = stringResource(R.string.cd_nav_edit)
    val activeDesc = stringResource(R.string.cd_app_state_active)
    val inactiveDesc = stringResource(R.string.cd_app_state_inactive)

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surfaceBase,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = spacing.medium)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(shapes.pill)
                    .background(colors.borderHighlight)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.default)
        ) {
            // --- SCROLLABLE CONTENT ---
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.default)
            ) {
                // HEADER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = spacing.large)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(shapes.medium)
                            .background(colors.surfaceElevatedGlass)
                            .border(1.dp, colors.borderHighlight, shapes.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        if (app.icon != null) {
                            Image(
                                bitmap = app.icon.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(shapes.medium)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Android,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = colors.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(spacing.default))

                    Column {
                        Text(
                            text = app.name,
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = app.packageName,
                            style = typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }

                HorizontalDivider(color = colors.borderSubtle)
                Spacer(modifier = Modifier.height(spacing.default))

                // THEME OVERRIDE WARNING
                if (isManagedByTheme) {
                    HyperCapsuleGlassCard(
                        modifier = Modifier.padding(bottom = spacing.default),
                        backgroundColor = colors.primary.copy(alpha = 0.12f),
                        borderColor = colors.primary.copy(alpha = 0.35f)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(spacing.medium))
                            Text(
                                text = "Behaviors are currently managed by your active Theme.",
                                color = colors.primary,
                                style = typography.bodySmall
                            )
                        }
                    }
                }

                // CARD 1: NOTIFICATION TYPES
                val activeCount = NotificationType.entries.toTypedArray().count { activeTypes.contains(it.name) }
                val activeSubtitle = stringResource(R.string.active_notifications_subtitle, activeCount)

                ExpandableSettingCard(
                    title = stringResource(R.string.active_notifications_title),
                    icon = Icons.Default.Notifications,
                    subtitle = activeSubtitle
                ) {
                    NotificationTypesContent(
                        app = app,
                        activeTypes = activeTypes,
                        viewModel = viewModel,
                        onNavConfigClick = { onDismiss(); onNavConfigClick() },
                        navEditDesc = navEditDesc
                    )
                }

                Spacer(modifier = Modifier.height(spacing.default))

                // CARD 2: APPEARANCE
                ExpandableSettingCard(
                    title = stringResource(R.string.island_appearance),
                    icon = Icons.Default.Palette
                ) {
                    AppearanceSettingsContent(
                        appConfig = appIslandConfig,
                        globalConfig = globalConfig,
                        onUpdate = { viewModel.updateAppIslandConfig(app.packageName, it) },
                        activeDesc = activeDesc,
                        inactiveDesc = inactiveDesc
                    )
                }

                Spacer(modifier = Modifier.height(spacing.default))

                // CARD 3: FILTERS
                val blockedCount = blockedTerms.size
                val blockedBadge = if (blockedCount > 0) stringResource(R.string.blocked_terms_active_count, blockedCount) else null

                ExpandableSettingCard(
                    title = stringResource(R.string.blocked_terms),
                    icon = Icons.Default.Block,
                    subtitle = blockedBadge
                ) {
                    BlocklistEditor(
                        terms = blockedTerms,
                        onUpdate = { newSet -> viewModel.updateAppBlockedTerms(app.packageName, newSet) }
                    )
                }

                Spacer(modifier = Modifier.height(spacing.large))
            }

            // FIXED FOOTER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.default, vertical = spacing.small)
            ) {
                HyperCapsuleActionButton(
                    text = stringResource(R.string.done),
                    onClick = onDismiss,
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun NotificationTypesContent(
    app: AppInfo,
    activeTypes: Set<String>,
    viewModel: AppListViewModel,
    onNavConfigClick: () -> Unit,
    navEditDesc: String
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    Column {
        NotificationType.entries.forEach { type ->
            val isChecked = activeTypes.contains(type.name)
            val typeLabel = stringResource(type.labelRes)
            val switchDesc = if (isChecked) stringResource(R.string.cd_disable_type, typeLabel)
            else stringResource(R.string.cd_enable_type, typeLabel)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.updateAppConfig(app.packageName, type, !isChecked) }
                    .padding(vertical = spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = typeLabel,
                        style = typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                }

                if (type == NotificationType.NAVIGATION) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(shapes.small)
                            .background(colors.primary.copy(alpha = 0.12f))
                            .tactilePress(onClick = onNavConfigClick)
                            .semantics { contentDescription = navEditDesc },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.small))
                }

                HyperCapsuleSwitch(
                    checked = isChecked,
                    onCheckedChange = { viewModel.updateAppConfig(app.packageName, type, it) }
                )
            }
            HorizontalDivider(color = colors.borderSubtle)
        }
    }
}

@Composable
fun ExpandableSettingCard(
    title: String,
    icon: ImageVector,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "rotation")
    val actionDesc = if (expanded) "Collapse $title" else "Expand $title"

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    HyperCapsuleGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .semantics { contentDescription = actionDesc }
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(shapes.medium)
                        .background(colors.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(spacing.default))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = typography.labelSmall,
                            color = if (subtitle.contains("active", true)) colors.primary else colors.error
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.rotate(rotation)
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = spacing.default)) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = spacing.default),
                        color = colors.borderSubtle
                    )
                    content()
                }
            }
        }
    }
}

@Composable
fun AppearanceSettingsContent(
    appConfig: IslandConfig,
    globalConfig: IslandConfig,
    onUpdate: (IslandConfig) -> Unit,
    activeDesc: String,
    inactiveDesc: String
) {
    val isUsingGlobal = appConfig.isFloat == null
    val colors = LocalHyperCapsuleColors.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (isUsingGlobal) onUpdate(globalConfig)
                    else onUpdate(IslandConfig(null, null, null))
                }
                .padding(vertical = spacing.small)
                .semantics { stateDescription = if (isUsingGlobal) activeDesc else inactiveDesc },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isUsingGlobal,
                onCheckedChange = null,
                colors = CheckboxDefaults.colors(
                    checkedColor = colors.primary,
                    uncheckedColor = colors.textSecondary
                )
            )
            Spacer(Modifier.width(spacing.medium))
            Text(
                text = stringResource(R.string.use_global_default),
                style = typography.bodyLarge,
                color = colors.textPrimary
            )
        }

        if (!isUsingGlobal) {
            Spacer(Modifier.height(spacing.small))
            IslandSettingsControl(
                config = appConfig,
                onUpdate = onUpdate
            )
        } else {
            Text(
                text = stringResource(R.string.appearance_use_defaults_desc),
                color = colors.textSecondary,
                style = typography.bodyMedium,
                modifier = Modifier.padding(top = spacing.small, start = spacing.small)
            )
        }
    }
}
