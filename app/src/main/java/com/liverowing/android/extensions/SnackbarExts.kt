package com.liverowing.android.extensions

import android.support.design.widget.Snackbar
import android.view.View

/**
 * Created by henrikmalmberg on 2017-10-18.
 */
fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}