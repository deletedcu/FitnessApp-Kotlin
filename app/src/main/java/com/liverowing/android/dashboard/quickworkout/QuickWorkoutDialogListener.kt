package com.liverowing.android.dashboard.quickworkout

import android.app.Dialog

interface QuickWorkoutDialogListener {
    fun onCancel(dialog: Dialog)
    fun onWorkoutTypeChoosen(dialog: Dialog, workoutTypeId: String)
}