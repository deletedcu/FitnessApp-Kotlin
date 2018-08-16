package com.liverowing.android.workoutshared.history

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView
import com.liverowing.android.model.parse.Workout

interface WorkoutHistoryView : MvpLceView<List<Workout>> {
}