package com.hypercapsule.app.ui.screens.theme.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.hypercapsule.app.R
import com.hypercapsule.app.models.theme.ActionConfig
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleEmptyState
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSearchField
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.screens.theme.ShapeStyle
import com.hypercapsule.app.ui.screens.theme.getExpressiveShape

@Composable
fun ActionsDetailContent(
    actions: Map<String, ActionConfig>,
    onUpdateAction: (String, ActionConfig) -> Unit,
    onRemoveAction: (String) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    var selectedKeyword by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    val filteredActions = remember(actions, searchQuery) {
        if (searchQuery.isBlank()) {
            actions
        } else {
            actions.filterKeys { it.contains(searchQuery, ignoreCase = true) }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedKeyword = null
                    showSheet = true
                },
                shape = shapes.squircleMedium,
                containerColor = colors.accentPrimary,
                contentColor = colors.textPrimary
            ) {
                Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.theme_manager_cd_add))
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
                if (actions.isEmpty() && searchQuery.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        HyperCapsuleEmptyState(
                            title = stringResource(R.string.actions_empty),
                            description = stringResource(R.string.actions_empty_desc),
                            icon = Icons.Outlined.TouchApp
                        )
                    }
                } else if (filteredActions.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.actions_search_empty_fmt, searchQuery),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                } else {
                    val actionsList = filteredActions.toList()
                    LazyColumn(
                        contentPadding = PaddingValues(start = spacing.screenPadding, end = spacing.screenPadding, bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(spacing.itemSpacing),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(actionsList.size) { index ->
                            val (keyword, config) = actionsList[index]
                            val shape = getExpressiveShape(actionsList.size, index, ShapeStyle.Large)

                            ActionItemCard(
                                keyword = keyword,
                                config = config,
                                shape = shape,
                                onClick = {
                                    selectedKeyword = keyword
                                    showSheet = true
                                },
                                onDelete = { onRemoveAction(keyword) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showSheet) {
        val initialConfig = selectedKeyword?.let { actions[it] }
        ActionConfigSheet(
            initialKeyword = selectedKeyword,
            initialConfig = initialConfig,
            onDismiss = { showSheet = false },
            onSave = { keyword, config ->
                onUpdateAction(keyword, config)
                showSheet = false
            }
        )
    }
}

@Composable
fun ActionItemCard(
    keyword: String,
    config: ActionConfig,
    shape: Shape,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    val bgColor = try { Color((config.backgroundColor ?: "#00000000").toColorInt()) } catch(_:Exception) { Color.Transparent }
    val tintColor = try { Color((config.tintColor ?: "#FFFFFF").toColorInt()) } catch(_:Exception) { Color.White }

    HyperCapsuleGlassCard(
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .tactilePress(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(shapes.squircleSmall)
                    .background(bgColor)
                    .border(1.dp, Color.White.copy(alpha = 0.2f), shapes.squircleSmall),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "A",
                    color = tintColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = keyword,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = config.mode.name,
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
