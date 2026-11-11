package com.hypercapsule.app.ui.screens.theme

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hypercapsule.app.R
import com.hypercapsule.app.data.theme.ThemeRepository
import com.hypercapsule.app.models.theme.HyperTheme
import com.hypercapsule.app.models.theme.ResourceType
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeManagerScreen(
    onBack: () -> Unit,
    onFindThemes: () -> Unit,
    onCreateTheme: () -> Unit,
    onEditTheme: (String) -> Unit
) {
    val viewModel: ThemeViewModel = viewModel()
    val installedThemes by viewModel.installedThemes.collectAsState()
    val activeId by viewModel.activeThemeId.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val themeRepo = remember { ThemeRepository(context.applicationContext) }

    var showAddOptions by remember { mutableStateOf(false) }
    var themeIcons by remember { mutableStateOf<Map<String, ImageBitmap?>>(emptyMap()) }
    val sheetState = rememberModalBottomSheetState()

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val intent = Intent(context, ThemeInstallerActivity::class.java).apply {
                data = uri
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(intent)
            showAddOptions = false
        }
    }

    LaunchedEffect(installedThemes) {
        withContext(Dispatchers.IO) {
            val iconMap = mutableMapOf<String, ImageBitmap?>()
            installedThemes.forEach { theme ->
                val iconRes = theme.meta.customIcon
                if (iconRes != null && iconRes.type == ResourceType.LOCAL_FILE) {
                    try {
                        val file = File(themeRepo.getThemesDir(), "${theme.id}/${iconRes.value}")
                        if (file.exists()) {
                            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                            if (bitmap != null) {
                                iconMap[theme.id] = bitmap.asImageBitmap()
                            }
                        }
                    } catch (e: Exception) { e.printStackTrace() }
                }
            }
            themeIcons = iconMap
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshThemes()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.theme_manager_title),
                onBackClick = onBack
            )

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(
                    start = spacing.default,
                    end = spacing.default,
                    top = spacing.small,
                    bottom = 90.dp
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    SystemDefaultCard(
                        isActive = activeId == null,
                        onClick = { viewModel.resetToDefault() }
                    )
                }

                items(installedThemes) { theme ->
                    ThemeCard(
                        theme = theme,
                        customIcon = themeIcons[theme.id],
                        isActive = activeId == theme.id,
                        onClick = { viewModel.applyTheme(theme) },
                        onDelete = { viewModel.deleteTheme(theme) },
                        onExport = { viewModel.exportAndShareTheme(theme) },
                        onEdit = { onEditTheme(theme.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddOptions = true },
            containerColor = colors.primary,
            contentColor = Color.Black,
            shape = shapes.large,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(spacing.default)
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = stringResource(R.string.theme_manager_cd_add)
            )
        }
    }

    if (showAddOptions) {
        ModalBottomSheet(
            onDismissRequest = { showAddOptions = false },
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
                    .fillMaxWidth()
                    .padding(start = spacing.default, end = spacing.default, bottom = spacing.huge)
            ) {
                Text(
                    text = stringResource(R.string.theme_sheet_title),
                    style = LocalHyperCapsuleTypography.current.titleLarge,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(bottom = spacing.large)
                )

                HyperCapsuleActionButton(
                    text = stringResource(R.string.theme_sheet_action_create),
                    icon = Icons.Rounded.Brush,
                    onClick = {
                        showAddOptions = false
                        onCreateTheme()
                    },
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    HyperCapsuleActionButton(
                        text = stringResource(R.string.theme_sheet_action_find),
                        icon = Icons.Rounded.Search,
                        onClick = {
                            showAddOptions = false
                            onFindThemes()
                        },
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )

                    HyperCapsuleActionButton(
                        text = stringResource(R.string.theme_action_import_short),
                        icon = Icons.Rounded.FolderOpen,
                        onClick = {
                            importLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
                        },
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeCard(
    theme: HyperTheme,
    customIcon: ImageBitmap? = null,
    isActive: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onExport: () -> Unit,
    onEdit: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    HyperCapsuleGlassCard(
        onClick = onClick,
        shape = shapes.large,
        borderColor = if (isActive) colors.primary else colors.borderHighlight,
        backgroundColor = if (isActive) colors.primary.copy(alpha = 0.08f) else colors.surfaceElevatedGlass,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (customIcon != null) {
                    Image(
                        bitmap = customIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(shapes.medium)
                            .background(parseColor(theme.global.highlightColor))
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                if (isActive) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = stringResource(R.string.theme_card_cd_active),
                        tint = colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Text(
                text = theme.meta.name,
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(R.string.theme_card_author_format, theme.meta.author),
                style = typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (!theme.meta.lockedForEditing) {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.theme_card_action_edit),
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }

                val hasCustomLink = !theme.meta.customShareLink.isNullOrEmpty()
                IconButton(
                    onClick = {
                        if (hasCustomLink) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, theme.meta.customShareLink.toUri())
                                context.startActivity(intent)
                            } catch (e: Exception) { e.printStackTrace() }
                        } else {
                            onExport()
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (hasCustomLink) Icons.Rounded.Link else Icons.Rounded.Share,
                        contentDescription = stringResource(R.string.theme_card_action_export),
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(R.string.theme_card_cd_delete),
                        tint = colors.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SystemDefaultCard(
    isActive: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    HyperCapsuleGlassCard(
        onClick = onClick,
        shape = shapes.large,
        borderColor = if (isActive) colors.primary else colors.borderHighlight,
        backgroundColor = if (isActive) colors.primary.copy(alpha = 0.08f) else colors.surfaceElevatedGlass,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(shapes.medium)
                        .background(colors.surfaceElevatedGlass)
                        .border(1.dp, colors.borderHighlight, shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PhoneAndroid,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (isActive) {
                    Icon(
                        imageVector = Icons.Rounded.CheckCircle,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(spacing.medium))
            Text(
                text = stringResource(R.string.theme_system_default_title),
                style = typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = stringResource(R.string.theme_system_default_desc),
                style = typography.bodySmall,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

fun parseColor(hex: String?): Color {
    return try {
        if (hex.isNullOrEmpty()) Color(0xFF00C7FF) else Color(hex.toColorInt())
    } catch (e: Exception) { Color(0xFF00C7FF) }
}
