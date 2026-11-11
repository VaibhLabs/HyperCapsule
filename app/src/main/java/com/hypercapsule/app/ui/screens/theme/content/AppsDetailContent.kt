package com.hypercapsule.app.ui.screens.theme.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.hypercapsule.app.R
import com.hypercapsule.app.models.theme.AppThemeOverride
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleEmptyState
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSearchField
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.screens.theme.AppItem
import com.hypercapsule.app.ui.screens.theme.ShapeStyle
import com.hypercapsule.app.ui.screens.theme.ThemeViewModel
import com.hypercapsule.app.ui.screens.theme.getExpressiveShape

@Composable
fun AppsDetailContent(viewModel: ThemeViewModel) {
    val overrides by viewModel.appOverrides.collectAsState()
    val apps by viewModel.installedApps.collectAsState()
    var showAppPicker by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    val filteredOverrides = remember(overrides, searchQuery) {
        val list = overrides.entries.toList()
        if (searchQuery.isBlank()) {
            list
        } else {
            list.filter { (pkg, _) ->
                val label = apps.find { it.packageName == pkg }?.label ?: pkg
                label.contains(searchQuery, true) || pkg.contains(searchQuery, true)
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAppPicker = true },
                shape = shapes.squircleMedium,
                containerColor = colors.accentPrimary,
                contentColor = colors.textPrimary
            ) {
                Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.action_add))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(bottom = padding.calculateBottomPadding())
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.padding(horizontal = spacing.screenPadding, vertical = 8.dp)) {
                HyperCapsuleSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = stringResource(R.string.actions_search_hint)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (overrides.isEmpty() && searchQuery.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        HyperCapsuleEmptyState(
                            title = stringResource(R.string.apps_empty),
                            description = stringResource(R.string.app_customization_desc),
                            icon = Icons.Outlined.Apps
                        )
                    }
                } else if (filteredOverrides.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.actions_search_empty_fmt, searchQuery),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = spacing.screenPadding, end = spacing.screenPadding, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(spacing.itemSpacing),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredOverrides.size) { index ->
                            val (pkg, ov) = filteredOverrides[index]
                            val label = apps.find { it.packageName == pkg }?.label ?: pkg
                            val shape = getExpressiveShape(filteredOverrides.size, index, ShapeStyle.Large)

                            AppOverrideCard(
                                label = label,
                                packageName = pkg,
                                override = ov,
                                shape = shape,
                                onClick = { viewModel.loadAppForEditing(pkg, label) },
                                onDelete = { viewModel.removeAppOverride(pkg) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAppPicker) {
        AppSelectionSheet(
            apps = apps,
            onDismiss = { showAppPicker = false },
            onAppSelected = { app ->
                showAppPicker = false
                viewModel.loadAppForEditing(app.packageName, app.label)
            }
        )
    }
}

@Composable
fun AppOverrideCard(
    label: String,
    packageName: String,
    override: AppThemeOverride,
    shape: Shape,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val spacing = LocalHyperCapsuleSpacing.current

    HyperCapsuleGlassCard(
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 84.dp)
            .tactilePress(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(packageName = packageName, modifier = Modifier.size(44.dp))

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )

                val details = mutableListOf<String>()
                if (override.highlightColor != null) details.add("Color")
                if (override.iconShapeId != null) details.add("Shape")
                if (override.callConfig != null) details.add("Call")
                if (!override.actions.isNullOrEmpty()) details.add("${override.actions.size} Actions")

                Text(
                    text = if (details.isEmpty()) "Configured" else details.joinToString(" • "),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = colors.accentError.copy(alpha = 0.8f)
                )
            ) {
                Icon(Icons.Rounded.Delete, null)
            }

            Spacer(Modifier.width(4.dp))

            Icon(
                Icons.AutoMirrored.Rounded.ArrowForwardIos,
                null,
                modifier = Modifier.size(16.dp),
                tint = colors.textTertiary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionSheet(
    apps: List<AppItem>,
    onDismiss: () -> Unit,
    onAppSelected: (AppItem) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.backgroundElevated,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            Text(
                stringResource(R.string.apps_sheet_select_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.padding(horizontal = spacing.screenPadding)
            )

            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.padding(horizontal = spacing.screenPadding)) {
                HyperCapsuleSearchField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = stringResource(R.string.search_apps)
                )
            }

            Spacer(Modifier.height(16.dp))

            val filteredApps = remember(apps, searchQuery) {
                if (searchQuery.isBlank()) apps
                else apps.filter { it.label.contains(searchQuery, true) || it.packageName.contains(searchQuery, true) }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = spacing.screenPadding, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(spacing.itemSpacing),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredApps, key = { it.packageName }) { app ->
                    HyperCapsuleGlassCard(
                        shape = shapes.squircleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .tactilePress { onAppSelected(app) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIcon(packageName = app.packageName, modifier = Modifier.size(40.dp))
                            Spacer(Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.label,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = app.packageName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppIcon(packageName: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val shapes = LocalHyperCapsuleShapes.current
    val colors = LocalHyperCapsuleColors.current

    val drawable = remember(packageName) {
        try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    if (drawable != null) {
        Image(
            bitmap = drawable.toBitmap().asImageBitmap(),
            contentDescription = null,
            modifier = modifier.clip(shapes.squircleSmall),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .clip(shapes.squircleSmall)
                .background(colors.accentPrimary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Android, null, tint = colors.accentPrimary)
        }
    }
}
