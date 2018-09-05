package com.liverowing.android.extensions

import android.view.View

fun View.toggleVisibility() : View {
    visibility = if (visibility == View.VISIBLE) {
        View.GONE
    } else {
        View.VISIBLE
    }
    return this
}