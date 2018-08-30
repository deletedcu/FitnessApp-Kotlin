package com.liverowing.android.dashboard

import com.liverowing.android.base.EventBusPresenter
import com.liverowing.android.model.messages.DeviceConnected
import com.liverowing.android.model.messages.DeviceDisconnected
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.WorkoutType
import com.parse.ParseException
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DashboardPresenter : EventBusPresenter<DashboardView>() {
    fun loadDashboard(featuredUsers: MutableList<User>? = null) {
        loadFeaturedWorkouts(featuredUsers)
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

    fun loadFeaturedWorkouts(featuredUsers: MutableList<User>? = null) {
        val query = WorkoutType.featuredWorkouts(featuredUsers)
        query.limit = 15

        ifViewAttached { it.featuredWorkoutsLoading() }
        query.findInBackground { objects, e ->
            if (e !== null) {
                if (e.code != ParseException.CACHE_MISS) {
                    ifViewAttached { it.featuredWorkoutsError(e) }
                }
            } else {
                ifViewAttached {
                    it.featuredWorkoutsLoaded(objects)
                }
            }
        }
    }

    private fun loadRecentAndLikedWorkouts() {
        val query = WorkoutType.recentAndLikedWorkouts()
        query.limit = 15

        ifViewAttached { it.recentAndLikedWorkoutsLoading() }
        query.findInBackground { objects, e ->
            if (e !== null) {
                if (e.code != ParseException.CACHE_MISS) {
                    ifViewAttached { it.recentAndLikedWorkoutsError(e) }
                }
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
