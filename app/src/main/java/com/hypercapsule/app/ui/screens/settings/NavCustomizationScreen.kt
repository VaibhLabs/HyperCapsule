package com.hypercapsule.app.ui.screens.settings

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.AltRoute
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.models.NavContent
import com.hypercapsule.app.ui.components.NavDropdown
import com.hypercapsule.app.ui.components.NavPreview
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleScaffold
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import kotlinx.coroutines.launch

@Composable
fun NavCustomizationScreen(
    onBack: () -> Unit,
    packageName: String? = null,
    showTopBar: Boolean = true
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context) }
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    // 1. Get Global Fallback
    val globalLayout by preferences.globalNavLayoutFlow.collectAsState(initial = NavContent.DISTANCE_ETA to NavContent.INSTRUCTION)

    // 2. Get local AppPreference (if packageName is provided)
    val appLayout by if (packageName != null) {
        preferences.getAppNavLayout(packageName).collectAsState(initial = null to null)
    } else {
        remember { mutableStateOf<Pair<NavContent?, NavContent?>>(null to null) }
    }

    // 3. Resolve what is currently active
    val isGlobalMode = packageName == null
    val isUsingGlobalDefault = !isGlobalMode && appLayout.first == null
    val currentLeft = appLayout.first ?: globalLayout.first
    val currentRight = appLayout.second ?: globalLayout.second

    HyperCapsuleScaffold(
        topBar = {
            if (showTopBar) {
                HyperCapsuleTopBar(
                    title = stringResource(R.string.nav_layout_title),
                    subtitle = if (!isGlobalMode) packageName else null,
                    onBack = onBack
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = spacing.screenPadding,
                    end = spacing.screenPadding,
                    bottom = spacing.screenPadding,
                    top = if (showTopBar) spacing.itemSpacing else 0.dp
                )
        ) {
            HyperCapsuleSectionHeader(
                title = stringResource(R.string.engine_preview_title),
                icon = Icons.Outlined.AltRoute
            )
            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            NavPreview(currentLeft, currentRight)

            Spacer(modifier = Modifier.height(spacing.sectionSpacing))

            HyperCapsuleSectionHeader(
                title = stringResource(R.string.group_configuration),
                icon = Icons.Outlined.Tune
            )
            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            if (!isGlobalMode) {
                HyperCapsuleGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = shapes.squircleMedium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isUsingGlobalDefault) {
                                    scope.launch {
                                        preferences.updateAppNavLayout(
                                            packageName,
                                            globalLayout.first,
                                            globalLayout.second
                                        )
                                    }
                                } else {
                                    scope.launch {
                                        preferences.updateAppNavLayout(
                                            packageName,
                                            null,
                                            null
                                        )
                                    }
                                }
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isUsingGlobalDefault,
                            onCheckedChange = null,
                            colors = CheckboxDefaults.colors(
                                checkedColor = colors.accentPrimary,
                                checkmarkColor = colors.textPrimary
                            )
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.use_global_default),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.textPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(spacing.itemSpacing))
            }

            val controlsEnabled = isGlobalMode || !isUsingGlobalDefault
            if (controlsEnabled) {
                HyperCapsuleGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = shapes.squircleLarge
                ) {
                    Column(Modifier.padding(spacing.cardPadding)) {
                        NavDropdown(
                            label = stringResource(R.string.left_content),
                            selected = currentLeft,
                            onSelect = { newLeft ->
                                scope.launch {
                                    if (isGlobalMode) preferences.setGlobalNavLayout(newLeft, currentRight)
                                    else preferences.updateAppNavLayout(packageName, newLeft, currentRight)
                                }
                            }
                        )
                        Spacer(Modifier.height(spacing.cardPadding))
                        NavDropdown(
                            label = stringResource(R.string.right_content),
                            selected = currentRight,
                            onSelect = { newRight ->
                                scope.launch {
                                    if (isGlobalMode) preferences.setGlobalNavLayout(currentLeft, newRight)
                                    else preferences.updateAppNavLayout(packageName, currentLeft, newRight)
                                }
                            }
                        )
                    }
                }
            }

            // --- Informational Notes ---
            Spacer(modifier = Modifier.height(spacing.sectionSpacing))
            HyperCapsuleGlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = shapes.squircleMedium,
                tint = colors.accentSecondary.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = colors.accentSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.good_to_know),
                            fontWeight = FontWeight.Bold,
                            color = colors.accentSecondary,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.nav_layout_info),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(spacing.sectionSpacing))
        }
    }
}
