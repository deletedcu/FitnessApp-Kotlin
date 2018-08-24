package com.liverowing.android.dashboard.quickworkout

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.parse.ParseConfig
import com.parse.ParseException
import kotlinx.serialization.Optional
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlinx.serialization.serializer
import org.json.JSONArray

class QuickWorkoutDialogPresenter : MvpBasePresenter<QuickWorkoutDialogView>() {
    fun loadQuickWorkouts() {
        val config = ParseConfig.getCurrentConfig()
        val json = config.getJSONArray("DASHBOARD_FAB", JSONArray()).toString()

        if (json == "[]") {
            refreshAndLoadQuickWorkouts()
        } else {
            ifViewAttached {
                val items = JSON.parse(QuickWorkoutItem::class.serializer().list, json)
                it.setData(items)
                it.showContent()
            }
        }
    }

    private fun refreshAndLoadQuickWorkouts() {
        ParseConfig.getInBackground { config, e ->
            if (e != null) {
                ifViewAttached {
                    if (e.code != ParseException.CACHE_MISS) {
                        it.showError(e, false)
                    }
                }
            } else {
                val json = config.getJSONArray("DASHBOARD_FAB", JSONArray()).toString()
                if (json == "[]") {
                    ifViewAttached {
                        val items = JSON.parse(QuickWorkoutItem::class.serializer().list, json)
                        it.setData(items)
                        it.showContent()
                    }
                }
            }
        }
    }

    @Serializable
    data class QuickWorkoutItem(val name: String, @Optional val type: String = "item", @Optional val icon: String? = null, @Optional val items: List<QuickWorkoutItem>? = null, @Optional val workoutType: String? = null)
}