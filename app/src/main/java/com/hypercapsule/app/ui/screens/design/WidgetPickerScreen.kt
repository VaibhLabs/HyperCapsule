package com.hypercapsule.app.ui.screens.design

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.core.graphics.drawable.toBitmap
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.data.widget.WidgetManager
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSearchField
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSegmentedControl
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class WidgetAppGroup(
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?,
    val widgets: List<AppWidgetProviderInfo>,
    val isExpanded: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPickerScreen(
    onBack: () -> Unit,
    onWidgetSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context.applicationContext) }

    val favorites by preferences.favoriteWidgetAppsFlow.collectAsState(initial = emptySet())

    var allGroups by remember { mutableStateOf<List<WidgetAppGroup>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var tabIndex by remember { mutableIntStateOf(0) } // 0 = Recommended, 1 = All
    var pendingWidgetId by remember { mutableIntStateOf(-1) }

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    val bindLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && pendingWidgetId != -1) {
            onWidgetSelected(pendingWidgetId)
        } else if (pendingWidgetId != -1) {
            WidgetManager.deleteId(context, pendingWidgetId)
            pendingWidgetId = -1
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val manager = AppWidgetManager.getInstance(context)
            val providers = manager.installedProviders
            val grouped = providers.groupBy { it.provider.packageName }

            val uiGroups = grouped.mapNotNull { (pkg, list) ->
                try {
                    val appName = context.packageManager.getApplicationLabel(context.packageManager.getApplicationInfo(pkg, 0)).toString()
                    val icon = context.packageManager.getApplicationIcon(pkg)
                    WidgetAppGroup(pkg, appName, icon, list)
                } catch (_: Exception) { null }
            }.sortedBy { it.appName }

            allGroups = uiGroups
        }
    }

    val displayedGroups = allGroups.mapNotNull { group ->
        val filteredWidgets = if (tabIndex == 0) {
            group.widgets.filter { w ->
                w.targetCellWidth == 4 && (w.targetCellHeight == 1 || w.targetCellHeight == 2)
            }
        } else group.widgets

        if (filteredWidgets.isNotEmpty()) group.copy(widgets = filteredWidgets) else null
    }.filter {
        if (searchQuery.isNotEmpty()) it.appName.contains(searchQuery, ignoreCase = true) else true
    }.map {
        if (searchQuery.isNotEmpty()) it.copy(isExpanded = true) else it
    }.sortedWith(
        compareByDescending<WidgetAppGroup> { favorites.contains(it.packageName) }
            .thenBy { it.appName }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.widget_picker_title),
                subtitle = "BETA",
                onBackClick = onBack
            )

            Column(modifier = Modifier.padding(horizontal = spacing.default, vertical = spacing.extraSmall)) {
                HyperCapsuleSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = stringResource(R.string.search_apps_placeholder)
                )

                Spacer(modifier = Modifier.height(spacing.small))

                HyperCapsuleSegmentedControl(
                    items = listOf(stringResource(R.string.recommended), stringResource(R.string.all)),
                    selectedIndex = tabIndex,
                    onItemSelected = { tabIndex = it }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = spacing.default, end = spacing.default, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                items(displayedGroups, key = { it.packageName }) { group ->
                    var expanded by remember(group.packageName, searchQuery) { mutableStateOf(group.isExpanded) }
                    val isFavorite = favorites.contains(group.packageName)

                    Box(modifier = Modifier.animateItem()) {
                        AppGroupItem(
                            group = group,
                            isExpanded = expanded,
                            isFavorite = isFavorite,
                            onToggle = { expanded = !expanded },
                            onFavoriteToggle = {
                                scope.launch { preferences.toggleFavoriteWidgetApp(group.packageName, !isFavorite) }
                            },
                            onSelectWidget = { provider ->
                                val widgetId = WidgetManager.allocateId(context)
                                val allowed = WidgetManager.bindWidget(context, widgetId, provider.provider)

                                if (allowed) {
                                    onWidgetSelected(widgetId)
                                } else {
                                    pendingWidgetId = widgetId
                                    val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
                                    }
                                    bindLauncher.launch(intent)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppGroupItem(
    group: WidgetAppGroup,
    isExpanded: Boolean,
    isFavorite: Boolean,
    onToggle: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onSelectWidget: (AppWidgetProviderInfo) -> Unit
) {
    val numWidget = pluralStringResource(R.plurals.widget_count_fmt, group.widgets.size, group.widgets.size)
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

                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Rounded.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) colors.primary else colors.textTertiary
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

                    group.widgets.forEachIndexed { index, widget ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = colors.borderSubtle.copy(alpha = 0.5f),
                                modifier = Modifier.padding(horizontal = spacing.huge)
                            )
                        }
                        WidgetChildItem(widget, onSelectWidget)
                    }

                    Spacer(Modifier.height(spacing.small))
                }
            }
        }
    }
}

@Composable
fun WidgetChildItem(
    info: AppWidgetProviderInfo,
    onClick: (AppWidgetProviderInfo) -> Unit
) {
    val context = LocalContext.current
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val label = info.loadLabel(context.packageManager)
    val cols = info.targetCellWidth
    val rows = info.targetCellHeight
    val dims = "$cols × $rows"

    var previewBitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
    LaunchedEffect(info) {
        withContext(Dispatchers.IO) {
            try {
                val d = info.loadPreviewImage(context, 0) ?: info.loadIcon(context, 0)
                if (d != null) {
                    val w = d.intrinsicWidth.takeIf { it > 0 } ?: 200
                    val h = d.intrinsicHeight.takeIf { it > 0 } ?: 200
                    val safeW = w.coerceIn(100, 1000)
                    val safeH = h.coerceIn(100, 1000)
                    previewBitmap = d.toBitmap(width = safeW, height = safeH).asImageBitmap()
                }
            } catch (_: Exception) { }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(info) }
            .padding(spacing.default),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(shapes.large)
                .background(Color.Black)
                .border(1.dp, colors.borderHighlight, shapes.large)
                .padding(spacing.large),
            contentAlignment = Alignment.Center
        ) {
            if (previewBitmap != null) {
                Image(
                    bitmap = previewBitmap!!,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
                    alignment = Alignment.Center
                )
            } else {
                Icon(
                    Icons.Outlined.Widgets,
                    null,
                    modifier = Modifier.size(48.dp),
                    tint = colors.textTertiary
                )
            }
        }

        Spacer(Modifier.height(spacing.medium))

        Text(
            text = label,
            style = typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = colors.textPrimary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = dims,
            style = typography.bodySmall,
            color = colors.textSecondary
        )
    }
}
