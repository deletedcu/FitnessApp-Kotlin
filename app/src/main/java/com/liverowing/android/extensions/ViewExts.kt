package com.liverowing.android.extensions

import android.view.View

fun View.toggleVisibility() : View {
    visibility = if (visibility == View.VISIBLE) {
        View.INVISIBLE
    } else {
        View.INVISIBLE
    }
    return this
}