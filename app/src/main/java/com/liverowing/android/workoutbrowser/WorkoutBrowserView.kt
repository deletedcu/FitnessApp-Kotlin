package com.liverowing.android.workoutbrowser

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView
import com.liverowing.android.model.parse.WorkoutType

interface WorkoutBrowserView : MvpLceView<List<WorkoutType>>