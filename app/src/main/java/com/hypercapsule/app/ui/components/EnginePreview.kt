package com.hypercapsule.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.SignalCellular4Bar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import kotlinx.coroutines.delay

@Composable
fun EnginePreview(isNative: Boolean) {
    var isExpanded by remember { mutableStateOf(false) }
    val shapes = LocalHyperCapsuleShapes.current

    LaunchedEffect(isNative) {
        while(true) {
            isExpanded = false
            delay(2500)
            isExpanded = true
            delay(4500)
        }
    }

    val topPadding by animateDpAsState(
        targetValue = if (isExpanded) 42.dp else 10.dp,
        animationSpec = tween(500, easing = EaseInOutQuart),
        label = "yOffset"
    )

    val height by animateDpAsState(
        targetValue = if (isExpanded && isNative ) 130.dp else if (isExpanded && !isNative) 80.dp else 26.dp,
        animationSpec = spring(0.8f, Spring.StiffnessLow),
        label = "height"
    )

    val width by animateDpAsState(
        targetValue = if (isExpanded) 340.dp else if (isNative) 120.dp else 200.dp,
        animationSpec = spring(0.8f, Spring.StiffnessLow),
        label = "width"
    )

    val containerColor by animateColorAsState(
        if (isNative && isExpanded) Color(0xFF1B1D22) else Color.Black,
        label = "color"
    )

    HyperCapsuleGlassCard(
        shape = shapes.squircleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
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
                Text("12:00", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.SignalCellular4Bar, null, modifier = Modifier.size(14.dp))
                    Icon(Icons.Default.BatteryFull, null, modifier = Modifier.size(14.dp))
                }
            }

            // The Pill
            Box(
                modifier = Modifier
                    .padding(top = topPadding)
                    .width(width)
                    .height(height)
                    .clip(RoundedCornerShape(if (isExpanded) 24.dp else 50.dp))
                    .background(containerColor)
                    .border(
                        1.dp,
                        Color.White.copy(alpha = if (isExpanded) 0.15f else 0.08f),
                        RoundedCornerShape(if (isExpanded) 24.dp else 50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!isExpanded) {
                    // COLLAPSED MOCKUP
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Group, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (isNative) "" else "Alice",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.weight(1f))
                        // Camera cutout mock
                        Box(
                            Modifier
                                .size(14.dp)
                                .background(Color(0xFF1F1F1F), CircleShape)
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = if (isNative) "Alice" else "Incoming",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    // EXPANDED MOCKUP
                    if (isNative) {
                        // Live Update Design
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(16.dp)
                                        .background(Color(0xFF3DDA82), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Call, null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("HyperCapsule • now", color = Color.White.copy(0.6f), fontSize = 10.sp)
                            }
                            Spacer(Modifier.height(4.dp))
                            Column(Modifier.padding(start= 24.dp)) {
                                Text(
                                    "Alice",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Incoming Call",
                                    color = Color.White.copy(0.7f),
                                    fontSize = 12.sp
                                )

                                Spacer(Modifier.height(8.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                    Text(
                                        "Reject",
                                        color = Color(0xFFFF5252),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        "Answer",
                                        color = Color(0xFF3DDA82),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        // Xiaomi Featured Design
                        Row(
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Group, null, tint = Color.White, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Alice", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("Incoming Call", color = Color.White.copy(0.7f), fontSize = 12.sp)
                            }
                            Box(
                                Modifier
                                    .size(42.dp)
                                    .background(Color(0xFFFF3B30), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CallEnd, null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Box(
                                Modifier
                                    .size(42.dp)
                                    .background(Color(0xFF34C759), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Call, null, tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EngineOptionCard(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    HyperCapsuleGlassCard(
        shape = shapes.squircleMedium,
        tint = if (isSelected) colors.accentPrimary.copy(alpha = 0.12f) else colors.cardSurface,
        borderColor = if (isSelected) colors.accentPrimary else colors.cardBorder,
        modifier = Modifier
            .fillMaxWidth()
            .tactilePress(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(spacing.cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = colors.accentPrimary,
                    unselectedColor = colors.textTertiary
                )
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textPrimary
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Xiaomi Featured (False)")
@Composable
fun PreviewXiaomiEngine() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            EnginePreview(isNative = false)
        }
    }
}

@Preview(showBackground = true, name = "Native Live Update (True)")
@Composable
fun PreviewNativeEngine() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            EnginePreview(isNative = true)
        }
    }
}

@Preview(showBackground = true, name = "Option Card Preview")
@Composable
fun PreviewOptionCard() {
    MaterialTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            EngineOptionCard(
                title = "Selected Item",
                description = "This is how it looks when selected",
                isSelected = true,
                onClick = {}
            )
            EngineOptionCard(
                title = "Unselected Item",
                description = "This is the default state",
                isSelected = false,
                onClick = {}
            )
        }
    }
}
