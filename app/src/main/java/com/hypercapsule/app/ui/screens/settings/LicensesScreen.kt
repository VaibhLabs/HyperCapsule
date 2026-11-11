package com.hypercapsule.app.ui.screens.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography

data class Library(val name: String, val author: String, val license: String, val url: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensesScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val libs = listOf(
        Library("HyperBridge (Upstream Base)", "D4vidDf & contributors", "Apache 2.0", "https://github.com/D4vidDf/HyperBridge"),
        Library("HyperIsland-ToolKit", "D4vidDf", "Apache 2.0", "https://github.com/D4vidDf/HyperIsland-ToolKit"),
        Library("Haze", "Chris Banes", "Apache 2.0", "https://github.com/chrisbanes/haze"),
        Library("Jetpack Compose", "Google", "Apache 2.0", "https://developer.android.com/jetpack/compose"),
        Library("Material 3", "Google", "Apache 2.0", "https://m3.material.io/"),
        Library("AndroidX Core", "Google", "Apache 2.0", "https://developer.android.com/jetpack/androidx"),
        Library("AndroidX Activity", "Google", "Apache 2.0", "https://developer.android.com/jetpack/androidx/releases/activity"),
        Library("AndroidX AppCompat", "Google", "Apache 2.0", "https://developer.android.com/jetpack/androidx/releases/appcompat"),
        Library("AndroidX DataStore", "Google", "Apache 2.0", "https://developer.android.com/topic/libraries/architecture/datastore"),
        Library("AndroidX Lifecycle", "Google", "Apache 2.0", "https://developer.android.com/jetpack/androidx/releases/lifecycle"),
        Library("AndroidX Navigation 3", "Google", "Apache 2.0", "https://developer.android.com/jetpack/androidx/releases/navigation"),
        Library("AndroidX Palette", "Google", "Apache 2.0", "https://developer.android.com/develop/ui/views/graphics/palette"),
        Library("AndroidX Room", "Google", "Apache 2.0", "https://developer.android.com/training/data-storage/room"),
        Library("Gson", "Google", "Apache 2.0", "https://github.com/google/gson"),
        Library("Kotlin Coroutines", "JetBrains", "Apache 2.0", "https://github.com/Kotlin/kotlinx.coroutines"),
        Library("Kotlin Serialization", "JetBrains", "Apache 2.0", "https://github.com/Kotlin/kotlinx.serialization"),
        Library("Shizuku API", "RikkaApps", "Apache 2.0", "https://github.com/RikkaApps/Shizuku-API")
    ).sortedBy { it.name }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.open_source_licenses),
                onBackClick = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(spacing.default),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                items(libs) { lib ->
                    HyperCapsuleGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { uriHandler.openUri(lib.url) },
                        shape = shapes.large
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(shapes.medium)
                                    .background(colors.primary.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Code,
                                    contentDescription = null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.padding(start = spacing.default))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lib.name,
                                    style = typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${lib.author} • ${lib.license}",
                                    style = typography.bodySmall,
                                    color = colors.textSecondary
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = colors.textTertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
