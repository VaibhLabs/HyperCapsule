package com.hypercapsule.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import kotlinx.coroutines.launch

private data class FeatureHighlight(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val iconTint: Color
)

/**
 * Premium HyperCapsule v1.00 Stable Welcome / What's New Sheet.
 * Displays brand artwork, verified feature highlights, and a prominent Continue button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VersionWelcomeDialog(
    versionName: String = "1.00 Stable",
    onContinue: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val appIconBitmap = remember(context) {
        try {
            context.packageManager.getApplicationIcon(context.packageName).toBitmap().asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    val features = listOf(
        FeatureHighlight(
            icon = Icons.Rounded.Brush,
            title = "Refined Premium Interface",
            description = "Human-crafted AMOLED glass aesthetics, balanced contrast, and fluid spring motion.",
            iconTint = colors.primary
        ),
        FeatureHighlight(
            icon = Icons.Rounded.NotificationsActive,
            title = "Improved Notification Island",
            description = "Smart notification interception, heads-up dynamic pill, and native inline reply.",
            iconTint = colors.secondary
        ),
        FeatureHighlight(
            icon = Icons.Rounded.Palette,
            title = "Advanced Theme Customization",
            description = "Live island simulation, custom highlight colors, and full .hbr theme pack support.",
            iconTint = colors.tertiary
        ),
        FeatureHighlight(
            icon = Icons.Rounded.Widgets,
            title = "Enhanced Widget Integration",
            description = "Seamlessly host Android app widgets straight inside your dynamic island notch area.",
            iconTint = colors.success
        ),
        FeatureHighlight(
            icon = Icons.Rounded.Tune,
            title = "Refined Settings & Controls",
            description = "Re-engineered Island Behavior with clear hierarchy, live previews, and intuitive sliders.",
            iconTint = colors.primary
        ),
        FeatureHighlight(
            icon = Icons.Rounded.Speed,
            title = "Performance & Battery Refinements",
            description = "Glitch-free 120Hz smooth scrolling, zero horizontal layout leaks, and light footprint.",
            iconTint = colors.warning
        )
    )

    ModalBottomSheet(
        onDismissRequest = onContinue,
        sheetState = sheetState,
        containerColor = colors.surfaceBase,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = spacing.medium)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(shapes.pill)
                    .background(colors.borderHighlight)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.default)
                .padding(bottom = spacing.large)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- LOGO EMBLEM ---
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(shapes.large)
                    .background(colors.surfaceElevatedGlass)
                    .border(1.dp, colors.borderHighlight, shapes.large),
                contentAlignment = Alignment.Center
            ) {
                if (appIconBitmap != null) {
                    Image(
                        bitmap = appIconBitmap,
                        contentDescription = "HyperCapsule Logo",
                        modifier = Modifier.size(56.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // --- VERSION BADGE ---
            Box(
                modifier = Modifier
                    .clip(shapes.pill)
                    .background(colors.primary.copy(alpha = 0.15f))
                    .border(1.dp, colors.primary.copy(alpha = 0.35f), shapes.pill)
                    .padding(horizontal = 12.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "HyperCapsule $versionName",
                    style = typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            // --- HEADLINE & SUBTITLE ---
            Text(
                text = "A new experience.",
                style = typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = "Built for your everyday notifications.",
                style = typography.bodyMedium,
                color = colors.textSecondary
            )

            Spacer(modifier = Modifier.height(spacing.large))

            // --- FEATURE HIGHLIGHTS LIST ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                features.forEach { feature ->
                    HyperCapsuleGlassCard(
                        shape = shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(shapes.medium)
                                    .background(feature.iconTint.copy(alpha = 0.14f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = feature.icon,
                                    contentDescription = null,
                                    tint = feature.iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = feature.title,
                                    style = typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = feature.description,
                                    style = typography.bodySmall,
                                    color = colors.textSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.large))

            // --- ONE CLEAR CONTINUE BUTTON ---
            HyperCapsuleActionButton(
                text = "Continue",
                icon = Icons.Rounded.CheckCircle,
                onClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                        onContinue()
                    }
                },
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }
}
