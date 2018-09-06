package com.liverowing.android.workoutbrowser

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView
import com.parse.ParseObject

interface WorkoutBrowserView : MvpLceView<List<ParseObject>>