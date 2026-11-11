package com.hypercapsule.app.ui.screens.design

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalCenteredHeroCarousel
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.data.theme.ThemeRepository
import com.hypercapsule.app.data.widget.WidgetManager
import com.hypercapsule.app.models.theme.CallModule
import com.hypercapsule.app.models.theme.GlobalConfig
import com.hypercapsule.app.models.theme.HyperTheme
import com.hypercapsule.app.models.theme.ResourceType
import com.hypercapsule.app.models.theme.ThemeMetadata
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleActionButton
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleSectionHeader
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.motion.tactilePress
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

// --- 1. STATEFUL COMPOSABLE (Logic Layer) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignScreen(
    onNavigateToWidgets: () -> Unit,
    onNavigateToThemes: () -> Unit,
    onEditTheme: (String) -> Unit,
    onLaunchPicker: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    val preferences = remember { AppPreferences(context.applicationContext) }
    val themeRepo = remember { ThemeRepository(context.applicationContext) }

    val activeThemeId by preferences.activeThemeIdFlow.collectAsState(initial = null)
    val savedWidgetIds by preferences.savedWidgetIdsFlow.collectAsState(initial = emptyList())

    var availableThemes by remember { mutableStateOf<List<HyperTheme>>(emptyList()) }
    var widgetIcons by remember { mutableStateOf<List<Drawable>>(emptyList()) }
    var themeIcons by remember { mutableStateOf<Map<String, ImageBitmap?>>(emptyMap()) }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    LaunchedEffect(Unit) {
        val themes = themeRepo.getAvailableThemes()
        availableThemes = themes

        withContext(Dispatchers.IO) {
            val iconMap = mutableMapOf<String, ImageBitmap?>()
            themes.forEach { theme ->
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

    LaunchedEffect(savedWidgetIds) {
        if (savedWidgetIds.isNotEmpty()) {
            withContext(Dispatchers.IO) {
                val icons = savedWidgetIds.take(5).mapNotNull { id ->
                    val info = WidgetManager.getWidgetInfo(context, id)
                    try {
                        val pkg = info?.provider?.packageName
                        if (pkg != null) context.packageManager.getApplicationIcon(pkg) else null
                    } catch (_: Exception) { null }
                }
                widgetIcons = icons
            }
        } else {
            widgetIcons = emptyList()
        }
    }

    DesignScreenContent(
        activeThemeId = activeThemeId,
        availableThemes = availableThemes,
        themeIcons = themeIcons,
        savedWidgetCount = savedWidgetIds.size,
        widgetIcons = widgetIcons,
        onNavigateToWidgets = onNavigateToWidgets,
        onNavigateToThemes = onNavigateToThemes,
        onEditTheme = onEditTheme,
        onFabClick = { showBottomSheet = true },
        onSettingsClick = onSettingsClick
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
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
                    .padding(bottom = spacing.huge, start = spacing.default, end = spacing.default),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.design_add_to_island),
                    style = LocalHyperCapsuleTypography.current.titleLarge,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(bottom = spacing.large)
                )

                HyperCapsuleActionButton(
                    text = stringResource(R.string.design_system_widget_beta),
                    icon = Icons.Rounded.Widgets,
                    onClick = {
                        showBottomSheet = false
                        onLaunchPicker()
                    },
                    isPrimary = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                HyperCapsuleActionButton(
                    text = stringResource(R.string.design_get_themes),
                    icon = Icons.Rounded.Palette,
                    onClick = {
                        showBottomSheet = false
                        onNavigateToThemes()
                    },
                    isPrimary = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// --- 2. STATELESS COMPOSABLE (UI Layer) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignScreenContent(
    activeThemeId: String?,
    availableThemes: List<HyperTheme>,
    themeIcons: Map<String, ImageBitmap?>,
    savedWidgetCount: Int,
    widgetIcons: List<Drawable>,
    onNavigateToWidgets: () -> Unit,
    onNavigateToThemes: () -> Unit,
    onEditTheme: (String) -> Unit,
    onFabClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.app_name),
                subtitle = stringResource(R.string.design),
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(shapes.small)
                            .background(colors.surfaceElevatedGlass)
                            .tactilePress(onClick = onSettingsClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(R.string.settings),
                            tint = colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(spacing.small))

            HeroSection()

            Spacer(modifier = Modifier.height(spacing.large))

            Column(modifier = Modifier.fillMaxWidth()) {
                HyperCapsuleSectionHeader(
                    title = stringResource(R.string.design_section_themes),
                    actionText = stringResource(R.string.see_all_action),
                    onActionClick = onNavigateToThemes
                )
                ThemesCarousel(
                    themes = availableThemes,
                    themeIcons = themeIcons,
                    activeId = activeThemeId,
                    onNavigateToThemes = onNavigateToThemes,
                    onEditTheme = onEditTheme
                )
            }

            Spacer(modifier = Modifier.height(spacing.large))

            Column(modifier = Modifier.fillMaxWidth()) {
                HyperCapsuleSectionHeader(
                    title = stringResource(R.string.design_section_widgets),
                    actionText = stringResource(R.string.see_all_action),
                    onActionClick = onNavigateToWidgets
                )
                WidgetsCarousel(
                    savedCount = savedWidgetCount,
                    icons = widgetIcons,
                    onNavigateToWidgets = onNavigateToWidgets,
                    onAddWidget = onFabClick
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        FloatingActionButton(
            onClick = onFabClick,
            containerColor = colors.primary,
            contentColor = Color.Black,
            shape = shapes.large,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = spacing.default)
        ) {
            Icon(Icons.Rounded.Add, contentDescription = stringResource(R.string.design_add_design))
        }
    }
}

// --- SECTIONS ---

data class HeroItem(val title: String, val subtitle: String, val color: Color, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroSection() {
    val items = listOf(
        HeroItem(stringResource(R.string.design_hero_customization_title), stringResource(R.string.design_hero_customization_subtitle), Color(0xFF00C7FF)) {},
        HeroItem(stringResource(R.string.design_hero_pro_title), stringResource(R.string.design_hero_pro_subtitle), Color(0xFF0A84FF)) {},
        HeroItem(stringResource(R.string.design_hero_community_title), stringResource(R.string.design_hero_community_subtitle), Color(0xFF9D4EDD)) {}
    )

    val state = rememberCarouselState { items.size }

    HorizontalCenteredHeroCarousel(
        modifier = Modifier.fillMaxWidth(),
        state = state,
        itemSpacing = 12.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val item = items[i]
        HeroCard(item = item)
    }
}

@Composable
fun HeroCard(item: HeroItem, modifier: Modifier = Modifier) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val typography = LocalHyperCapsuleTypography.current
    val spacing = LocalHyperCapsuleSpacing.current

    HyperCapsuleGlassCard(
        modifier = modifier
            .height(180.dp)
            .fillMaxWidth(),
        shape = shapes.extraLarge,
        borderColor = item.color.copy(alpha = 0.35f),
        backgroundColor = colors.surfaceElevatedGlass,
        onClick = item.onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                item.color.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 24.dp, y = 24.dp)
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(item.color.copy(alpha = 0.12f))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(spacing.default)
            ) {
                Text(
                    text = item.title,
                    style = typography.titleLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.subtitle,
                    style = typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemesCarousel(
    themes: List<HyperTheme>,
    themeIcons: Map<String, ImageBitmap?>,
    activeId: String?,
    onNavigateToThemes: () -> Unit,
    onEditTheme: (String) -> Unit
) {
    val displayThemes = themes.take(5)
    val totalCount = 1 + displayThemes.size + 1
    val state = rememberCarouselState { totalCount }

    HorizontalMultiBrowseCarousel(
        modifier = Modifier.fillMaxWidth(),
        state = state,
        preferredItemWidth = 160.dp,
        itemSpacing = 12.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { i ->
        val isSystemDefault = i == 0
        val isAction = i > displayThemes.size

        if (isAction) {
            ThemePreviewCard(
                title = stringResource(R.string.design_browse_more),
                subtitle = "",
                color = LocalHyperCapsuleColors.current.primary,
                isActive = false,
                icon = Icons.Rounded.Add,
                isAction = true,
                onClick = onNavigateToThemes
            )
        } else if (isSystemDefault) {
            ThemePreviewCard(
                title = stringResource(R.string.theme_system_default_title),
                subtitle = stringResource(R.string.theme_system_default_desc),
                color = LocalHyperCapsuleColors.current.secondary,
                isActive = activeId == null,
                icon = Icons.Rounded.PhoneAndroid,
                onClick = onNavigateToThemes
            )
        } else {
            val theme = displayThemes[i - 1]
            val color = try {
                Color((theme.global.highlightColor ?: "#00C7FF").toColorInt())
            } catch (_: Exception) { LocalHyperCapsuleColors.current.primary }

            val customIcon = themeIcons[theme.id]

            ThemePreviewCard(
                title = theme.meta.name,
                subtitle = stringResource(R.string.theme_card_author_format, theme.meta.author),
                color = color,
                isActive = theme.id == activeId,
                icon = Icons.Rounded.Palette,
                customIcon = customIcon,
                onClick = { onEditTheme(theme.id) }
            )
        }
    }
}

@Composable
fun ThemePreviewCard(
    title: String,
    subtitle: String,
    color: Color,
    isActive: Boolean,
    icon: ImageVector,
    customIcon: ImageBitmap? = null,
    isAction: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    HyperCapsuleGlassCard(
        modifier = modifier.height(180.dp),
        shape = shapes.large,
        borderColor = if (isActive) colors.primary else colors.borderHighlight,
        backgroundColor = if (isActive) colors.primary.copy(alpha = 0.08f) else colors.surfaceElevatedGlass,
        onClick = onClick
    ) {
        if (isAction) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(shapes.medium)
                            .background(colors.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = colors.primary
                        )
                    }
                    Spacer(Modifier.height(spacing.small))
                    Text(
                        text = title,
                        style = typography.labelMedium,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.small),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (customIcon != null) {
                        Image(
                            bitmap = customIcon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(shapes.medium)
                                .background(color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp),
                                tint = color
                            )
                        }
                    }

                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clip(shapes.pill)
                                .background(colors.primary)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Active",
                                style = typography.labelSmall,
                                color = Color.Black
                            )
                        }
                    }
                }

                Column {
                    Text(
                        text = title,
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = subtitle,
                        style = typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetsCarousel(
    savedCount: Int,
    icons: List<Drawable>,
    onNavigateToWidgets: () -> Unit,
    onAddWidget: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    if (savedCount == 0) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            HyperCapsuleGlassCard(
                onClick = onAddWidget,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(shapes.medium)
                            .background(colors.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Widgets,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = colors.primary
                        )
                    }
                    Spacer(Modifier.height(spacing.medium))
                    Text(
                        text = stringResource(R.string.design_empty_widget_title),
                        style = typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
            }
        }
    } else {
        val displayCount = icons.size
        val totalCount = displayCount + 1
        val state = rememberCarouselState { totalCount }

        HorizontalMultiBrowseCarousel(
            modifier = Modifier.fillMaxWidth(),
            state = state,
            preferredItemWidth = 140.dp,
            itemSpacing = 12.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { i ->
            if (i < displayCount) {
                val icon = icons[i]
                WidgetPreviewCard(
                    label = stringResource(R.string.widget_id_fmt, i + 1),
                    icon = icon,
                    isAction = false,
                    onClick = onNavigateToWidgets
                )
            } else {
                WidgetPreviewCard(
                    label = stringResource(R.string.design_add_new),
                    icon = null,
                    isAction = true,
                    onClick = onAddWidget
                )
            }
        }
    }
}

@Composable
fun WidgetPreviewCard(
    label: String,
    icon: Drawable?,
    isAction: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    HyperCapsuleGlassCard(
        modifier = modifier.height(160.dp),
        shape = shapes.large,
        onClick = onClick
    ) {
        if (isAction) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(shapes.medium)
                            .background(colors.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = colors.primary
                        )
                    }
                    Spacer(Modifier.height(spacing.small))
                    Text(
                        text = label,
                        style = typography.labelMedium,
                        color = colors.textSecondary
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.small),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(shapes.medium)
                            .background(colors.surfaceElevatedGlass)
                            .border(1.dp, colors.borderHighlight, shapes.medium),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = icon.toBitmap().asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
                Spacer(Modifier.height(spacing.medium))
                Text(
                    text = label,
                    style = typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary
                )
            }
        }
    }
}
