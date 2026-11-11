package com.hypercapsule.app.ui.screens.home

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.ToggleOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.AppInfo
import com.hypercapsule.app.ui.AppListViewModel
import com.hypercapsule.app.ui.components.AppConfigBottomSheet
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleBottomNavigation
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleNavItem
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleScaffold
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import com.hypercapsule.app.ui.screens.design.DesignScreen
import com.hypercapsule.app.ui.screens.design.SavedAppWidgetsScreen
import com.hypercapsule.app.ui.screens.design.WidgetConfigScreen
import com.hypercapsule.app.ui.screens.design.WidgetPickerScreen
import com.hypercapsule.app.ui.screens.theme.ThemeCreatorScreen
import com.hypercapsule.app.ui.screens.theme.ThemeManagerScreen

private enum class DesignRoute {
    DASHBOARD,
    WIDGET_LIST,
    THEME_MANAGER,
    THEME_CREATOR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AppListViewModel = viewModel(),
    onSettingsClick: () -> Unit,
    onNavConfigClick: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(1) }
    var designRoute by remember { mutableStateOf(DesignRoute.DASHBOARD) }
    var editingThemeId by remember { mutableStateOf<String?>(null) }

    var showWidgetPicker by remember { mutableStateOf(false) }
    var editingWidgetId by remember { mutableStateOf<Int?>(null) }
    var configApp by remember { mutableStateOf<AppInfo?>(null) }

    val activeApps by viewModel.activeAppsState.collectAsState()
    val libraryApps by viewModel.libraryAppsState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    if (configApp != null) BackHandler { configApp = null }
    if (editingWidgetId != null) BackHandler { editingWidgetId = null }
    if (showWidgetPicker) BackHandler { showWidgetPicker = false }

    if (selectedTab == 0 && designRoute != DesignRoute.DASHBOARD) {
        BackHandler {
            designRoute = when (designRoute) {
                DesignRoute.THEME_CREATOR -> {
                    editingThemeId = null
                    DesignRoute.THEME_MANAGER
                }
                else -> DesignRoute.DASHBOARD
            }
        }
    }

    val navItems = listOf(
        HyperCapsuleNavItem(
            title = stringResource(R.string.design),
            icon = Icons.Outlined.Brush,
            selectedIcon = Icons.Filled.Brush
        ),
        HyperCapsuleNavItem(
            title = stringResource(R.string.tab_active),
            icon = Icons.Outlined.ToggleOff,
            selectedIcon = Icons.Filled.ToggleOn
        ),
        HyperCapsuleNavItem(
            title = stringResource(R.string.tab_library),
            icon = Icons.Outlined.Apps,
            selectedIcon = Icons.Filled.Apps
        )
    )

    HyperCapsuleScaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = (selectedTab != 0 || designRoute == DesignRoute.DASHBOARD) && !showWidgetPicker && editingWidgetId == null,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                HyperCapsuleBottomNavigation(
                    items = navItems,
                    selectedIndex = selectedTab,
                    onItemSelected = { selectedTab = it }
                )
            }
        }
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            val prefs = remember { com.hypercapsule.app.data.AppPreferences(context) }
            val showWarning by prefs.featuredPermissionWarningFlow.collectAsState(initial = false)

            if (showWarning) {
                Box(modifier = Modifier.padding(horizontal = spacing.default, vertical = spacing.small)) {
                    HyperCapsuleGlassCard(
                        borderColor = colors.warning.copy(alpha = 0.4f),
                        backgroundColor = colors.warning.copy(alpha = 0.12f),
                        onClick = {
                            val intent = Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(shapes.medium)
                                    .background(colors.warning.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = colors.warning,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(spacing.default))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.featured_notifications_troubleshoot_title),
                                    style = typography.titleSmall,
                                    color = colors.warning
                                )
                                Text(
                                    text = stringResource(R.string.featured_notifications_troubleshoot_desc),
                                    style = typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> {
                        AnimatedContent(
                            targetState = designRoute,
                            transitionSpec = {
                                if (targetState.ordinal > initialState.ordinal) {
                                    slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it / 3 } + fadeOut()
                                } else {
                                    slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it / 3 } + fadeOut()
                                }
                            },
                            label = "DesignTabNav"
                        ) { route ->
                            when (route) {
                                DesignRoute.DASHBOARD -> {
                                    DesignScreen(
                                        onNavigateToWidgets = {
                                            designRoute = DesignRoute.WIDGET_LIST
                                        },
                                        onNavigateToThemes = {
                                            designRoute = DesignRoute.THEME_MANAGER
                                        },
                                        onEditTheme = { themeId ->
                                            editingThemeId = themeId
                                            designRoute = DesignRoute.THEME_CREATOR
                                        },
                                        onNavigateToThemeCreator = {
                                            editingThemeId = null
                                            designRoute = DesignRoute.THEME_CREATOR
                                        },
                                        onLaunchPicker = { showWidgetPicker = true },
                                        onSettingsClick = onSettingsClick
                                    )
                                }

                                DesignRoute.WIDGET_LIST -> {
                                    SavedAppWidgetsScreen(
                                        onBack = { designRoute = DesignRoute.DASHBOARD },
                                        onEditWidget = { id -> editingWidgetId = id },
                                        onAddMore = { showWidgetPicker = true }
                                    )
                                }

                                DesignRoute.THEME_MANAGER -> {
                                    ThemeManagerScreen(
                                        onBack = { designRoute = DesignRoute.DASHBOARD },
                                        onFindThemes = {
                                            val query = "HyperCapsule Theme"
                                            try {
                                                val intent = Intent(
                                                    Intent.ACTION_VIEW,
                                                    "market://search?q=$query&c=apps".toUri()
                                                )
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(intent)
                                            } catch (_: Exception) {
                                                val intent = Intent(
                                                    Intent.ACTION_VIEW,
                                                    "https://play.google.com/store/search?q=$query&c=apps".toUri()
                                                )
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                context.startActivity(intent)
                                            }
                                        },
                                        onCreateTheme = {
                                            editingThemeId = null
                                            designRoute = DesignRoute.THEME_CREATOR
                                        },
                                        onEditTheme = { id ->
                                            editingThemeId = id
                                            designRoute = DesignRoute.THEME_CREATOR
                                        }
                                    )
                                }

                                DesignRoute.THEME_CREATOR -> {
                                    ThemeCreatorScreen(
                                        editThemeId = editingThemeId,
                                        onBack = {
                                            designRoute = DesignRoute.THEME_MANAGER
                                            editingThemeId = null
                                        },
                                        onThemeCreated = {
                                            designRoute = DesignRoute.THEME_MANAGER
                                            editingThemeId = null
                                        }
                                    )
                                }
                            }
                        }
                    }

                    1 -> ActiveAppsPage(
                        apps = activeApps,
                        isLoading = isLoading,
                        viewModel = viewModel,
                        onConfig = { configApp = it },
                        onSettingsClick = onSettingsClick
                    )

                    2 -> LibraryPage(
                        apps = libraryApps,
                        isLoading = isLoading,
                        viewModel = viewModel,
                        onConfig = { configApp = it },
                        onSettingsClick = onSettingsClick
                    )
                }
            }
        }

        // --- OVERLAYS ---
        AnimatedVisibility(
            visible = showWidgetPicker,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(400)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(400)
            ) + fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            WidgetPickerScreen(
                onBack = { showWidgetPicker = false },
                onWidgetSelected = { newId ->
                    showWidgetPicker = false
                    editingWidgetId = newId
                }
            )
        }

        AnimatedVisibility(
            visible = editingWidgetId != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(400)
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(400)
            ) + fadeOut(),
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            if (editingWidgetId != null) {
                WidgetConfigScreen(
                    widgetId = editingWidgetId!!,
                    onBack = { editingWidgetId = null }
                )
            }
        }

        if (configApp != null) {
            val currentConfigApp = configApp!!
            AppConfigBottomSheet(
                app = currentConfigApp,
                viewModel = viewModel,
                onDismiss = { configApp = null },
                onNavConfigClick = {
                    onNavConfigClick(currentConfigApp.packageName)
                    configApp = null
                }
            )
        }
    }
}
