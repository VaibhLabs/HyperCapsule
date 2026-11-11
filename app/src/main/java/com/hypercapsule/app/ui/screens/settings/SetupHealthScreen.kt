package com.hypercapsule.app.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleStatusIndicator
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleStatusType
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import com.hypercapsule.app.util.DeviceUtils
import com.hypercapsule.app.util.XiaomiNotificationHelper
import com.hypercapsule.app.util.isNotificationServiceEnabled
import com.hypercapsule.app.util.isPostNotificationsEnabled
import com.hypercapsule.app.util.openAutoStartSettings
import com.hypercapsule.app.util.openBatterySettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupHealthScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    // --- STATE ---
    var isListenerGranted by remember { mutableStateOf(isNotificationServiceEnabled(context)) }
    var isPostGranted by remember { mutableStateOf(isPostNotificationsEnabled(context)) }
    var isOverlayGranted by remember { mutableStateOf(Settings.canDrawOverlays(context)) }
    var isBatteryOptimized by remember { mutableStateOf(isIgnoringBatteryOptimizations(context)) }
    var isFeaturedGranted by remember { mutableStateOf(false) }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isListenerGranted = isNotificationServiceEnabled(context)
                isPostGranted = isPostNotificationsEnabled(context)
                isOverlayGranted = Settings.canDrawOverlays(context)
                isBatteryOptimized = isIgnoringBatteryOptimizations(context)
                isFeaturedGranted = XiaomiNotificationHelper.hasFocusPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // --- DEVICE CHECKS ---
    val isXiaomi = DeviceUtils.isXiaomi
    val isCompatibleOS = DeviceUtils.isCompatibleOS()
    val osVersionString = DeviceUtils.getHyperOSVersion()
    val isCN = DeviceUtils.isCNRom
    val deviceModel = android.os.Build.MODEL

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.system_setup),
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = spacing.default, vertical = spacing.small)
            ) {
                // Info Header Card
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
                                .size(40.dp)
                                .clip(shapes.medium)
                                .background(colors.primary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Info, null, tint = colors.primary, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(spacing.default))
                        Text(
                            text = stringResource(R.string.app_health_desc),
                            style = typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    }
                }

                // 1. SYSTEM COMPATIBILITY
                HyperCapsuleSectionHeader(title = stringResource(R.string.setup_health_title))
                HyperCapsuleGlassCard(contentPadding = 0.dp, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        StatusRow(
                            title = android.os.Build.MANUFACTURER.uppercase(),
                            subtitle = deviceModel,
                            isSuccess = isXiaomi,
                            icon = Icons.Default.Smartphone
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                        StatusRow(
                            title = stringResource(R.string.system_version),
                            subtitle = osVersionString,
                            isSuccess = isCompatibleOS,
                            icon = if (isCompatibleOS) Icons.Default.CheckCircle else Icons.Default.Warning
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.large))

                // 2. PERMISSIONS
                HyperCapsuleSectionHeader(title = stringResource(R.string.req_permissions))
                HyperCapsuleGlassCard(contentPadding = 0.dp, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        HealthItem(
                            title = stringResource(R.string.notif_access),
                            subtitle = stringResource(R.string.notif_access_desc),
                            icon = Icons.Default.NotificationsActive,
                            isGranted = isListenerGranted,
                            onClick = { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                        HealthItem(
                            title = stringResource(R.string.show_island),
                            subtitle = stringResource(R.string.perm_post_desc),
                            icon = Icons.Default.Visibility,
                            isGranted = isPostGranted,
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    intent.data = "package:${context.packageName}".toUri()
                                    context.startActivity(intent)
                                } catch (_: Exception) { }
                            }
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                        HealthItem(
                            title = stringResource(R.string.perm_display_title),
                            subtitle = stringResource(R.string.perm_display_onboard_desc),
                            icon = Icons.Default.Layers,
                            isGranted = isOverlayGranted,
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                    intent.data = "package:${context.packageName}".toUri()
                                    context.startActivity(intent)
                                } catch (_: Exception) { }
                            }
                        )

                        val isSupported = XiaomiNotificationHelper.isSupportIsland()
                        if (isXiaomi && isSupported) {
                            HorizontalDivider(color = colors.borderSubtle)
                            HealthItem(
                                title = stringResource(R.string.xiaomi_featured_notifications),
                                subtitle = stringResource(R.string.featured_notifications_open_settings),
                                icon = Icons.Default.Smartphone,
                                isGranted = isFeaturedGranted,
                                onClick = {
                                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.large))

                // 3. OPTIMIZATION
                HyperCapsuleSectionHeader(title = stringResource(R.string.device_optimization))
                HyperCapsuleGlassCard(contentPadding = 0.dp, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        HealthItem(
                            title = stringResource(R.string.xiaomi_autostart),
                            subtitle = stringResource(R.string.autostart_manual_check),
                            icon = Icons.Default.RestartAlt,
                            isGranted = false,
                            forceAction = true,
                            onClick = { openAutoStartSettings(context) }
                        )
                        HorizontalDivider(color = colors.borderSubtle)
                        HealthItem(
                            title = stringResource(R.string.battery_unrestricted),
                            subtitle = stringResource(R.string.battery_desc),
                            icon = Icons.Default.BatteryAlert,
                            isGranted = isBatteryOptimized,
                            onClick = { openBatterySettings(context) }
                        )
                    }
                }

                // 4. WARNINGS
                if (isCN && isXiaomi) {
                    Spacer(Modifier.height(spacing.large))
                    HyperCapsuleGlassCard(
                        backgroundColor = colors.error.copy(alpha = 0.12f),
                        borderColor = colors.error.copy(alpha = 0.35f)
                    ) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Warning, null, tint = colors.error, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(spacing.default))
                            Column {
                                Text(
                                    text = stringResource(R.string.warning_cn_rom_title),
                                    style = typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.error
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.warning_cn_rom_desc),
                                    style = typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.medium))
                Text(
                    text = stringResource(R.string.recents_note),
                    style = typography.labelSmall,
                    color = colors.textTertiary,
                    modifier = Modifier.padding(horizontal = spacing.small)
                )
                Spacer(modifier = Modifier.height(spacing.huge))
            }
        }
    }
}

@Composable
fun StatusRow(
    title: String,
    subtitle: String,
    isSuccess: Boolean,
    icon: ImageVector
) {
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
                .size(40.dp)
                .clip(shapes.medium)
                .background(if (isSuccess) colors.success.copy(alpha = 0.12f) else colors.error.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSuccess) colors.success else colors.error,
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

        HyperCapsuleStatusIndicator(
            text = if (isSuccess) "Compatible" else "Warning",
            statusType = if (isSuccess) HyperCapsuleStatusType.ACTIVE else HyperCapsuleStatusType.WARNING,
            pulse = false
        )
    }
}

@Composable
fun HealthItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isGranted: Boolean,
    forceAction: Boolean = false,
    onClick: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current
    val isActionable = !isGranted || forceAction

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isActionable) Modifier.tactilePress(onClick = onClick) else Modifier)
            .padding(horizontal = spacing.default, vertical = spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(shapes.medium)
                .background(if (isGranted) colors.success.copy(alpha = 0.12f) else colors.warning.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) colors.success else colors.warning,
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
            val desc = if (isGranted && !forceAction) stringResource(R.string.status_active) else subtitle
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = typography.bodySmall,
                color = colors.textSecondary
            )
        }

        if (isGranted && !forceAction) {
            HyperCapsuleStatusIndicator(
                text = "Active",
                statusType = HyperCapsuleStatusType.ACTIVE,
                pulse = false
            )
        } else {
            Icon(
                imageVector = if (forceAction) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Error,
                contentDescription = null,
                tint = if (forceAction) colors.textTertiary else colors.warning,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}
