package com.hypercapsule.app.ui.screens.theme.content

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AltRoute
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Security
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
import com.hypercapsule.app.ui.components.EngineOptionCard
import com.hypercapsule.app.ui.components.EnginePreview
import com.hypercapsule.app.ui.designsystem.components.ButtonVariant
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import kotlinx.coroutines.launch

@Composable
fun EngineThemeContent(
    isNative: Boolean?,
    showDefaultOption: Boolean = true,
    onEngineChange: (Boolean?) -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val isShizukuInstalled = remember { com.hypercapsule.app.util.ShizukuManager.isShizukuInstalled(context) }
    val isShizukuRunning by com.hypercapsule.app.util.ShizukuManager.isShizukuRunning.collectAsState()
    val isPermissionGranted by com.hypercapsule.app.util.ShizukuManager.isPermissionGranted.collectAsState()
    val isWorkaroundEnabled by prefs.isShizukuWorkaroundEnabled.collectAsState(initial = false)
    val scope = rememberCoroutineScope()
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.screenPadding)
    ) {
        HyperCapsuleSectionHeader(
            title = stringResource(R.string.engine_preview_title),
            icon = Icons.Outlined.AltRoute
        )
        Spacer(modifier = Modifier.height(spacing.itemSpacing))

        EnginePreview(isNative = isNative ?: false)

        Spacer(modifier = Modifier.height(spacing.sectionSpacing))

        HyperCapsuleSectionHeader(
            title = stringResource(R.string.engine_design_section),
            icon = Icons.Outlined.Engineering
        )
        Spacer(modifier = Modifier.height(spacing.itemSpacing))

        if (showDefaultOption) {
            EngineOptionCard(
                title = stringResource(R.string.use_global_default),
                description = stringResource(R.string.appearance_use_defaults_desc),
                isSelected = isNative == null,
                onClick = { onEngineChange(null) }
            )
            Spacer(modifier = Modifier.height(spacing.itemSpacing))
        }

        // Xiaomi Engine Option
        EngineOptionCard(
            title = stringResource(R.string.engine_xiaomi_title),
            description = stringResource(R.string.engine_xiaomi_desc),
            isSelected = isNative == false,
            onClick = { onEngineChange(false) }
        )

        Spacer(modifier = Modifier.height(spacing.itemSpacing))

        // Native Android Live Updates Option
        EngineOptionCard(
            title = stringResource(R.string.engine_native_title),
            description = stringResource(R.string.engine_native_desc),
            isSelected = isNative == true,
            onClick = { onEngineChange(true) }
        )

        if (isNative == false) {
            Spacer(modifier = Modifier.height(spacing.sectionSpacing))

            HyperCapsuleSectionHeader(
                title = stringResource(R.string.shizuku_workaround_title),
                icon = Icons.Outlined.Security
            )
            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            HyperCapsuleGlassCard(
                shape = shapes.squircleLarge,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(spacing.cardPadding)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.shizuku_workaround_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        HyperCapsuleSwitch(
                            checked = isWorkaroundEnabled,
                            onCheckedChange = { scope.launch { prefs.setShizukuWorkaroundEnabled(it) } },
                            enabled = isShizukuInstalled
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.shizuku_workaround_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
            }

            if (isShizukuInstalled && isWorkaroundEnabled) {
                Spacer(modifier = Modifier.height(spacing.itemSpacing))

                HyperCapsuleGlassCard(
                    shape = shapes.squircleMedium,
                    tint = if (isPermissionGranted) colors.accentSuccess.copy(alpha = 0.12f) else colors.accentWarning.copy(alpha = 0.12f),
                    borderColor = if (isPermissionGranted) colors.accentSuccess.copy(alpha = 0.4f) else colors.accentWarning.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(shapes.squircleSmall)
                                .background(
                                    if (isPermissionGranted) colors.accentSuccess.copy(alpha = 0.2f)
                                    else colors.accentWarning.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPermissionGranted) Icons.Default.Security else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isPermissionGranted) colors.accentSuccess else colors.accentWarning,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(if (isPermissionGranted) R.string.shizuku_permission_granted else R.string.shizuku_status_running),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary
                            )
                            Text(
                                text = stringResource(if (isPermissionGranted) R.string.shizuku_status_running else R.string.shizuku_permission_denied),
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.itemSpacing))

                HyperCapsuleActionButton(
                    text = stringResource(if (isPermissionGranted) R.string.perm_granted else R.string.shizuku_request_permission),
                    onClick = {
                        if (!isPermissionGranted) {
                            com.hypercapsule.app.util.ShizukuManager.requestPermission(context, 1)
                        }
                    },
                    enabled = !isPermissionGranted,
                    variant = ButtonVariant.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (!isShizukuInstalled) {
                Spacer(modifier = Modifier.height(spacing.itemSpacing))
                HyperCapsuleGlassCard(
                    shape = shapes.squircleMedium,
                    tint = colors.accentError.copy(alpha = 0.1f),
                    borderColor = colors.accentError.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.shizuku_not_installed),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.accentError,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.sectionSpacing))
    }
}
