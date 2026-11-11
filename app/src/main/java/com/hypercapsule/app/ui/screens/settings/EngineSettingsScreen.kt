package com.hypercapsule.app.ui.screens.settings

import android.content.Intent
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AltRoute
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Memory
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
import com.hypercapsule.app.data.theme.ThemeRepository
import com.hypercapsule.app.service.NotificationReaderService
import com.hypercapsule.app.ui.components.EngineOptionCard
import com.hypercapsule.app.ui.components.EnginePreview
import com.hypercapsule.app.ui.designsystem.components.ButtonVariant
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleScaffold
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import kotlinx.coroutines.launch

@Composable
fun EngineSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val repo = remember { ThemeRepository(context) }
    val scope = rememberCoroutineScope()
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    // 1. Observe both the Global Preference and the Active Theme
    val defaultEnginePref by prefs.useNativeLiveUpdates.collectAsState(initial = false)
    val activeTheme by repo.activeTheme.collectAsState(initial = null)

    // 2. Calculate which engine is ACTUALLY being used right now
    val isCustomTheme = activeTheme != null && activeTheme!!.id.isNotEmpty()
    val isNative = if (isCustomTheme) {
        activeTheme!!.global.useNativeLiveUpdates ?: defaultEnginePref
    } else {
        defaultEnginePref
    }

    // 3. Handle Engine Changes intelligently based on active state
    val onEngineChange = { useNative: Boolean ->
        scope.launch {
            if (isCustomTheme) {
                val currentTheme = activeTheme!!
                val updatedGlobal = currentTheme.global.copy(useNativeLiveUpdates = useNative)
                val updatedTheme = currentTheme.copy(global = updatedGlobal)
                repo.saveTheme(updatedTheme)
                repo.activateTheme(updatedTheme.id)
            } else {
                prefs.setUseNativeLiveUpdates(useNative)
            }

            val intent = Intent(context, NotificationReaderService::class.java).apply {
                action = NotificationReaderService.ACTION_RELOAD_THEME
            }
            context.startService(intent)
        }
    }

    HyperCapsuleScaffold(
        topBar = {
            HyperCapsuleTopBar(
                title = stringResource(R.string.engine),
                onBack = onBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = spacing.screenPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            HyperCapsuleSectionHeader(
                title = stringResource(R.string.engine_preview_title),
                icon = Icons.Outlined.AltRoute
            )
            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            EnginePreview(isNative = isNative)

            Spacer(modifier = Modifier.height(spacing.sectionSpacing))

            HyperCapsuleSectionHeader(
                title = stringResource(R.string.engine_section_design),
                icon = Icons.Outlined.Engineering
            )
            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            EngineOptionCard(
                title = stringResource(R.string.engine_xiaomi_title),
                description = stringResource(R.string.engine_xiaomi_desc),
                isSelected = !isNative,
                onClick = { onEngineChange(false) }
            )

            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            EngineOptionCard(
                title = stringResource(R.string.engine_native_title),
                description = stringResource(R.string.engine_native_desc),
                isSelected = isNative,
                onClick = { onEngineChange(true) }
            )

            if (!isNative) {
                Spacer(modifier = Modifier.height(spacing.sectionSpacing))

                val isShizukuInstalled = remember { com.hypercapsule.app.util.ShizukuManager.isShizukuInstalled(context) }
                val isShizukuRunning by com.hypercapsule.app.util.ShizukuManager.isShizukuRunning.collectAsState()
                val isPermissionGranted by com.hypercapsule.app.util.ShizukuManager.isPermissionGranted.collectAsState()
                val isWorkaroundEnabled by prefs.isShizukuWorkaroundEnabled.collectAsState(initial = false)

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
}
