package com.hypercapsule.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.data.db.SettingsKeys
import com.hypercapsule.app.data.model.HyperCapsuleBackup
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import com.hypercapsule.app.util.BackupSelection
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportPreviewScreen(
    backupData: HyperCapsuleBackup,
    onBack: () -> Unit,
    onConfirmRestore: (BackupSelection) -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val settingsList = backupData.settings

    val hasBlocklist = settingsList.any { it.key == SettingsKeys.GLOBAL_BLOCKED_TERMS || it.key.endsWith("_blocked") }
    val hasPriorities = settingsList.any { it.key == SettingsKeys.PRIORITY_ORDER }
    val hasSettings = settingsList.any {
        it.key != SettingsKeys.GLOBAL_BLOCKED_TERMS &&
                !it.key.endsWith("_blocked") &&
                it.key != SettingsKeys.PRIORITY_ORDER
    }

    val formattedDate = remember {
        SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(Date(backupData.metadata.timestamp))
    }

    var selection by remember {
        mutableStateOf(BackupSelection(
            includeSettings = hasSettings,
            includeBlocklist = hasBlocklist,
            includePriorities = hasPriorities
        ))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.backup_restore_title),
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.default, vertical = spacing.small)
            ) {
                // METADATA CARD
                HyperCapsuleGlassCard(
                    backgroundColor = colors.primary.copy(alpha = 0.12f),
                    borderColor = colors.primary.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth().padding(bottom = spacing.default)
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(R.string.backup_info_title),
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(Modifier.height(spacing.medium))

                        MetadataRow(stringResource(R.string.backup_meta_date), formattedDate)
                        MetadataRow(stringResource(R.string.backup_meta_device), backupData.metadata.deviceModel)
                        MetadataRow(stringResource(R.string.backup_meta_version), "${backupData.metadata.versionName} (${backupData.metadata.versionCode})")
                        MetadataRow(stringResource(R.string.backup_meta_keys), "${settingsList.size}")
                    }
                }

                HyperCapsuleSectionHeader(title = stringResource(R.string.backup_options_subtitle))

                // SELECTION LIST
                HyperCapsuleGlassCard(
                    shape = shapes.large,
                    contentPadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        if (hasSettings) {
                            PreviewCheckboxItem(
                                title = stringResource(R.string.option_app_settings),
                                subtitle = stringResource(R.string.option_app_settings_desc),
                                icon = Icons.Default.Settings,
                                checked = selection.includeSettings,
                                onCheckedChange = { selection = selection.copy(includeSettings = it) }
                            )
                            HorizontalDivider(color = colors.borderSubtle)
                        }

                        if (hasBlocklist) {
                            PreviewCheckboxItem(
                                title = stringResource(R.string.option_blocklist),
                                subtitle = stringResource(R.string.option_blocklist_desc),
                                icon = Icons.Default.CheckCircle,
                                checked = selection.includeBlocklist,
                                onCheckedChange = { selection = selection.copy(includeBlocklist = it) }
                            )
                            HorizontalDivider(color = colors.borderSubtle)
                        } else {
                            DisabledPreviewItem(stringResource(R.string.option_blocklist) + stringResource(R.string.backup_empty_suffix))
                        }

                        if (hasPriorities) {
                            PreviewCheckboxItem(
                                title = stringResource(R.string.option_priorities),
                                subtitle = stringResource(R.string.option_priorities_desc),
                                icon = Icons.Default.CheckCircle,
                                checked = selection.includePriorities,
                                onCheckedChange = { selection = selection.copy(includePriorities = it) }
                            )
                        } else {
                            DisabledPreviewItem(stringResource(R.string.option_priorities) + stringResource(R.string.backup_empty_suffix))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.huge))

                // RESTORE BUTTON
                HyperCapsuleActionButton(
                    text = stringResource(R.string.action_restore_selected),
                    icon = Icons.Default.Restore,
                    onClick = { onConfirmRestore(selection) },
                    isPrimary = true,
                    enabled = selection.includeSettings || selection.includeBlocklist || selection.includePriorities,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(spacing.huge))
            }
        }
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    val colors = LocalHyperCapsuleColors.current
    val typography = LocalHyperCapsuleTypography.current

    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            text = "$label: ",
            style = typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textSecondary,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = value,
            style = typography.bodyMedium,
            color = colors.textPrimary
        )
    }
}

@Composable
fun PreviewCheckboxItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = spacing.default, vertical = spacing.medium),
        verticalAlignment = Alignment.CenterVertically
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

        Spacer(modifier = Modifier.width(spacing.default))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = typography.titleSmall, fontWeight = FontWeight.Bold, color = colors.textPrimary)
            Text(text = subtitle, style = typography.bodySmall, color = colors.textSecondary)
        }

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.primary,
                uncheckedColor = colors.textSecondary
            )
        )
    }
}

@Composable
fun DisabledPreviewItem(title: String) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.default, vertical = spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(shapes.medium)
                .background(colors.surfaceElevatedGlass),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, null, tint = colors.textTertiary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(spacing.default))
        Text(
            text = title,
            style = typography.titleSmall,
            color = colors.textTertiary,
            modifier = Modifier.weight(1f)
        )
    }
}
