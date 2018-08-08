package com.liverowing.android.dashboard

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.liverowing.android.model.parse.WorkoutType

interface DashboardView : MvpView {
    fun featuredWorkoutsLoading()
    fun featuredWorkoutsLoaded(workouts: List<WorkoutType>)
    fun featuredWorkoutsError(e: Exception)

    fun recentAndLikedWorkoutsLoading()
    fun recentAndLikedWorkoutsLoaded(workouts: List<WorkoutType>)
    fun recentAndLikedWorkoutsError(e: Exception)

    fun myCustomWorkoutsLoading()
    fun myCustomWorkoutsLoaded(workouts: List<WorkoutType>)
    fun myCustomWorkoutsError(e: Exception)
}
