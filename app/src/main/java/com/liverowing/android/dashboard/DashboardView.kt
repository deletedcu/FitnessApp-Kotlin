package com.liverowing.android.dashboard

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.liverowing.android.model.parse.User
import com.liverowing.android.model.parse.WorkoutType

interface DashboardView : MvpView {
    fun featuredWorkoutsLoading()
    fun featuredWorkoutsLoaded(workouts: MutableList<WorkoutType>)
    fun featuredWorkoutsError(e: Exception)

    fun popularWorkoutsLoading()
    fun popularWorkoutsLoaded(workouts: MutableList<WorkoutType>)
    fun popularWorkoutsError(e: Exception)

    fun recentWorkoutsLoading()
    fun recentWorkoutsLoaded(workouts: MutableList<WorkoutType>)
    fun recentWorkoutsError(e: Exception)

    fun deviceConnected(device: Any?)
    fun deviceConnecting(device: Any?)
    fun deviceDisconnected(device: Any?)
    fun deviceReady(device: Any?, name: String)

    fun featuredWorkoutsUpdated(workouts: MutableList<WorkoutType>, users: MutableList<User>)
}
