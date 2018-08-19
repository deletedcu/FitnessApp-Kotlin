package com.liverowing.android.dashboard

import com.liverowing.android.base.EventBusPresenter
import com.liverowing.android.model.messages.DeviceConnected
import com.liverowing.android.model.messages.DeviceConnecting
import com.liverowing.android.model.messages.DeviceDisconnected
import com.liverowing.android.model.parse.WorkoutType
import com.parse.ParseConfig
import kotlinx.serialization.*
import kotlinx.serialization.json.JSON
import kotlinx.serialization.json.JsonArray
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONArray

class DashboardPresenter : EventBusPresenter<DashboardView>() {
    fun loadDashboard() {
        loadFeaturedWorkouts()
        loadRecentAndLikedWorkouts()
        loadMyCustomWorkouts()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onDeviceConnectedMainThread(message: DeviceConnected) {
        ifViewAttached { it.deviceConnected(message.device) }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onDeviceConnectingMainThread(message: DeviceConnecting) {
        ifViewAttached { it.deviceConnecting(message.device) }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onDeviceDisconnectedMainThread(message: DeviceDisconnected) {
        ifViewAttached { it.deviceDisconnected(message.device) }
    }

    private fun loadFeaturedWorkouts() {
        val query = WorkoutType.featuredWorkouts()
        query.limit = 10

        ifViewAttached { it.featuredWorkoutsLoading() }
        query.findInBackground { objects, e ->
            if (e !== null) {
                ifViewAttached { it.featuredWorkoutsError(e) }
            } else {
                ifViewAttached {
                    it.featuredWorkoutsLoaded(objects)
                }
            }
        }
    }

    private fun loadRecentAndLikedWorkouts() {
        val query = WorkoutType.recentAndLikedWorkouts()
        query.limit = 10

        ifViewAttached { it.recentAndLikedWorkoutsLoading() }
        query.findInBackground { objects, e ->
            if (e !== null) {
                ifViewAttached { it.recentAndLikedWorkoutsError(e) }
            } else {
                ifViewAttached {
                    it.recentAndLikedWorkoutsLoaded(objects)
                }
            }
        }
    }

    private fun loadMyCustomWorkouts() {

    }

}
