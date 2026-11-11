package com.hypercapsule.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.hypercapsule.app.ui.designsystem.theme.HyperCapsuleDesignSystemTheme

@Composable
fun HyperCapsuleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    HyperCapsuleDesignSystemTheme(
        darkTheme = darkTheme,
        content = content
    )
}

/**
 * Backward compatibility alias for HyperBridgeTheme.
 */
@Deprecated("Use HyperCapsuleTheme instead", ReplaceWith("HyperCapsuleTheme(darkTheme, content)"))
@Composable
fun HyperBridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = HyperCapsuleTheme(darkTheme = darkTheme, content = content)
