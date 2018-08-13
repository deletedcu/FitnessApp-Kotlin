package com.liverowing.android.workoutshared.leaderboards

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView
import com.liverowing.android.model.parse.UserStats

interface WorkoutLeaderBoardsView : MvpLceView<List<UserStats>> {
}