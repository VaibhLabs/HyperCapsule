package com.hypercapsule.app.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.ui.AppInfo
import com.hypercapsule.app.ui.AppListViewModel
import com.hypercapsule.app.ui.components.BlocklistEditor
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSettingsRow
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlobalBlocklistScreen(
    onBack: () -> Unit,
    onNavigateToAppList: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context) }

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    val globalBlockedTerms by preferences.globalBlockedTermsFlow.collectAsState(initial = emptySet())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.blocked_terms),
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.default, vertical = spacing.small)
            ) {
                // 1. GLOBAL RULES
                HyperCapsuleSectionHeader(title = stringResource(R.string.global_rules))

                HyperCapsuleGlassCard(
                    shape = shapes.large,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BlocklistEditor(
                        terms = globalBlockedTerms,
                        onUpdate = { scope.launch { preferences.setGlobalBlockedTerms(it) } }
                    )
                }

                Spacer(Modifier.height(spacing.large))

                // 2. APP RULES
                HyperCapsuleSectionHeader(title = stringResource(R.string.app_specific_rules))

                HyperCapsuleGlassCard(
                    shape = shapes.large,
                    contentPadding = 0.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HyperCapsuleSettingsRow(
                        title = stringResource(R.string.app_specific_rules),
                        subtitle = stringResource(R.string.manage_app_rules_desc),
                        icon = Icons.Default.Apps,
                        showChevron = true,
                        onClick = onNavigateToAppList
                    )
                }

                Spacer(modifier = Modifier.height(spacing.huge))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocklistAppListScreen(
    onBack: () -> Unit,
    viewModel: AppListViewModel = viewModel()
) {
    val activeApps by viewModel.activeAppsState.collectAsState()
    var selectedApp by remember { mutableStateOf<AppInfo?>(null) }
    val colors = LocalHyperCapsuleColors.current
    val spacing = LocalHyperCapsuleSpacing.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.app_specific_rules),
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(spacing.default),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                items(activeApps, key = { it.packageName }) { app ->
                    AppBlockItem(app = app, viewModel = viewModel) { selectedApp = app }
                }
            }
        }
    }

    if (selectedApp != null) {
        AppBlocklistDialog(
            app = selectedApp!!,
            viewModel = viewModel,
            onDismiss = { selectedApp = null }
        )
    }
}

@Composable
fun AppBlockItem(
    app: AppInfo,
    viewModel: AppListViewModel,
    onClick: () -> Unit
) {
    val terms by viewModel.getAppBlockedTerms(app.packageName).collectAsState(initial = emptySet())
    val count = terms.size

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val subtitle = if (count > 0) {
        stringResource(R.string.blocked_terms_count, count)
    } else {
        stringResource(R.string.no_active_rules)
    }
    val subtitleColor = if (count > 0) colors.error else colors.textSecondary

    HyperCapsuleGlassCard(
        modifier = Modifier.fillMaxWidth(),
        shape = shapes.large,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (app.icon != null) {
                Image(
                    bitmap = app.icon.asImageBitmap(),
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
                        imageVector = Icons.Default.Android,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(Modifier.width(spacing.default))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = typography.bodySmall,
                    color = subtitleColor
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = colors.textTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun AppBlocklistDialog(
    app: AppInfo,
    viewModel: AppListViewModel,
    onDismiss: () -> Unit
) {
    val blockedTerms by viewModel.getAppBlockedTerms(app.packageName).collectAsState(initial = emptySet())
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val typography = LocalHyperCapsuleTypography.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceElevated,
        shape = shapes.large,
        title = {
            Text(app.name, style = typography.titleMedium, color = colors.textPrimary)
        },
        text = {
            BlocklistEditor(
                terms = blockedTerms,
                onUpdate = { viewModel.updateAppBlockedTerms(app.packageName, it) }
            )
        },
        confirmButton = {
            HyperCapsuleActionButton(
                text = stringResource(R.string.done),
                onClick = onDismiss,
                isPrimary = true
            )
        }
    )
}
