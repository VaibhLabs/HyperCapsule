package com.hypercapsule.app.models

data class WidgetConfig(
    // Appearance
    val size: WidgetSize = WidgetSize.MEDIUM,
    val renderMode: WidgetRenderMode = WidgetRenderMode.INTERACTIVE,

    // Behavior
    val isShowShade: Boolean = true,
    val timeout: Int = 0,

    // Automation
    val autoUpdate: Boolean = false,
    val updateIntervalMinutes: Int = 15
) {
    val timeoutMs: Int?
        get() = if (timeout > 0) (timeout.toLong() * 1000L).coerceAtMost(86_400_000L).toInt() else null
}