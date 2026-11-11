package com.hypercapsule.app.models

data class IslandConfig(
    val isFloat: Boolean? = null,
    val isShowShade: Boolean? = null,
    val timeout: Int? = null,
    val floatTimeout: Int? = null,
    val removeOriginalNotification: Boolean? = null,
    val dismissWithOriginal: Boolean? = null,
    val enableInlineReply: Boolean? = null,
) {
    /**
     * Converts second-based [timeout] into milliseconds safely.
     * Guaranteed to prevent integer overflow and bounds to max 24 hours.
     */
    val timeoutMs: Int?
        get() = timeout?.let { if (it > 0) (it.toLong() * 1000L).coerceAtMost(86_400_000L).toInt() else null }

    /**
     * Converts second-based [floatTimeout] into milliseconds safely.
     */
    val floatTimeoutMs: Int?
        get() = floatTimeout?.let { if (it > 0) (it.toLong() * 1000L).coerceAtMost(86_400_000L).toInt() else null }

    // Merges this config (App) with a default config (Global)
    fun mergeWith(global: IslandConfig): IslandConfig {
        return IslandConfig(
            isFloat = this.isFloat ?: global.isFloat ?: false,
            isShowShade = this.isShowShade ?: global.isShowShade ?: false,
            timeout = this.timeout ?: global.timeout ?: 10,
            floatTimeout = this.floatTimeout ?: global.floatTimeout ?: 5,
            removeOriginalNotification = this.removeOriginalNotification ?: global.removeOriginalNotification ?: false,
            dismissWithOriginal = this.dismissWithOriginal ?: global.dismissWithOriginal ?: true,
            enableInlineReply = this.enableInlineReply ?: global.enableInlineReply ?: true,
        )
    }
}