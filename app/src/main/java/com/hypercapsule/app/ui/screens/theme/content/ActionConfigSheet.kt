package com.hypercapsule.app.ui.screens.theme.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.hypercapsule.app.R
import com.hypercapsule.app.models.theme.ActionButtonMode
import com.hypercapsule.app.models.theme.ActionConfig
import com.hypercapsule.app.ui.designsystem.components.ButtonVariant
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import kotlin.text.ifEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionConfigSheet(
    initialKeyword: String?,
    initialConfig: ActionConfig?,
    onDismiss: () -> Unit,
    onSave: (String, ActionConfig) -> Unit
) {
    var keyword by remember { mutableStateOf(initialKeyword ?: "") }
    var mode by remember { mutableStateOf(initialConfig?.mode ?: ActionButtonMode.ICON) }
    var bgColor by remember { mutableStateOf(initialConfig?.backgroundColor ?: "#ffffff") }
    var tintColor by remember { mutableStateOf(initialConfig?.tintColor ?: "#000000") }

    val isEditing = initialKeyword != null
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.backgroundElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.screenPadding)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.action_sheet_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )

            Spacer(Modifier.height(16.dp))

            // --- WIP WARNING ---
            HyperCapsuleGlassCard(
                shape = shapes.squircleMedium,
                tint = colors.accentSecondary.copy(alpha = 0.12f),
                borderColor = colors.accentSecondary.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = colors.accentSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.action_wip_warning),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.accentSecondary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- LIVE PREVIEW ---
            Text(
                stringResource(R.string.action_preview),
                style = MaterialTheme.typography.labelMedium,
                color = colors.accentPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            // Simulation of Notification Action Button
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .widthIn(min = 48.dp)
                    .clip(if (mode == ActionButtonMode.ICON) CircleShape else shapes.squircleMedium)
                    .background(safeParseColor(bgColor))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), if (mode == ActionButtonMode.ICON) CircleShape else shapes.squircleMedium)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (mode != ActionButtonMode.TEXT) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = safeParseColor(tintColor),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (mode != ActionButtonMode.ICON) {
                        if (mode == ActionButtonMode.BOTH) Spacer(Modifier.width(8.dp))
                        Text(
                            text = keyword.ifEmpty { stringResource(R.string.action_default_preview_text) },
                            color = safeParseColor(tintColor),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Keyword Input
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text(stringResource(R.string.action_label_keyword)) },
                shape = shapes.squircleMedium,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isEditing,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentPrimary,
                    unfocusedBorderColor = colors.cardBorder,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                )
            )

            Spacer(Modifier.height(16.dp))

            // Mode Selector
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                val modes = ActionButtonMode.entries.toTypedArray()
                modes.forEachIndexed { index, m ->
                    SegmentedButton(
                        selected = mode == m,
                        onClick = { mode = m },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size)
                    ) {
                        Text(
                            text = when (m) {
                                ActionButtonMode.ICON -> stringResource(R.string.action_mode_icon)
                                ActionButtonMode.TEXT -> stringResource(R.string.action_mode_text)
                                ActionButtonMode.BOTH -> stringResource(R.string.action_mode_both)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Color Inputs
            OutlinedTextField(
                value = bgColor,
                onValueChange = { bgColor = it },
                label = { Text(stringResource(R.string.action_label_bg_color)) },
                shape = shapes.squircleMedium,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Box(Modifier.size(24.dp).background(safeParseColor(bgColor), CircleShape).border(1.dp, colors.cardBorder, CircleShape))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentPrimary,
                    unfocusedBorderColor = colors.cardBorder,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                )
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = tintColor,
                onValueChange = { tintColor = it },
                label = { Text(stringResource(R.string.action_label_tint_color)) },
                shape = shapes.squircleMedium,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Box(Modifier.size(24.dp).background(safeParseColor(tintColor), CircleShape).border(1.dp, colors.cardBorder, CircleShape))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentPrimary,
                    unfocusedBorderColor = colors.cardBorder,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary
                )
            )

            Spacer(Modifier.height(28.dp))

            HyperCapsuleActionButton(
                text = stringResource(R.string.creator_action_save),
                onClick = {
                    if (keyword.isNotBlank()) {
                        val newConfig = ActionConfig(
                            mode = mode,
                            backgroundColor = bgColor,
                            tintColor = tintColor
                        )
                        onSave(keyword, newConfig)
                    }
                },
                enabled = keyword.isNotBlank(),
                variant = ButtonVariant.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

// Helper for color parsing safely
fun safeParseColor(hex: String): Color {
    return try {
        Color(hex.toColorInt())
    } catch (e: Exception) {
        Color.Magenta
    }
}
