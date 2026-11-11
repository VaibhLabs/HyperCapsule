package com.hypercapsule.app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.models.IslandConfig
import com.hypercapsule.app.ui.components.IslandSettingsControl
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleScaffold
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.theme.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.theme.HyperCapsuleTheme
import kotlinx.coroutines.launch

@Composable
fun IslandSettingsScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context) }

    val globalConfig by preferences.globalConfigFlow.collectAsState(initial = IslandConfig(
        isFloat = false,
        isShowShade = false,
        timeout = 10
    ))

    IslandSettingsContent(
        globalConfig = globalConfig,
        onBack = onBack,
        onUpdateConfig = { newConfig ->
            scope.launch { preferences.updateGlobalConfig(newConfig) }
        }
    )
}

@Composable
fun IslandSettingsContent(
    globalConfig: IslandConfig,
    onBack: () -> Unit,
    onUpdateConfig: (IslandConfig) -> Unit
) {
    val spacing = LocalHyperCapsuleSpacing.current

    HyperCapsuleScaffold(
        topBar = {
            HyperCapsuleTopBar(
                title = stringResource(R.string.global_settings),
                onBack = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenPadding, vertical = spacing.itemSpacing)
        ) {
            IslandSettingsControl(
                config = globalConfig,
                onUpdate = onUpdateConfig
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IslandSettingsScreenPreview() {
    HyperCapsuleTheme {
        IslandSettingsContent(
            globalConfig = IslandConfig(isFloat = true, isShowShade = true, timeout = 5, floatTimeout = 6),
            onBack = {},
            onUpdateConfig = {}
        )
    }
}
