package com.hypercapsule.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes

@Composable
fun PermanentIslandPreview(islandWidthValue: Int) {
    val shapes = LocalHyperCapsuleShapes.current
    val colors = LocalHyperCapsuleColors.current

    // Convert 0-20 value to dp width. Base width is 120dp. Each step adds 5dp.
    val targetWidth = 120.dp + (islandWidthValue * 5).dp

    val width by animateDpAsState(
        targetValue = targetWidth,
        animationSpec = spring(0.8f, Spring.StiffnessLow),
        label = "width"
    )

    HyperCapsuleGlassCard(
        shape = shapes.squircleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {

            // Mock Status Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "12:00",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.SignalCellular4Bar,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = colors.textSecondary
                    )
                    Icon(
                        imageVector = Icons.Default.BatteryFull,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = colors.textSecondary
                    )
                }
            }

            // The Pill
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .width(width)
                    .height(28.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color.Black)
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Empty island, just the camera cutout mock
                Box(
                    Modifier
                        .size(14.dp)
                        .background(Color(0xFF1F1F1F), CircleShape)
                )
            }
        }
    }
}
