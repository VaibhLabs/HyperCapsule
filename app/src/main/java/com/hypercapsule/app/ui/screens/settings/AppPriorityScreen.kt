package com.hypercapsule.app.ui.screens.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hypercapsule.app.R
import com.hypercapsule.app.data.AppPreferences
import com.hypercapsule.app.ui.designsystem.color.LocalHyperCapsuleColors
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleGlassCard
import com.hypercapsule.app.ui.designsystem.components.HyperCapsuleTopBar
import com.hypercapsule.app.ui.designsystem.shapes.LocalHyperCapsuleShapes
import com.hypercapsule.app.ui.designsystem.spacing.LocalHyperCapsuleSpacing
import com.hypercapsule.app.ui.designsystem.typography.LocalHyperCapsuleTypography
import com.hypercapsule.app.util.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PriorityAppInfo(val name: String, val packageName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppPriorityScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { AppPreferences(context) }
    val packageManager = context.packageManager

    val colors = LocalHyperCapsuleColors.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val enabledPackages: Set<String> by preferences.allowedPackagesFlow.collectAsState(initial = emptySet())
    val savedOrder: List<String> by preferences.appPriorityListFlow.collectAsState(initial = emptyList())

    val displayList = remember { mutableStateListOf<PriorityAppInfo>() }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(enabledPackages, savedOrder) {
        if (enabledPackages.isEmpty()) {
            displayList.clear()
            isLoaded = true
            return@LaunchedEffect
        }

        withContext(Dispatchers.IO) {
            val ordered = savedOrder.filter { enabledPackages.contains(it) }
            val newApps = enabledPackages.filter { !savedOrder.contains(it) }
            val finalPackageList = ordered + newApps

            val infoList = finalPackageList.map { pkg ->
                try {
                    val appInfo = packageManager.getApplicationInfo(pkg, 0)
                    val name = packageManager.getApplicationLabel(appInfo).toString()
                    PriorityAppInfo(name, pkg)
                } catch (_: Exception) {
                    PriorityAppInfo(pkg, pkg)
                }
            }

            withContext(Dispatchers.Main) {
                displayList.clear()
                displayList.addAll(infoList)
                isLoaded = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HyperCapsuleTopBar(
                title = stringResource(R.string.priority_order),
                onBackClick = onBack
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = spacing.default)
            ) {
                Spacer(modifier = Modifier.height(spacing.small))

                Text(
                    text = stringResource(R.string.priority_order_desc),
                    style = typography.bodyMedium,
                    color = colors.textSecondary
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                if (!isLoaded) {
                    Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = colors.primary)
                    }
                } else if (displayList.isEmpty()) {
                    Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.no_active_bridges), color = colors.error)
                    }
                } else {
                    DraggableLazyList(
                        items = displayList,
                        onMove = { from, to ->
                            displayList.apply { add(to, removeAt(from)) }
                        },
                        onDragEnd = {
                            scope.launch {
                                preferences.setAppPriorityOrder(displayList.map { it.packageName })
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DraggableLazyList(
    items: List<PriorityAppInfo>,
    onMove: (Int, Int) -> Unit,
    onDragEnd: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current

    var draggingIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableFloatStateOf(0f) }

    val density = LocalDensity.current
    val scrollThreshold = with(density) { 60.dp.toPx() }
    var scrollVelocity by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(scrollVelocity) {
        if (scrollVelocity != 0f) {
            while (true) {
                listState.scrollBy(scrollVelocity)
                delay(16)
                if (scrollVelocity == 0f) break
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp)
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        listState.layoutInfo.visibleItemsInfo
                            .firstOrNull { item -> offset.y.toInt() in item.offset..(item.offset + item.size) }
                            ?.let {
                                draggingIndex = it.index
                                dragOffset = 0f
                            }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount.y

                        val viewportHeight = listState.layoutInfo.viewportSize.height
                        val touchY = change.position.y

                        scrollVelocity = if (touchY < scrollThreshold) {
                            if (listState.canScrollBackward && dragAmount.y <= 0) -20f else 0f
                        } else if (touchY > viewportHeight - scrollThreshold) {
                            if (listState.canScrollForward && dragAmount.y >= 0) 20f else 0f
                        } else {
                            0f
                        }

                        val currentIndex = draggingIndex ?: return@detectDragGesturesAfterLongPress
                        val currentItemInfo = listState.layoutInfo.visibleItemsInfo
                            .firstOrNull { it.index == currentIndex }

                        if (currentItemInfo != null) {
                            val itemHeight = currentItemInfo.size
                            val threshold = itemHeight * 0.5f

                            if (dragOffset > threshold) {
                                if (currentIndex < items.lastIndex) {
                                    onMove(currentIndex, currentIndex + 1)
                                    draggingIndex = currentIndex + 1
                                    dragOffset -= itemHeight
                                }
                            } else if (dragOffset < -threshold) {
                                if (currentIndex > 0) {
                                    onMove(currentIndex, currentIndex - 1)
                                    draggingIndex = currentIndex - 1
                                    dragOffset += itemHeight
                                }
                            }
                        }
                    },
                    onDragEnd = {
                        draggingIndex = null
                        dragOffset = 0f
                        scrollVelocity = 0f
                        onDragEnd()
                    },
                    onDragCancel = {
                        draggingIndex = null
                        dragOffset = 0f
                        scrollVelocity = 0f
                    }
                )
            },
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        itemsIndexed(items, key = { _, item -> item.packageName }) { index, item ->
            val isDragging = index == draggingIndex

            val elevation by animateDpAsState(if (isDragging) 12.dp else 0.dp, label = "elevation")
            val scale by animateFloatAsState(if (isDragging) 1.04f else 1f, label = "scale")
            val alpha = if (isDragging) 0.92f else 1f
            val areButtonsEnabled = draggingIndex == null

            Box(
                modifier = Modifier
                    .zIndex(if (isDragging) 1f else 0f)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                        translationY = if (isDragging) dragOffset else 0f
                    }
                    .shadow(elevation, shapes.medium)
                    .then(if (!isDragging) Modifier.animateItem() else Modifier)
            ) {
                PriorityAppItem(
                    rank = index + 1,
                    app = item,
                    isFirst = index == 0,
                    isLast = index == items.lastIndex,
                    areButtonsEnabled = areButtonsEnabled,
                    onMoveUp = {
                        if (index > 0) {
                            onMove(index, index - 1)
                            onDragEnd()
                            scope.launch { listState.animateScrollToItem(index - 1) }
                        }
                    },
                    onMoveDown = {
                        if (index < items.lastIndex) {
                            onMove(index, index + 1)
                            onDragEnd()
                            scope.launch { listState.animateScrollToItem(index + 1) }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PriorityAppItem(
    rank: Int,
    app: PriorityAppInfo,
    isFirst: Boolean,
    isLast: Boolean,
    areButtonsEnabled: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    val colors = LocalHyperCapsuleColors.current
    val shapes = LocalHyperCapsuleShapes.current
    val spacing = LocalHyperCapsuleSpacing.current
    val typography = LocalHyperCapsuleTypography.current

    val moveUpLabel = stringResource(R.string.cd_move_up)
    val moveDownLabel = stringResource(R.string.cd_move_down)
    val handleDesc = stringResource(R.string.cd_drag_handle)
    val appIconDesc = stringResource(R.string.cd_app_icon, app.name)
    val buttonTint = if (areButtonsEnabled) colors.primary else Color.Transparent

    HyperCapsuleGlassCard(
        shape = shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                stateDescription = "Priority $rank"
                customActions = buildList {
                    if (areButtonsEnabled && !isFirst) {
                        add(CustomAccessibilityAction(label = moveUpLabel) { onMoveUp(); true })
                    }
                    if (areButtonsEnabled && !isLast) {
                        add(CustomAccessibilityAction(label = moveDownLabel) { onMoveDown(); true })
                    }
                }
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = handleDesc,
                tint = if (areButtonsEnabled) colors.textTertiary else Color.Gray,
                modifier = Modifier.size(36.dp).padding(4.dp)
            )

            Text(
                text = "#$rank",
                style = typography.labelLarge,
                color = colors.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(36.dp)
            )

            AppIconLoader(packageName = app.packageName, contentDescription = appIconDesc)

            Spacer(modifier = Modifier.width(spacing.medium))

            Text(
                text = app.name,
                modifier = Modifier.weight(1f),
                style = typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )

            IconButton(onClick = onMoveUp, modifier = Modifier.size(32.dp), enabled = areButtonsEnabled && !isFirst) {
                if (!isFirst) Icon(Icons.Default.ArrowUpward, moveUpLabel, tint = buttonTint, modifier = Modifier.size(18.dp))
            }
            IconButton(onClick = onMoveDown, modifier = Modifier.size(32.dp), enabled = areButtonsEnabled && !isLast) {
                if (!isLast) Icon(Icons.Default.ArrowDownward, moveDownLabel, tint = buttonTint, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun AppIconLoader(packageName: String, contentDescription: String) {
    val context = LocalContext.current
    val shapes = LocalHyperCapsuleShapes.current
    val colors = LocalHyperCapsuleColors.current
    var iconBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(packageName) {
        withContext(Dispatchers.IO) {
            try {
                val drawable = context.packageManager.getApplicationIcon(packageName)
                iconBitmap = drawable.toBitmap()
            } catch (_: Exception) { }
        }
    }

    if (iconBitmap != null) {
        Image(
            bitmap = iconBitmap!!.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(36.dp)
                .clip(shapes.small)
        )
    } else {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(shapes.small)
                .background(colors.surfaceElevatedGlass),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Android,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp),
                tint = colors.primary
            )
        }
    }
}
