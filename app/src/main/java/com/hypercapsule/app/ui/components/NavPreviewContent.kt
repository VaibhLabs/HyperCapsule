package com.hypercapsule.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypercapsule.app.R
import com.hypercapsule.app.models.NavContent
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes

@Composable
fun NavPreview(left: NavContent, right: NavContent) {
    val leftLabel = stringResource(getNavContentLabelRes(left))
    val rightLabel = stringResource(getNavContentLabelRes(right))
    val cd = stringResource(R.string.cd_nav_preview, leftLabel, rightLabel)
    val shapes = LocalHyperCapsuleShapes.current

    HyperCapsuleGlassCard(
        shape = shapes.squircleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = cd }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(330.dp)
                    .height(46.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.Black)
            ) {
                // Camera Cutout
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1F1F1F))
                )

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // LEFT SIDE
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = Icons.Default.TurnRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        NavContentRenderer(left, Alignment.Start)
                    }

                    // SPACER
                    Spacer(modifier = Modifier.width(32.dp))

                    // RIGHT SIDE
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        NavContentRenderer(right, Alignment.End)
                    }
                }
            }
        }
    }
}

@Composable
fun NavContentRenderer(type: NavContent, align: Alignment.Horizontal) {

    // Fade Logic: Only applies to long text (Instruction)
    val fadeBrush = if (type == NavContent.INSTRUCTION) {
        if (align == Alignment.Start) {
            Brush.horizontalGradient(0.85f to Color.White, 1.0f to Color.Transparent)
        } else {
            Brush.horizontalGradient(0.0f to Color.Transparent, 0.15f to Color.White)
        }
    } else null

    val textStyle = if (fadeBrush != null) {
        TextStyle(brush = fadeBrush, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    } else {
        TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }

    val timeStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Normal, fontSize = 14.sp)

    when (type) {
        NavContent.INSTRUCTION -> {
            Text(
                text = stringResource(R.string.nav_preview_instruction),
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }
        NavContent.DISTANCE -> {
            Text(
                text = stringResource(R.string.nav_preview_distance),
                style = textStyle
            )
        }
        NavContent.ETA -> {
            Text(
                text = stringResource(R.string.nav_preview_time),
                style = timeStyle
            )
        }
        NavContent.DISTANCE_ETA -> {
            // Combined Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.nav_preview_distance), style = textStyle.copy(fontSize = 13.sp))
                Spacer(Modifier.width(4.dp))
                Text("•", color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.nav_preview_time), style = timeStyle.copy(fontSize = 13.sp))
            }
        }
        NavContent.NONE -> { /* Empty */ }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavDropdown(label: String, selected: NavContent, onSelect: (NavContent) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current

    Column {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.accentPrimary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = stringResource(getNavContentLabelRes(selected)),
                onValueChange = {},
                readOnly = true,
                shape = shapes.squircleMedium,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = colors.textPrimary),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentPrimary,
                    unfocusedBorderColor = colors.cardBorder,
                    focusedContainerColor = colors.cardSurface,
                    unfocusedContainerColor = colors.cardSurface
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                NavContent.entries.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(getNavContentLabelRes(option)),
                                color = if (option == selected) colors.accentPrimary else colors.textPrimary,
                                fontWeight = if (option == selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = { onSelect(option); expanded = false }
                    )
                }
            }
        }
    }
}

private fun getNavContentLabelRes(content: NavContent): Int {
    return when(content) {
        NavContent.INSTRUCTION -> R.string.nav_content_instruction
        NavContent.DISTANCE -> R.string.nav_content_distance
        NavContent.ETA -> R.string.nav_content_eta
        NavContent.DISTANCE_ETA -> R.string.nav_content_distance_eta
        NavContent.NONE -> R.string.nav_content_none
    }
}
