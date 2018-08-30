package com.liverowing.android.dashboard

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableViewState
import com.liverowing.android.model.parse.WorkoutType

class DashboardViewState : RestorableViewState<DashboardView> {
    companion object {
        const val KEY_FEATURED_WORKOUTS = "DashboardViewState-featuredWorkouts"
        const val KEY_POPULAR_WORKOUTS = "DashboardViewState-popularWorkouts"
        const val KEY_RECENT_WORKOUTS = "DashboardViewState-recentWorkouts"
    }

    var featuredWorkouts = mutableListOf<WorkoutType>()
    var popularWorkouts = mutableListOf<WorkoutType>()
    var recentWorkouts = mutableListOf<WorkoutType>()

    override fun saveInstanceState(out: Bundle) {
        out.putParcelableArray(KEY_FEATURED_WORKOUTS, featuredWorkouts.toTypedArray())
        out.putParcelableArray(KEY_POPULAR_WORKOUTS, popularWorkouts.toTypedArray())
        out.putParcelableArray(KEY_RECENT_WORKOUTS, recentWorkouts.toTypedArray())
    }

    override fun restoreInstanceState(state: Bundle?): RestorableViewState<DashboardView> {

        if (state !== null) {
            featuredWorkouts = state.getParcelableArray(KEY_FEATURED_WORKOUTS).toMutableList() as MutableList<WorkoutType>
            popularWorkouts = state.getParcelableArray(KEY_POPULAR_WORKOUTS).toMutableList() as MutableList<WorkoutType>
            recentWorkouts = state.getParcelableArray(KEY_RECENT_WORKOUTS).toMutableList() as MutableList<WorkoutType>
        }
        return this
    }

    override fun apply(view: DashboardView?, retained: Boolean) {
        view?.featuredWorkoutsLoaded(featuredWorkouts)
        view?.popularWorkoutsLoaded(popularWorkouts)
        view?.recentWorkoutsLoaded(recentWorkouts)
    }
}