package com.hypercapsule.app.ui.screens.legal

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

/**
 * Reusable native Compose document viewer for legal texts (Privacy Policy, Terms of Use, Copyright).
 * Follows the Red AMOLED x Liquid Glass aesthetic with accessible typography and offline readability.
 */
@Composable
fun LegalDocumentScreen(
    document: LegalDocument,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    bottomContent: (@Composable () -> Unit)? = null
) {
    val colors = LocalHyperCapsuleColors.current
    val typography = LocalHyperCapsuleTypography.current
    val spacing = LocalHyperCapsuleSpacing.current
    val shapes = LocalHyperCapsuleShapes.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = document.title,
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = spacing.default,
                    end = spacing.default,
                    top = spacing.small,
                    bottom = spacing.huge
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                // Summary Card & Effective Date
                item {
                    HyperCapsuleGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = shapes.large,
                        contentPadding = spacing.default
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(shapes.medium)
                                        .background(colors.primary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Shield,
                                        contentDescription = null,
                                        tint = colors.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(spacing.small))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = document.title,
                                        style = typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                    Text(
                                        text = "Effective Date: ${document.lastUpdated}",
                                        style = typography.labelSmall,
                                        color = colors.primary
                                    )
                                }
                            }

                            if (document.summary.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(spacing.small))
                                Text(
                                    text = document.summary,
                                    style = typography.bodyMedium,
                                    color = colors.textSecondary,
                                    lineHeight = 20.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Individual Document Sections
                items(document.sections) { section ->
                    HyperCapsuleGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = shapes.large,
                        contentPadding = spacing.default
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = section.title,
                                style = typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(spacing.extraSmall))

                            section.paragraphs.forEachIndexed { index, paragraph ->
                                if (index > 0) {
                                    Spacer(modifier = Modifier.height(spacing.small))
                                }
                                Text(
                                    text = paragraph,
                                    style = typography.bodyMedium,
                                    color = colors.textPrimary,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (section.bulletPoints.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(spacing.small))
                                section.bulletPoints.forEach { point ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "•",
                                            style = typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primary,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(
                                            text = point,
                                            style = typography.bodyMedium,
                                            color = colors.textSecondary,
                                            lineHeight = 20.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Optional Bottom Action (e.g. Website launcher)
                if (bottomContent != null) {
                    item {
                        bottomContent()
                    }
                }
            }
        }
    }
}
