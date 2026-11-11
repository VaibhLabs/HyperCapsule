package com.hypercapsule.app.ui.screens.theme

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypercapsule.app.R
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleScaffold
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.screens.theme.content.ReplyStyleSheetContent

@Composable
fun GlobalReplyCustomizationScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: ThemeViewModel = viewModel()
    val activeThemeId by viewModel.activeThemeId.collectAsState()

    LaunchedEffect(activeThemeId) {
        if (activeThemeId != null) {
            viewModel.loadThemeForEditing(activeThemeId!!)
        } else {
            viewModel.clearCreatorState()
        }
    }

    val handleBack = {
        viewModel.saveTheme(existingId = viewModel.currentEditingThemeId, apply = true)
        onBack()
    }

    BackHandler {
        handleBack()
    }

    HyperCapsuleScaffold(
        topBar = {
            HyperCapsuleTopBar(
                title = stringResource(R.string.inline_reply_title),
                onBack = handleBack
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.currentEditingThemeId != null) {
                ReplyStyleSheetContent(viewModel = viewModel)
            }
        }
    }
}
