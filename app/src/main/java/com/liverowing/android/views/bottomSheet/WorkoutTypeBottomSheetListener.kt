package com.liverowing.android.views.bottomSheet

import com.liverowing.android.model.parse.WorkoutType

interface WorkoutTypeBottomSheetListener {
    fun onBookMarkClick(workoutType: WorkoutType)
    fun onShareClick(workoutType: WorkoutType)
    fun onSendClick(workoutType: WorkoutType)
}