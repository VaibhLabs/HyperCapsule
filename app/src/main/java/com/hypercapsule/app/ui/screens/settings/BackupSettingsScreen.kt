package com.hypercapsule.app.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.data.model.HyperCapsuleBackup
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import com.hypercapsule.app.util.BackupManager
import com.hypercapsule.app.util.BackupSelection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSettingsScreen(
    onBack: () -> Unit,
    backupManager: BackupManager,
    onBackupFileLoaded: (HyperCapsuleBackup) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    var exportSelection by remember { mutableStateOf(BackupSelection()) }
    var isProcessing by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        if (uri != null) {
            isProcessing = true
            scope.launch {
                val result = backupManager.performExport(uri, exportSelection)
                isProcessing = false
                if (result.isSuccess) {
                    snackbarHostState.showSnackbar(context.getString(R.string.export_success))
                } else {
                    snackbarHostState.showSnackbar(context.getString(R.string.export_failed, result.exceptionOrNull()?.message))
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            isProcessing = true
            scope.launch {
                val result = backupManager.readBackupFile(uri)
                isProcessing = false

                result.fold(
                    onSuccess = { backup ->
                        onBackupFileLoaded(backup)
                    },
                    onFailure = { error ->
                        snackbarHostState.showSnackbar(context.getString(R.string.import_failed, error.message))
                    }
                )
            }
        }
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
                // Header Info Card
                HyperCapsuleGlassCard(
                    backgroundColor = colors.primary.copy(alpha = 0.12f),
                    borderColor = colors.primary.copy(alpha = 0.35f),
                    modifier = Modifier.fillMaxWidth().padding(bottom = spacing.default)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(shapes.medium)
                                .background(colors.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CloudSync,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = colors.primary
                            )
                        }
                        Spacer(Modifier.width(spacing.default))
                        Text(
                            text = stringResource(R.string.backup_options_subtitle),
                            style = typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    }
                }

                // Options Section
                HyperCapsuleSectionHeader(title = stringResource(R.string.backup_section_title))

                HyperCapsuleGlassCard(
                    shape = shapes.large,
                    contentPadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        BackupOptionItem(
                            title = stringResource(R.string.option_app_settings),
                            subtitle = stringResource(R.string.option_app_settings_desc),
                            icon = Icons.Default.Settings,
                            checked = exportSelection.includeSettings,
                            onCheckedChange = { exportSelection = exportSelection.copy(includeSettings = it) }
                        )

                        HorizontalDivider(color = colors.borderSubtle)

                        BackupOptionItem(
                            title = stringResource(R.string.option_blocklist),
                            subtitle = stringResource(R.string.option_blocklist_desc),
                            icon = Icons.Default.CheckCircle,
                            checked = exportSelection.includeBlocklist,
                            onCheckedChange = { exportSelection = exportSelection.copy(includeBlocklist = it) }
                        )

                        HorizontalDivider(color = colors.borderSubtle)

                        BackupOptionItem(
                            title = stringResource(R.string.option_priorities),
                            subtitle = stringResource(R.string.option_priorities_desc),
                            icon = Icons.Default.CheckCircle,
                            checked = exportSelection.includePriorities,
                            onCheckedChange = { exportSelection = exportSelection.copy(includePriorities = it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.huge))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    HyperCapsuleActionButton(
                        text = stringResource(R.string.action_export),
                        icon = Icons.Default.Upload,
                        onClick = { exportLauncher.launch(BackupManager.generateFileName()) },
                        isPrimary = true,
                        enabled = !isProcessing,
                        modifier = Modifier.weight(1f)
                    )

                    HyperCapsuleActionButton(
                        text = stringResource(R.string.action_import),
                        icon = Icons.Default.Download,
                        onClick = { importLauncher.launch(arrayOf("*/*")) },
                        isPrimary = false,
                        enabled = !isProcessing,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(spacing.huge))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = spacing.default)
        )
    }
}

@Composable
fun BackupOptionItem(
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
            Text(
                text = title,
                style = typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = typography.bodySmall,
                color = colors.textSecondary
            )
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
