package com.hypercapsule.app.models

import androidx.annotation.StringRes
import com.hypercapsule.app.R

enum class WidgetRenderMode(
    @StringRes val labelRes: Int,
    @StringRes val descriptionRes: Int
) {
    INTERACTIVE(R.string.render_interactive_label, R.string.render_interactive_desc),
    SNAPSHOT(R.string.render_snapshot_label, R.string.render_snapshot_desc)
}