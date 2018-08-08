package com.liverowing.android.dashboard

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.WorkoutType

class DashboardPresenter : MvpBasePresenter<DashboardView>() {
    fun loadDashboard() {
        loadFeaturedWorkouts()
        loadRecentAndLikedWorkouts()
        loadMyCustomWorkouts()
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
