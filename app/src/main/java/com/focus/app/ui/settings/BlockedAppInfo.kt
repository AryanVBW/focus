package com.focus.app.ui.settings

import android.graphics.drawable.Drawable

data class BlockedAppInfo(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    var isBlocked: Boolean
)
