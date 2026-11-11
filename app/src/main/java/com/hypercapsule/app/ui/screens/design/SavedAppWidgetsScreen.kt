package com.hypercapsule.app.ui.screens.design

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.edit
import androidx.core.content.pm.PackageInfoCompat
import androidx.core.graphics.drawable.toBitmap
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.data.widget.WidgetManager
import com.hypercapsule.app.models.WidgetSize
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleEmptyState
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSearchField
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSegmentedControl
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SavedWidgetGroup(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?,
    val widgetIds: List<Int>,
    val isExpanded: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SavedAppWidgetsScreen(
    onBack: () -> Unit,
    onEditWidget: (Int) -> Unit,
    onAddMore: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context.applicationContext) }

    val favorites by preferences.favoriteWidgetAppsFlow.collectAsState(initial = emptySet())
    val savedIds by preferences.savedWidgetIdsFlow.collectAsState(initial = null)

    var allGroups by remember { mutableStateOf<List<SavedWidgetGroup>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var tabIndex by remember { mutableIntStateOf(1) } // 0 = Favorites, 1 = All

    var showPermissionReminder by remember { mutableStateOf(false) }
    val refreshTrigger = remember { mutableIntStateOf(0) }

    val pullState = rememberPullToRefreshState()
    val isRefreshing = isLoading && allGroups.isNotEmpty()

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    LaunchedEffect(Unit) {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val currentVersion = PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
        val legacyPrefs = context.getSharedPreferences("hyperbridge_internal", Context.MODE_PRIVATE)
        val sharedPrefs = context.getSharedPreferences("hypercapsule_internal", Context.MODE_PRIVATE)
        val lastVersion = sharedPrefs.getInt("last_seen_version", legacyPrefs.getInt("last_seen_version", currentVersion))

        if (lastVersion in 1 until currentVersion) {
            showPermissionReminder = true
        }
        sharedPrefs.edit { putInt("last_seen_version", currentVersion) }
    }

    LaunchedEffect(savedIds, refreshTrigger.intValue) {
        if (savedIds == null) return@LaunchedEffect

        isLoading = true
        withContext(Dispatchers.IO) {
            val groupsMap = mutableMapOf<String, MutableList<Int>>()
            savedIds!!.forEach { id ->
                val info = WidgetManager.getWidgetInfo(context, id)
                val pkg = info?.provider?.packageName ?: return@forEach
                groupsMap.getOrPut(pkg) { mutableListOf() }.add(id)
            }

            val uiGroups = groupsMap.map { (pkg, ids) ->
                val appName = try {
                    val appInfo = context.packageManager.getApplicationInfo(pkg, 0)
                    context.packageManager.getApplicationLabel(appInfo).toString()
                } catch (_: Exception) { pkg }

                val icon = try {
                    context.packageManager.getApplicationIcon(pkg)
                } catch (_: Exception) { null }

                SavedWidgetGroup(pkg, appName, icon, ids)
            }.sortedBy { it.appName }

            if (uiGroups.isEmpty()) delay(300)
            allGroups = uiGroups
        }
        isLoading = false
    }

    val displayedGroups = remember(allGroups, searchQuery, tabIndex, favorites) {
        var filtered = allGroups
        if (tabIndex == 0) {
            filtered = filtered.filter { favorites.contains(it.packageName) }
        }
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.appName.contains(searchQuery, ignoreCase = true)
            }.map { it.copy(isExpanded = true) }
        }
        filtered
    }

    if (showPermissionReminder) {
        AlertDialog(
            onDismissRequest = { showPermissionReminder = false },
            containerColor = colors.surfaceElevated,
            shape = shapes.large,
            title = { Text(stringResource(R.string.permission_reminder_title), color = colors.textPrimary) },
            text = { Text(stringResource(R.string.permission_reminder_desc), color = colors.textSecondary) },
            confirmButton = {
                HyperCapsuleActionButton(
                    text = stringResource(R.string.got_it),
                    onClick = { showPermissionReminder = false },
                    isPrimary = true
                )
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.saved_widgets_title),
                subtitle = "BETA",
                onBackClick = onBack,
                actions = {
                    if (allGroups.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(shapes.small)
                                .background(colors.error.copy(alpha = 0.15f))
                                .border(1.dp, colors.error.copy(alpha = 0.3f), shapes.small)
                                .tactilePress(onClick = {
                                    val intent = Intent(context, com.hypercapsule.app.service.WidgetOverlayService::class.java).apply {
                                        action = "ACTION_KILL_ALL_WIDGETS"
                                    }
                                    context.startService(intent)
                                }),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Block,
                                contentDescription = stringResource(R.string.kill_all_widgets),
                                tint = colors.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            )

            // Search Bar & Filter Controls
            Column(modifier = Modifier.padding(horizontal = spacing.default, vertical = spacing.extraSmall)) {
                HyperCapsuleSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = stringResource(R.string.search_apps_placeholder)
                )

                Spacer(modifier = Modifier.height(spacing.small))

                HyperCapsuleSegmentedControl(
                    items = listOf(stringResource(R.string.favorites), stringResource(R.string.all)),
                    selectedIndex = tabIndex,
                    onItemSelected = { tabIndex = it }
                )
            }

            // Widget Groups List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { refreshTrigger.intValue++ },
                    state = pullState,
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                    indicator = {
                        PullToRefreshDefaults.LoadingIndicator(
                            state = pullState,
                            isRefreshing = isRefreshing,
                            modifier = Modifier.align(Alignment.TopCenter),
                        )
                    }
                ) {
                    if (displayedGroups.isEmpty() && isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = colors.primary)
                        }
                    } else if (displayedGroups.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            HyperCapsuleEmptyState(
                                title = stringResource(R.string.no_saved_widgets),
                                description = "",
                                icon = Icons.Outlined.Widgets,
                                actionButtonText = stringResource(R.string.design_add_new),
                                onActionClick = onAddMore
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(start = spacing.default, end = spacing.default, bottom = 100.dp),
                            verticalArrangement = Arrangement.spacedBy(spacing.medium)
                        ) {
                            items(displayedGroups, key = { it.packageName }) { group ->
                                var expanded by remember(group.packageName, searchQuery) { mutableStateOf(group.isExpanded) }

                                Box(modifier = Modifier.animateItem()) {
                                    SavedGroupItem(
                                        group = group,
                                        isExpanded = expanded,
                                        onToggle = { expanded = !expanded },
                                        onEdit = onEditWidget,
                                        onDelete = { widgetId ->
                                            val killIntent = Intent(context, com.hypercapsule.app.service.WidgetOverlayService::class.java).apply {
                                                action = "ACTION_KILL_WIDGET"
                                                putExtra("WIDGET_ID", widgetId)
                                            }
                                            context.startService(killIntent)

                                            scope.launch {
                                                preferences.removeWidgetId(widgetId)
                                                refreshTrigger.intValue++
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = onAddMore,
            containerColor = colors.primary,
            contentColor = Color.Black,
            shape = shapes.large,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(spacing.default)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Widget")
        }
    }
}

@Composable
fun SavedGroupItem(
    group: SavedWidgetGroup,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onEdit: (Int) -> Unit,
    onDelete: (Int) -> Unit
) {
    val numWidget = pluralStringResource(R.plurals.widget_count_fmt, group.widgetIds.size, group.widgetIds.size)
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    HyperCapsuleGlassCard(
        shape = shapes.large,
        backgroundColor = if (isExpanded) colors.surfaceElevatedGlass else colors.surfaceCardGlass,
        contentPadding = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = spacing.default, vertical = spacing.default),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (group.appIcon != null) {
                    Image(
                        bitmap = group.appIcon.toBitmap().asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(shapes.medium)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(shapes.medium)
                            .background(colors.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Widgets,
                            null,
                            modifier = Modifier.size(22.dp),
                            tint = colors.primary
                        )
                    }
                }

                Spacer(Modifier.width(spacing.default))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.appName,
                        style = typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = numWidget,
                        style = typography.bodySmall,
                        color = colors.textSecondary
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = colors.textSecondary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    expandFrom = Alignment.Top
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    shrinkTowards = Alignment.Top
                ) + fadeOut()
            ) {
                Column {
                    HorizontalDivider(
                        color = colors.borderSubtle,
                        modifier = Modifier.padding(horizontal = spacing.default)
                    )

                    group.widgetIds.forEachIndexed { index, widgetId ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = colors.borderSubtle.copy(alpha = 0.5f),
                                modifier = Modifier.padding(horizontal = spacing.huge)
                            )
                        }
                        SavedWidgetChildItem(
                            widgetId = widgetId,
                            onEdit = { onEdit(widgetId) },
                            onDelete = { onDelete(widgetId) }
                        )
                    }
                    Spacer(Modifier.height(spacing.small))
                }
            }
        }
    }
}

@Composable
fun SavedWidgetChildItem(
    widgetId: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val preferences = remember { AppPreferences(context.applicationContext) }
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val config by preferences.getWidgetConfigFlow(widgetId).collectAsState(initial = null)

    val viewHeightDp = when (config?.size) {
        WidgetSize.SMALL -> 100
        WidgetSize.MEDIUM -> 180
        WidgetSize.LARGE -> 280
        WidgetSize.XLARGE -> 380
        else -> 180
    }

    val containerHeight = viewHeightDp.dp + 40.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(spacing.default)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.widget_id_fmt, widgetId),
                style = typography.labelMedium,
                color = colors.primary,
                fontWeight = FontWeight.Bold
            )

            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(shapes.small)
                    .background(colors.error.copy(alpha = 0.12f))
                    .tactilePress(onClick = onDelete),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = colors.error,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.height(spacing.small))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(containerHeight)
                .clip(shapes.large)
                .background(Color.Black)
                .border(1.dp, colors.borderHighlight, shapes.large),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    val wrapper = FrameLayout(ctx)
                    val hostView = WidgetManager.createPreview(ctx, widgetId)
                    if (hostView != null) {
                        val info = WidgetManager.getWidgetInfo(ctx, widgetId)
                        hostView.setAppWidget(widgetId, info)
                        wrapper.addView(hostView)

                        val density = ctx.resources.displayMetrics.density
                        val w = (300 * density).toInt()
                        val h = (viewHeightDp * density).toInt()

                        hostView.measure(
                            View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.AT_MOST)
                        )
                        hostView.layout(0, 0, hostView.measuredWidth, hostView.measuredHeight)
                    }
                    wrapper
                },
                modifier = Modifier
                    .padding(spacing.default)
                    .fillMaxSize()
            )
        }

        Spacer(Modifier.height(spacing.small))

        HyperCapsuleActionButton(
            text = stringResource(R.string.show_on_island),
            icon = Icons.Default.PlayArrow,
            onClick = {
                val intent = Intent(context, com.hypercapsule.app.service.WidgetOverlayService::class.java).apply {
                    action = "ACTION_TEST_WIDGET"
                    putExtra("WIDGET_ID", widgetId)
                }
                context.startService(intent)
            },
            isPrimary = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
