package com.liverowing.liverowing.service.messages

import com.liverowing.liverowing.model.parse.WorkoutType

/**
 * Created by henrikmalmberg on 2017-11-26.
 */
data class WorkoutProgramRequest(val workoutType: WorkoutType, val targetPace: Int? = null)