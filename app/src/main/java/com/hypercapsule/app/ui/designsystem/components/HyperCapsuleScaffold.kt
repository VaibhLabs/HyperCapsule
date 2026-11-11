package com.hypercapsule.app.ui.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors

/**
 * HyperCapsule Scaffold with AMOLED background and ambient top glow mesh.
 */
@Composable
fun HyperCapsuleScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val colors = LocalHyperCapsuleColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Ambient subtle top glow for depth
        if (colors.isDark) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colors.primary.copy(alpha = 0.08f),
                                colors.secondary.copy(alpha = 0.03f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            topBar()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                content(PaddingValues(0.dp))
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            bottomBar()
        }
    }
}
