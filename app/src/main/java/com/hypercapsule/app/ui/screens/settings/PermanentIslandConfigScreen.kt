package com.hypercapsule.app.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AspectRatio
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.ui.components.PermanentIslandPreview
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleScaffold
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSlider
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSwitch
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.theme.HyperCapsuleTheme
import kotlinx.coroutines.launch

@Composable
fun PermanentIslandConfigScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val scope = rememberCoroutineScope()

    val isEnabled by prefs.isPermanentIslandEnabledFlow.collectAsState(initial = false)
    val islandWidth by prefs.permanentIslandWidthFlow.collectAsState(initial = 0)
    val hideInLandscape by prefs.hidePermanentIslandLandscapeFlow.collectAsState(initial = false)

    PermanentIslandConfigContent(
        isEnabled = isEnabled,
        islandWidth = islandWidth,
        hideInLandscape = hideInLandscape,
        onEnabledChange = { checked ->
            scope.launch {
                prefs.setPermanentIslandEnabled(checked)
            }
        },
        onWidthChange = { width ->
            scope.launch {
                prefs.setPermanentIslandWidth(width)
            }
        },
        onHideInLandscapeChange = { hide ->
            scope.launch {
                prefs.setHidePermanentIslandLandscape(hide)
            }
        },
        onBack = onBack
    )
}

@Composable
fun PermanentIslandConfigContent(
    isEnabled: Boolean,
    islandWidth: Int,
    hideInLandscape: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    onWidthChange: (Int) -> Unit,
    onHideInLandscapeChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    var sliderValue by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    LaunchedEffect(islandWidth) {
        if (!isDragging) {
            sliderValue = islandWidth.toFloat()
        }
    }

    HyperCapsuleScaffold(
        topBar = {
            HyperCapsuleTopBar(
                title = stringResource(R.string.permanent_island_title),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.screenPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(spacing.itemSpacing))

            PermanentIslandPreview(islandWidthValue = if (isDragging) sliderValue.toInt() else islandWidth)

            Spacer(Modifier.height(spacing.sectionSpacing))

            HyperCapsuleSectionHeader(
                title = stringResource(R.string.group_configuration),
                icon = Icons.Outlined.Tune
            )
            Spacer(Modifier.height(spacing.itemSpacing))

            HyperCapsuleGlassCard(
                shape = shapes.squircleLarge,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(spacing.cardPadding)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.permanent_island_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.textPrimary
                            )
                        }
                        HyperCapsuleSwitch(
                            checked = isEnabled,
                            onCheckedChange = onEnabledChange
                        )
                    }

                    if (isEnabled) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.permanent_island_width),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                                color = colors.textSecondary
                            )
                            Text(
                                text = "+${sliderValue.toInt() * 5}dp",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.accentPrimary
                            )
                        }

                        HyperCapsuleSlider(
                            value = sliderValue,
                            onValueChange = { value ->
                                isDragging = true
                                sliderValue = value
                                onWidthChange(value.toInt())
                            },
                            onValueChangeFinished = {
                                isDragging = false
                            },
                            valueRange = 0f..20f,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.permanent_island_hide_landscape),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = colors.textPrimary
                                )
                            }
                            HyperCapsuleSwitch(
                                checked = hideInLandscape,
                                onCheckedChange = onHideInLandscapeChange
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(spacing.itemSpacing))

            HyperCapsuleGlassCard(
                shape = shapes.squircleMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.permanent_island_screen_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(Modifier.height(spacing.sectionSpacing))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PermanentIslandConfigScreenPreview() {
    HyperCapsuleTheme {
        PermanentIslandConfigContent(
            isEnabled = true,
            islandWidth = 10,
            hideInLandscape = false,
            onEnabledChange = {},
            onWidthChange = {},
            onHideInLandscapeChange = {},
            onBack = {}
        )
    }
}
