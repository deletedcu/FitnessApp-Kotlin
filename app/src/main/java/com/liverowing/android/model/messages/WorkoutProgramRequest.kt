package com.liverowing.android.model.messages

import com.liverowing.android.model.parse.WorkoutType

data class WorkoutProgramRequest(val workoutType: WorkoutType, val targetPace: Int? = null)
