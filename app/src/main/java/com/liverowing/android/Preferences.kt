package com.liverowing.android

import android.content.Context
import android.content.SharedPreferences

object Preferences {
    private const val NAME = "LiveRowing"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val LAST_PARSE_CONFIG_FETCH = Pair("last_parse_config_fetch", 0L)
    private val PRIMARY_METRIC_LEFT = Pair("primary_metric_left", 100)
    private val PRIMARY_METRIC_RIGHT = Pair("right", 200)
    private val SECONDARY_METRIC_LEFT = Pair("secondary_metric_left", 0)
    private val SECONDARY_METRIC_CENTER = Pair("secondary_metric_center", 1)
    private val SECONDARY_METRIC_RIGHT = Pair("secondary_metric_right", 2)

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

    var primaryMetricLeft: Int
        get() = preferences.getInt(PRIMARY_METRIC_LEFT.first, PRIMARY_METRIC_LEFT.second)
        set(value) = preferences.edit {
            it.putInt(PRIMARY_METRIC_LEFT.first, value)
        }

    var primaryMetricRight: Int
        get() = preferences.getInt(PRIMARY_METRIC_RIGHT.first, PRIMARY_METRIC_RIGHT.second)
        set(value) = preferences.edit {
            it.putInt(PRIMARY_METRIC_RIGHT.first, value)
        }

    var secondaryMetricLeft: Int
        get() = preferences.getInt(SECONDARY_METRIC_LEFT.first, SECONDARY_METRIC_LEFT.second)
        set(value) = preferences.edit {
            it.putInt(SECONDARY_METRIC_LEFT.first, value)
        }

    var secondaryMetricCenter: Int
        get() = preferences.getInt(SECONDARY_METRIC_CENTER.first, SECONDARY_METRIC_CENTER.second)
        set(value) = preferences.edit {
            it.putInt(SECONDARY_METRIC_CENTER.first, value)
        }

    var secondaryMetricRight: Int
        get() = preferences.getInt(SECONDARY_METRIC_RIGHT.first, SECONDARY_METRIC_RIGHT.second)
        set(value) = preferences.edit {
            it.putInt(SECONDARY_METRIC_RIGHT.first, value)
        }
}