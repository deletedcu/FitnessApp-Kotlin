package com.liverowing.android.dashboard

import com.liverowing.android.base.EventBusPresenter
import com.liverowing.android.model.messages.DeviceConnected
import com.liverowing.android.model.messages.DeviceDisconnected
import com.liverowing.android.model.parse.WorkoutType
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DashboardPresenter : EventBusPresenter<DashboardView>() {
    fun loadDashboard() {
        loadFeaturedWorkouts()
        loadRecentAndLikedWorkouts()
        loadMyCustomWorkouts()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onDeviceConnectedMainThread(data: DeviceConnected) {
        ifViewAttached { it.deviceConnected(data.device) }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onDeviceDisconnectedMainThread(data: DeviceDisconnected) {
        ifViewAttached { it.deviceDisconnected(data.device) }
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
