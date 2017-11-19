package com.liverowing.liverowing.extensions

import android.support.design.widget.Snackbar
import android.view.View

/**
 * Created by henrikmalmberg on 2017-10-18.
 */
inline fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snack = Snackbar.make(this, message, length)
    snack.f()
    snack.show()
}

fun View.toggle() {
    visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
}