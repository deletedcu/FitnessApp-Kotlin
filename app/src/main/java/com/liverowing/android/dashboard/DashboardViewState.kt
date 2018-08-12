package com.liverowing.android.dashboard

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.viewstate.RestorableViewState
import com.liverowing.android.model.parse.WorkoutType

class DashboardViewState : RestorableViewState<DashboardView> {
    companion object {
        const val KEY_FEATURED_WORKOUTS = "DashboardViewState-featuredWorkouts"
        const val KEY_RECENT_AND_LIKED_WORKOUTS = "DashboardViewState-recentAndLikedWorkouts"
    }

    var featuredWorkouts = mutableListOf<WorkoutType>()
    var recentAndLikedWorkouts = mutableListOf<WorkoutType>()

    override fun saveInstanceState(out: Bundle) {
        out.putParcelableArray(KEY_FEATURED_WORKOUTS, featuredWorkouts.toTypedArray())
        out.putParcelableArray(KEY_RECENT_AND_LIKED_WORKOUTS, recentAndLikedWorkouts.toTypedArray())
    }

    override fun restoreInstanceState(state: Bundle?): RestorableViewState<DashboardView> {

        if (state !== null) {
            featuredWorkouts = state.getParcelableArray(KEY_FEATURED_WORKOUTS).toMutableList() as MutableList<WorkoutType>
            recentAndLikedWorkouts = state.getParcelableArray(KEY_RECENT_AND_LIKED_WORKOUTS).toMutableList() as MutableList<WorkoutType>
        }
        return this
    }

    override fun apply(view: DashboardView?, retained: Boolean) {
        view?.featuredWorkoutsLoaded(featuredWorkouts)
        view?.recentAndLikedWorkoutsLoaded(recentAndLikedWorkouts)
    }
}