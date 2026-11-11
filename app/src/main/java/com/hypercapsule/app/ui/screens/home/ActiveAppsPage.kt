package com.hypercapsule.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.AppInfo
import com.hypercapsule.app.ui.AppListViewModel
import com.hypercapsule.app.ui.components.AppListFilterSection
import com.hypercapsule.app.ui.components.AppListItem
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleEmptyState
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing

/**
 * Redesigned Liquid Glass Active Apps Page
 */
@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActiveAppsPage(
    apps: List<AppInfo>,
    isLoading: Boolean,
    viewModel: AppListViewModel,
    onConfig: (AppInfo) -> Unit,
    onSettingsClick: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    val searchQuery = viewModel.activeSearch.collectAsState().value
    val selectedCategory = viewModel.activeCategory.collectAsState().value
    val sortOption = viewModel.activeSort.collectAsState().value

    val isRefreshing = isLoading && apps.isNotEmpty()
    val pullState = rememberPullToRefreshState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        HyperCapsuleTopBar(
            title = stringResource(R.string.app_name),
            subtitle = stringResource(R.string.tab_active),
            actions = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(shapes.small)
                        .background(colors.surfaceElevatedGlass)
                        .tactilePress(onClick = onSettingsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.settings),
                        tint = colors.textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        AppListFilterSection(
            searchQuery = searchQuery,
            onSearchChange = { viewModel.activeSearch.value = it },
            selectedCategory = selectedCategory,
            onCategoryChange = { viewModel.activeCategory.value = it },
            sortOption = sortOption,
            onSortChange = { viewModel.activeSort.value = it }
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshApps() },
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
                if (apps.isEmpty() && isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.primary)
                    }
                } else if (apps.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        HyperCapsuleEmptyState(
                            title = stringResource(R.string.no_active_bridges),
                            description = stringResource(R.string.no_active_bridges_desc),
                            icon = Icons.Outlined.NotificationsOff
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(apps, key = { it.packageName }) { app ->
                            Column(modifier = Modifier.animateItem()) {
                                AppListItem(
                                    app = app,
                                    onToggle = { viewModel.toggleApp(app.packageName, false) },
                                    onSettingsClick = { onConfig(app) },
                                )
                                HorizontalDivider(
                                    color = colors.borderSubtle
                                )
                            }
                        }

                        if (isLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = spacing.extraLarge),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = colors.primary,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
