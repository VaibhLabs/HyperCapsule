package com.hypercapsule.app.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import com.hypercapsule.app.util.parseBold

data class VersionLog(val version: String, val titleRes: Int, val textRes: Int, val isLatest: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogHistoryScreen(onBack: () -> Unit) {
    val colors = LocalHyperCapsuleColors.current
    val spacing = LocalHyperCapsuleSpacing.current

    val history = listOf(
        VersionLog("0.5.7", R.string.title_0_5_7, R.string.changelog_0_5_7, isLatest = true),
        VersionLog("0.5.6", R.string.title_0_5_6, R.string.changelog_0_5_6),
        VersionLog("0.5.5", R.string.title_0_5_5, R.string.changelog_0_5_5),
        VersionLog("0.5.2", R.string.title_0_5_2, R.string.changelog_0_5_2),
        VersionLog("0.5.1", R.string.title_0_5_1, R.string.changelog_0_5_1),
        VersionLog("0.5.0", R.string.title_0_5_0, R.string.changelog_0_5_0),
        VersionLog("0.4.2", R.string.title_0_4_2, R.string.changelog_0_4_2),
        VersionLog("0.4.0", R.string.title_0_4_0, R.string.changelog_0_4_0),
        VersionLog("0.3.1", R.string.title_0_3_1, R.string.changelog_0_3_1),
        VersionLog("0.3.0", R.string.title_0_3_0, R.string.changelog_0_3_0),
        VersionLog("0.2.0", R.string.title_0_2_0, R.string.changelog_0_2_0),
        VersionLog("0.1.0", R.string.title_0_1_0, R.string.changelog_0_1_0)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.version_history),
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(spacing.default),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                items(history) { log ->
                    ChangelogItem(log)
                }
            }
        }
    }
}

@Composable
fun ChangelogItem(log: VersionLog) {
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { if (log.isLatest) expanded = true }

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "rotation")
    val expandLabel = if (expanded) stringResource(R.string.cd_collapse_changelog) else stringResource(R.string.cd_expand_changelog)

    HyperCapsuleGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClickLabel = expandLabel) { expanded = !expanded },
        shape = shapes.large,
        borderColor = if (log.isLatest) colors.primary.copy(alpha = 0.4f) else colors.borderHighlight,
        backgroundColor = if (log.isLatest) colors.primary.copy(alpha = 0.08f) else colors.surfaceCardGlass
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Version Bubble
                Box(
                    modifier = Modifier
                        .clip(shapes.pill)
                        .background(colors.surfaceElevatedGlass)
                        .border(1.dp, colors.borderHighlight, shapes.pill)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "v${log.version}",
                        style = typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (log.isLatest) colors.primary else colors.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(spacing.medium))

                Text(
                    text = stringResource(log.titleRes),
                    style = typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )

                if (log.isLatest) {
                    Box(
                        modifier = Modifier
                            .clip(shapes.pill)
                            .background(colors.primary)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.badge_new),
                            style = typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.small))
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp).rotate(rotation)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(spacing.medium))
                    HorizontalDivider(color = colors.borderSubtle)
                    Spacer(modifier = Modifier.height(spacing.medium))

                    Text(
                        text = stringResource(log.textRes).parseBold(),
                        style = typography.bodyMedium,
                        color = colors.textSecondary,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
