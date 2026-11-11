package com.hypercapsule.app.ui.screens.theme.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.CloudDownload
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Timer
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.models.IslandConfig
import com.hypercapsule.app.models.NotificationType
import com.hypercapsule.app.ui.components.IslandSettingsControl
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.screens.theme.CreatorOptionCard
import com.hypercapsule.app.ui.screens.theme.CreatorRoute
import com.hypercapsule.app.ui.screens.theme.ShapeStyle
import com.hypercapsule.app.ui.screens.theme.getExpressiveShape
import kotlinx.coroutines.launch

@Composable
fun BehaviourMenuContent(onNavigate: (CreatorRoute) -> Unit) {
    val spacing = LocalHyperCapsuleSpacing.current

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.screenPadding)
    ) {
        CreatorOptionCard(
            title = stringResource(R.string.engine),
            subtitle = stringResource(R.string.engine_desc),
            icon = Icons.Outlined.Memory,
            shape = getExpressiveShape(3, 0, ShapeStyle.Large),
            onClick = { onNavigate(CreatorRoute.BEHAVIOR_ENGINE) }
        )
        Spacer(Modifier.height(spacing.itemSpacing))
        CreatorOptionCard(
            title = stringResource(R.string.island_behavior),
            subtitle = stringResource(R.string.island_behavior_desc),
            icon = Icons.Outlined.DisplaySettings,
            shape = getExpressiveShape(3, 1, ShapeStyle.Large),
            onClick = { onNavigate(CreatorRoute.BEHAVIOR_ISLAND) }
        )
        Spacer(Modifier.height(spacing.itemSpacing))
        CreatorOptionCard(
            title = stringResource(R.string.active_notifications_title),
            subtitle = stringResource(R.string.select_triggered_events),
            icon = Icons.Outlined.NotificationsActive,
            shape = getExpressiveShape(3, 2, ShapeStyle.Large),
            onClick = { onNavigate(CreatorRoute.BEHAVIOR_TYPES) }
        )
    }
}

@Composable
fun ThemeBehaviourContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context) }
    val spacing = LocalHyperCapsuleSpacing.current

    val globalConfig by preferences.globalConfigFlow.collectAsState(initial = IslandConfig(
        isFloat = false,
        isShowShade = false,
        timeout = 10
    ))

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.screenPadding)
    ) {
        IslandSettingsControl(
            config = globalConfig,
            onUpdate = { newConfig ->
                scope.launch { preferences.updateGlobalConfig(newConfig) }
            }
        )
    }
}

@Composable
fun NotificationTypesContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context) }
    val colors = LocalHyperCapsuleColors.current
    val spacing = LocalHyperCapsuleSpacing.current

    val enabledTypesStr by preferences.globalNotificationTypesFlow.collectAsState(
        initial = NotificationType.entries.map { it.name }.toSet()
    )

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.screenPadding)
    ) {
        HyperCapsuleSectionHeader(
            title = stringResource(R.string.active_triggers),
            icon = Icons.Outlined.NotificationsActive
        )
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.active_triggers_global_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textSecondary
        )
        Spacer(Modifier.height(spacing.itemSpacing))

        NotificationType.entries.forEachIndexed { index, type ->
            val (icon, subtitle) = when (type) {
                NotificationType.STANDARD -> Icons.AutoMirrored.Outlined.Message to stringResource(R.string.type_standard_desc)
                NotificationType.PROGRESS -> Icons.Outlined.HourglassEmpty to stringResource(R.string.type_progress_desc)
                NotificationType.DOWNLOAD -> Icons.Outlined.CloudDownload to stringResource(R.string.type_download_desc)
                NotificationType.MEDIA -> Icons.Outlined.MusicNote to stringResource(R.string.type_media_desc)
                NotificationType.NAVIGATION -> Icons.Outlined.Map to stringResource(R.string.type_nav_desc)
                NotificationType.CALL -> Icons.Outlined.Call to stringResource(R.string.type_call_desc)
                NotificationType.TIMER -> Icons.Outlined.Timer to stringResource(R.string.type_timer_desc)
                NotificationType.MESSAGE -> Icons.AutoMirrored.Outlined.Message to stringResource(R.string.type_message_desc)
            }

            val shape = when {
                NotificationType.entries.size == 1 -> RoundedCornerShape(24.dp)
                index == 0 -> RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                index == NotificationType.entries.size - 1 -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 24.dp, bottomEnd = 24.dp)
                else -> RoundedCornerShape(4.dp)
            }

            SettingsToggleCard(
                title = stringResource(type.labelRes),
                subtitle = subtitle,
                icon = icon,
                checked = enabledTypesStr.contains(type.name),
                onCheckedChange = { isChecked ->
                    scope.launch {
                        preferences.updateGlobalNotificationType(type, isChecked)
                    }
                },
                shape = shape
            )

            if (index < NotificationType.entries.size - 1) {
                Spacer(modifier = Modifier.height(2.dp))
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
fun SettingsToggleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    shape: Shape
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current

    HyperCapsuleGlassCard(
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .tactilePress { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(shapes.squircleSmall)
                    .background(
                        if (checked) colors.accentPrimary.copy(alpha = 0.15f)
                        else colors.cardBorder.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) colors.accentPrimary else colors.textSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            HyperCapsuleSwitch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
