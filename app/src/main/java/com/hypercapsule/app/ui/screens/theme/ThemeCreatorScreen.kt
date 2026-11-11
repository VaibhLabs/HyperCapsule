package com.hypercapsule.app.ui.screens.theme

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.DisplaySettings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypercapsule.app.R
import com.hypercapsule.app.models.theme.ColorMode
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import com.hypercapsule.app.ui.screens.settings.NavCustomizationScreen
import com.hypercapsule.app.ui.screens.theme.content.ActionsDetailContent
import com.hypercapsule.app.ui.screens.theme.content.AppsDetailContent
import com.hypercapsule.app.ui.screens.theme.content.BehaviourMenuContent
import com.hypercapsule.app.ui.screens.theme.content.CallStyleSheetContent
import com.hypercapsule.app.ui.screens.theme.content.ColorsDetailContent
import com.hypercapsule.app.ui.screens.theme.content.EngineThemeContent
import com.hypercapsule.app.ui.screens.theme.content.IconsDetailContent
import com.hypercapsule.app.ui.screens.theme.content.NotificationTypesContent
import com.hypercapsule.app.ui.screens.theme.content.ReplyStyleSheetContent
import com.hypercapsule.app.ui.screens.theme.content.ThemeBehaviourContent
import com.hypercapsule.app.ui.screens.theme.content.safeParseColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class CreatorRoute {
    MAIN_MENU, BEHAVIOR_MENU, BEHAVIOR_ENGINE, BEHAVIOR_ISLAND, BEHAVIOR_TYPES, COLORS, ICONS, CALLS, NAVIGATION, REPLY, ACTIONS, APPS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeCreatorScreen(
    editThemeId: String? = null,
    onBack: () -> Unit,
    onThemeCreated: () -> Unit
) {
    val viewModel: ThemeViewModel = viewModel()
    val activeThemeId by viewModel.activeThemeId.collectAsState()
    val isNative by viewModel.useNativeLiveUpdates.collectAsState()

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    if (viewModel.editingAppPackage != null) {
        AppThemeEditor(viewModel = viewModel)
    } else {
        var currentRoute by remember { mutableStateOf(CreatorRoute.MAIN_MENU) }
        var showSettingsSheet by remember { mutableStateOf(false) }
        var showSaveDialog by remember { mutableStateOf(false) }

        LaunchedEffect(editThemeId) {
            if (editThemeId != null) {
                if (viewModel.currentEditingThemeId != editThemeId) {
                    viewModel.loadThemeForEditing(editThemeId)
                }
            } else {
                if (viewModel.currentEditingThemeId == null) {
                    viewModel.clearCreatorState()
                }
            }
        }

        val handleBackNavigation = {
            when (currentRoute) {
                CreatorRoute.BEHAVIOR_ENGINE,
                CreatorRoute.BEHAVIOR_ISLAND,
                CreatorRoute.BEHAVIOR_TYPES -> currentRoute = CreatorRoute.BEHAVIOR_MENU
                CreatorRoute.MAIN_MENU -> {
                    viewModel.currentEditingThemeId = null
                    onBack()
                }
                else -> currentRoute = CreatorRoute.MAIN_MENU
            }
        }

        BackHandler(enabled = true) {
            handleBackNavigation()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                val title = when (currentRoute) {
                    CreatorRoute.MAIN_MENU -> if (editThemeId == null) stringResource(R.string.creator_title_new) else stringResource(R.string.creator_title_edit)
                    CreatorRoute.BEHAVIOR_MENU -> stringResource(R.string.behaviour_triggers)
                    CreatorRoute.BEHAVIOR_ENGINE -> stringResource(R.string.engine)
                    CreatorRoute.BEHAVIOR_ISLAND -> stringResource(R.string.island_behavior)
                    CreatorRoute.BEHAVIOR_TYPES -> stringResource(R.string.active_notifications_title)
                    CreatorRoute.COLORS -> stringResource(R.string.creator_nav_colors)
                    CreatorRoute.ICONS -> stringResource(R.string.creator_nav_icons)
                    CreatorRoute.CALLS -> stringResource(R.string.creator_nav_calls)
                    CreatorRoute.NAVIGATION -> stringResource(R.string.nav_layout_title)
                    CreatorRoute.REPLY -> stringResource(R.string.inline_reply_title)
                    CreatorRoute.ACTIONS -> stringResource(R.string.creator_nav_actions)
                    CreatorRoute.APPS -> stringResource(R.string.creator_nav_apps)
                }

                HyperCapsuleTopBar(
                    title = title,
                    onBackClick = handleBackNavigation,
                    actions = {
                        if (currentRoute == CreatorRoute.MAIN_MENU) {
                            HyperCapsuleActionButton(
                                text = stringResource(R.string.creator_action_save),
                                onClick = {
                                    if (editThemeId != null && editThemeId == activeThemeId) {
                                        viewModel.saveTheme(existingId = editThemeId, apply = true)
                                        viewModel.currentEditingThemeId = null
                                        onThemeCreated()
                                    } else {
                                        showSaveDialog = true
                                    }
                                },
                                isPrimary = true
                            )
                        }
                    }
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = currentRoute,
                        transitionSpec = {
                            if (targetState == CreatorRoute.MAIN_MENU || targetState == CreatorRoute.BEHAVIOR_MENU && initialState != CreatorRoute.MAIN_MENU)
                                slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                            else
                                slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                        },
                        label = "CreatorNav"
                    ) { route ->
                        when (route) {
                            CreatorRoute.MAIN_MENU -> CreatorMainList(
                                viewModel = viewModel,
                                onNavigate = { currentRoute = it },
                                onEditSettings = { showSettingsSheet = true }
                            )
                            CreatorRoute.BEHAVIOR_MENU -> Box(Modifier.fillMaxSize()) {
                                BehaviourMenuContent(onNavigate = { currentRoute = it })
                            }
                            CreatorRoute.BEHAVIOR_ENGINE -> {
                                EngineThemeContent(
                                    isNative = isNative,
                                    showDefaultOption = false,
                                    onEngineChange = { viewModel.setUseNativeLiveUpdates(it ?: false) }
                                )
                            }
                            CreatorRoute.BEHAVIOR_ISLAND -> Box(Modifier.fillMaxSize()) {
                                ThemeBehaviourContent()
                            }
                            CreatorRoute.BEHAVIOR_TYPES -> Box(Modifier.fillMaxSize()) {
                                NotificationTypesContent()
                            }
                            CreatorRoute.NAVIGATION -> Box(Modifier.fillMaxSize()) {
                                NavCustomizationScreen(onBack = { currentRoute = CreatorRoute.MAIN_MENU }, null, false)
                            }
                            CreatorRoute.REPLY -> Box(Modifier.fillMaxSize()) {
                                ReplyStyleSheetContent(viewModel = viewModel)
                            }
                            CreatorRoute.COLORS -> DetailScreenShell(
                                previewContent = {
                                    SharedThemePreview(
                                        highlightColorHex = viewModel.selectedColorHex,
                                        useAppColors = (viewModel.colorMode == ColorMode.APP_ICON),
                                        shapeId = viewModel.selectedShapeId,
                                        paddingPercent = viewModel.iconPaddingPercent,
                                        answerColorHex = viewModel.callAnswerColor,
                                        declineColorHex = viewModel.callDeclineColor,
                                        answerShapeId = viewModel.callAnswerShapeId,
                                        declineShapeId = viewModel.callDeclineShapeId
                                    )
                                },
                                content = {
                                    ColorsDetailContent(
                                        selectedColorHex = viewModel.selectedColorHex,
                                        colorMode = viewModel.colorMode,
                                        onColorSelected = { viewModel.selectedColorHex = it },
                                        onColorModeChanged = { viewModel.colorMode = it }
                                    )
                                }
                            )
                            CreatorRoute.ICONS -> DetailScreenShell(
                                previewContent = {
                                    SharedThemePreview(
                                        highlightColorHex = viewModel.selectedColorHex,
                                        useAppColors = (viewModel.colorMode == ColorMode.APP_ICON),
                                        shapeId = viewModel.selectedShapeId,
                                        paddingPercent = viewModel.iconPaddingPercent,
                                        answerColorHex = viewModel.callAnswerColor,
                                        declineColorHex = viewModel.callDeclineColor,
                                        answerShapeId = viewModel.callAnswerShapeId,
                                        declineShapeId = viewModel.callDeclineShapeId
                                    )
                                },
                                content = {
                                    IconsDetailContent(
                                        iconPaddingPercent = viewModel.iconPaddingPercent,
                                        selectedShapeId = viewModel.selectedShapeId,
                                        onPaddingChange = { viewModel.iconPaddingPercent = it },
                                        onShapeChange = { viewModel.selectedShapeId = it },
                                        onStageAsset = { key, uri -> viewModel.stageAsset(key, uri) }
                                    )
                                }
                            )
                            CreatorRoute.CALLS -> DetailScreenShell(
                                previewContent = {
                                    SharedThemePreview(
                                        highlightColorHex = viewModel.selectedColorHex,
                                        useAppColors = (viewModel.colorMode == ColorMode.APP_ICON),
                                        shapeId = viewModel.selectedShapeId,
                                        paddingPercent = viewModel.iconPaddingPercent,
                                        answerColorHex = viewModel.callAnswerColor,
                                        declineColorHex = viewModel.callDeclineColor,
                                        answerShapeId = viewModel.callAnswerShapeId,
                                        declineShapeId = viewModel.callDeclineShapeId
                                    )
                                },
                                content = {
                                    CallStyleSheetContent(
                                        answerColor = viewModel.callAnswerColor,
                                        declineColor = viewModel.callDeclineColor,
                                        answerShapeId = viewModel.callAnswerShapeId,
                                        declineShapeId = viewModel.callDeclineShapeId,
                                        onAnswerColorChange = { viewModel.callAnswerColor = it },
                                        onDeclineColorChange = { viewModel.callDeclineColor = it },
                                        onAnswerShapeChange = { viewModel.callAnswerShapeId = it },
                                        onDeclineShapeChange = { viewModel.callDeclineShapeId = it },
                                        onAnswerIconSelected = { viewModel.callAnswerUri = it; viewModel.stageAsset("call_answer", it) },
                                        onDeclineIconSelected = { viewModel.callDeclineUri = it; viewModel.stageAsset("call_decline", it) }
                                    )
                                }
                            )
                            CreatorRoute.ACTIONS -> Box(Modifier.fillMaxSize()) {
                                ActionsDetailContent(
                                    actions = viewModel.themeDefaultActions,
                                    onUpdateAction = { k, v -> viewModel.updateDefaultAction(k, v) },
                                    onRemoveAction = { k -> viewModel.removeDefaultAction(k) }
                                )
                            }
                            CreatorRoute.APPS -> Box(Modifier.fillMaxSize()) {
                                AppsDetailContent(viewModel)
                            }
                        }
                    }
                }
            }

            if (showSettingsSheet) ThemeMetadataSheet(viewModel) { showSettingsSheet = false }
            if (showSaveDialog) SaveDialog(viewModel, editThemeId, activeThemeId, onThemeCreated) { showSaveDialog = false }
        }
    }
}

@Composable
fun SaveDialog(
    viewModel: ThemeViewModel,
    editThemeId: String?,
    activeThemeId: String?,
    onThemeCreated: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val typography = LocalHyperCapsuleTypography.current
    val shapes = LocalHyperCapsuleShapes.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceElevated,
        shape = shapes.large,
        title = {
            Text(
                text = stringResource(R.string.creator_dialog_apply_title),
                style = typography.titleMedium,
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                text = stringResource(R.string.creator_dialog_apply_desc),
                style = typography.bodyMedium,
                color = colors.textSecondary
            )
        },
        confirmButton = {
            HyperCapsuleActionButton(
                text = stringResource(R.string.creator_dialog_action_save_apply),
                onClick = {
                    onDismiss()
                    viewModel.saveTheme(existingId = editThemeId, apply = true)
                    viewModel.currentEditingThemeId = null
                    onThemeCreated()
                },
                isPrimary = true
            )
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                viewModel.saveTheme(existingId = editThemeId, apply = false)
                viewModel.currentEditingThemeId = null
                onThemeCreated()
            }) {
                Text(
                    text = stringResource(R.string.creator_dialog_action_save_only),
                    color = colors.textSecondary
                )
            }
        }
    )
}

@Composable
fun CreatorMainList(
    viewModel: ThemeViewModel,
    onNavigate: (CreatorRoute) -> Unit,
    onEditSettings: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.default, vertical = spacing.small)
        ) {
            HyperCapsuleGlassCard(
                shape = shapes.extraLarge,
                backgroundColor = Color.Black,
                borderColor = colors.borderHighlight,
                contentPadding = spacing.medium
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    SharedThemePreview(
                        viewModel.selectedColorHex,
                        (viewModel.colorMode == ColorMode.APP_ICON),
                        viewModel.selectedShapeId,
                        viewModel.iconPaddingPercent,
                        viewModel.callAnswerColor,
                        viewModel.callDeclineColor,
                        viewModel.callAnswerShapeId,
                        viewModel.callDeclineShapeId
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.default)
        ) {
            HyperCapsuleActionButton(
                text = stringResource(R.string.creator_btn_edit_info),
                icon = Icons.Outlined.Edit,
                onClick = onEditSettings,
                isPrimary = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(spacing.default))

            val menuItems = listOf(
                CreatorRoute.BEHAVIOR_MENU,
                CreatorRoute.COLORS,
                CreatorRoute.ICONS,
                CreatorRoute.CALLS,
                CreatorRoute.NAVIGATION,
                CreatorRoute.REPLY,
                CreatorRoute.ACTIONS,
                CreatorRoute.APPS
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                menuItems.forEach { route ->
                    val icon = when (route) {
                        CreatorRoute.BEHAVIOR_MENU -> Icons.Outlined.DisplaySettings
                        CreatorRoute.COLORS -> Icons.Outlined.ColorLens
                        CreatorRoute.ICONS -> Icons.Outlined.Widgets
                        CreatorRoute.CALLS -> Icons.Outlined.Call
                        CreatorRoute.NAVIGATION -> Icons.Outlined.Map
                        CreatorRoute.REPLY -> Icons.Outlined.Edit
                        CreatorRoute.ACTIONS -> Icons.Outlined.TouchApp
                        CreatorRoute.APPS -> Icons.Outlined.Apps
                        else -> Icons.Outlined.Image
                    }
                    val title = when (route) {
                        CreatorRoute.BEHAVIOR_MENU -> stringResource(R.string.engine)
                        CreatorRoute.COLORS -> stringResource(R.string.creator_nav_colors)
                        CreatorRoute.ICONS -> stringResource(R.string.creator_nav_icons)
                        CreatorRoute.CALLS -> stringResource(R.string.creator_nav_calls)
                        CreatorRoute.NAVIGATION -> stringResource(R.string.nav_layout_title)
                        CreatorRoute.REPLY -> stringResource(R.string.inline_reply_title)
                        CreatorRoute.ACTIONS -> stringResource(R.string.creator_nav_actions)
                        CreatorRoute.APPS -> stringResource(R.string.creator_nav_apps)
                        else -> stringResource(R.string.app_name)
                    }
                    val sub = when (route) {
                        CreatorRoute.BEHAVIOR_MENU -> stringResource(R.string.engine_timeouts_triggers)
                        CreatorRoute.COLORS -> stringResource(R.string.creator_sub_colors)
                        CreatorRoute.ICONS -> stringResource(R.string.creator_sub_icons)
                        CreatorRoute.CALLS -> stringResource(R.string.creator_sub_calls)
                        CreatorRoute.NAVIGATION -> stringResource(R.string.nav_layout_desc)
                        CreatorRoute.REPLY -> stringResource(R.string.customize_inline_reply)
                        CreatorRoute.ACTIONS -> stringResource(R.string.creator_sub_actions)
                        CreatorRoute.APPS -> stringResource(R.string.creator_sub_apps)
                        else -> stringResource(R.string.app_name)
                    }

                    val trailing: (@Composable () -> Unit)? = when (route) {
                        CreatorRoute.COLORS -> {
                            {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(safeParseColor(viewModel.selectedColorHex))
                                        .border(1.dp, colors.borderHighlight, CircleShape)
                                )
                            }
                        }
                        CreatorRoute.ICONS -> {
                            {
                                Icon(
                                    Icons.Outlined.Image,
                                    null,
                                    tint = colors.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        else -> null
                    }

                    CreatorOptionCard(
                        title = title,
                        subtitle = sub,
                        icon = icon,
                        onClick = { onNavigate(route) },
                        trailingContent = trailing
                    )
                }
            }
            Spacer(Modifier.height(48.dp))
        }
    }
}

@Composable
fun CreatorOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    shape: androidx.compose.ui.graphics.Shape = LocalHyperCapsuleShapes.current.large,
    onClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    HyperCapsuleGlassCard(
        onClick = onClick,
        shape = shape,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 76.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(shapes.medium)
                    .background(colors.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(spacing.default))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }

            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = colors.textTertiary
                )
            }
        }
    }
}

@Composable
fun DetailScreenShell(
    previewContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.default, vertical = spacing.small)
        ) {
            HyperCapsuleGlassCard(
                shape = shapes.extraLarge,
                backgroundColor = Color.Black,
                borderColor = colors.borderHighlight,
                contentPadding = spacing.medium
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    previewContent()
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeMetadataSheet(viewModel: ThemeViewModel, onDismiss: () -> Unit) {
    val fm = LocalFocusManager.current
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val iconLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) { viewModel.themeIconUri = uri }
    }
    var iconBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    LaunchedEffect(viewModel.themeIconUri) {
        if (viewModel.themeIconUri != null) {
            withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(viewModel.themeIconUri!!)?.use { stream ->
                        val bmp = BitmapFactory.decodeStream(stream)
                        iconBitmap = bmp?.asImageBitmap()
                    }
                } catch (e: Exception) { e.printStackTrace(); iconBitmap = null }
            }
        } else { iconBitmap = null }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surfaceBase,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = spacing.medium)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(shapes.pill)
                    .background(colors.borderHighlight)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(spacing.default)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.meta_title),
                style = typography.titleLarge,
                color = colors.textPrimary
            )
            Spacer(Modifier.height(spacing.large))

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(shapes.large)
                    .background(colors.surfaceElevatedGlass)
                    .border(1.dp, colors.borderHighlight, shapes.large)
                    .tactilePress(onClick = {
                        iconLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }),
                contentAlignment = Alignment.Center
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap!!,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(Modifier.height(spacing.default))

            if (viewModel.themeIconUri != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HyperCapsuleActionButton(
                        text = "Remove",
                        icon = Icons.Rounded.Delete,
                        onClick = { viewModel.themeIconUri = null },
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )
                    HyperCapsuleActionButton(
                        text = stringResource(R.string.change_icon),
                        icon = Icons.Rounded.Edit,
                        onClick = { iconLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        isPrimary = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                HyperCapsuleActionButton(
                    text = stringResource(R.string.select_icon),
                    icon = Icons.Rounded.Image,
                    onClick = { iconLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(spacing.large))

            OutlinedTextField(
                value = viewModel.themeName,
                onValueChange = { viewModel.themeName = it },
                label = { Text(stringResource(R.string.meta_label_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { fm.clearFocus() }),
                shape = shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.borderHighlight
                )
            )

            Spacer(Modifier.height(spacing.medium))

            OutlinedTextField(
                value = viewModel.themeAuthor,
                onValueChange = { viewModel.themeAuthor = it },
                label = { Text(stringResource(R.string.meta_label_author)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardActions = KeyboardActions(onDone = { fm.clearFocus() }),
                shape = shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.borderHighlight
                )
            )

            Spacer(Modifier.height(spacing.medium))

            OutlinedTextField(
                value = viewModel.themeDescription,
                onValueChange = { viewModel.themeDescription = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.borderHighlight
                )
            )

            Spacer(Modifier.height(spacing.large))

            HyperCapsuleActionButton(
                text = stringResource(R.string.meta_action_done),
                onClick = onDismiss,
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(spacing.large))
        }
    }
}
