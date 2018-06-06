package com.liverowing.android.service.messages

import com.liverowing.android.model.parse.Workout
import com.liverowing.android.model.parse.WorkoutType

data class WorkoutSetup(
        val workoutType: WorkoutType,
        val opponent: Workout? = null,
        val personalBest: Workout? = null,
        val targetPace: Int? = null
)