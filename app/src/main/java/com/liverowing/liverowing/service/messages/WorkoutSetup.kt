package com.liverowing.liverowing.service.messages

import com.liverowing.liverowing.model.parse.Workout
import com.liverowing.liverowing.model.parse.WorkoutType

data class WorkoutSetup(val workoutType: WorkoutType, val opponent: Workout?, val personalBest: Workout?, val targetPace: Int?)