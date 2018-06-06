package com.liverowing.android.service.messages

import com.liverowing.android.model.parse.WorkoutType

/**
 * Created by henrikmalmberg on 2017-11-26.
 */
data class WorkoutProgramRequest(val workoutType: WorkoutType, val targetPace: Int? = null)