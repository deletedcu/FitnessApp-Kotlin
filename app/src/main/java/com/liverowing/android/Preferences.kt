package com.liverowing.android

import android.content.Context
import android.content.SharedPreferences

object Preferences {
    private const val NAME = "LiveRowing"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val LAST_PARSE_CONFIG_FETCH = Pair("last_parse_config_fetch", 0L)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var lastParseConfigFetch: Long
        get() = preferences.getLong(LAST_PARSE_CONFIG_FETCH.first, LAST_PARSE_CONFIG_FETCH.second)
        set(value) = preferences.edit {
            it.putLong(LAST_PARSE_CONFIG_FETCH.first, value)
        }
}