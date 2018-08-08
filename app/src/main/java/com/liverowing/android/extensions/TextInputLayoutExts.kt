package com.liverowing.android.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.intOrError(default: Int? = 0, message: String?): Int {
    try {
        return this.editText!!.text.toString().toInt()
    } catch (e: Exception) {}

    if (default == null) {
        this.error = message
    } else {
        this.editText!!.setText(default.toString())
        return default
    }

    return 0
}

fun TextInputLayout.doubleOrError(default: Double? = 0.0, message: String?): Double {
    try {
        return this.editText!!.text.toString().toDouble()
    } catch (e: Exception) {}

    if (default == null) {
        this.error = message
    } else {
        this.editText!!.setText(default.toString())
        return default
    }

    return 0.0
}