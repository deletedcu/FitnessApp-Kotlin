package com.liverowing.android.dashboard.bottomSheet

import com.liverowing.android.model.parse.WorkoutType

interface DashboardBottomSheetListener {
    fun onBookMarkClick(workoutType: WorkoutType)
    fun onShareClick(workoutType: WorkoutType)
    fun onSendClick(workoutType: WorkoutType)
}