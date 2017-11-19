package com.liverowing.liverowing.extensions

import android.content.SharedPreferences

/**
 * Created by henrikmalmberg on 2017-11-10.
 */
inline fun SharedPreferences.edit(func: SharedPreferences.Editor.() -> Unit) {
    val editor = edit()
    editor.func()
    editor.apply()
}