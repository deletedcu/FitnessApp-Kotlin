package com.liverowing.android.pm

import com.liverowing.android.model.parse.WorkoutType

interface PMDevice {
    fun connect()
    fun disconnect()
    fun programWorkout(workoutType: WorkoutType, targetPace: Int? = null)
}