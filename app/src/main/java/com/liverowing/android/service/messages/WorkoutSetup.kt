package com.liverowing.android.service.messages

import com.liverowing.android.model.parse.Workout
import com.liverowing.android.model.parse.WorkoutType

data class WorkoutSetup(
        val workoutType: WorkoutType,
        var opponent: Workout? = null,
        var personalBest: Workout? = null,
        var targetPace: Int? = null
)