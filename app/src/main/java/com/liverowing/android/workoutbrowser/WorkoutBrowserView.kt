package com.liverowing.android.workoutbrowser

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView
import com.liverowing.android.model.parse.User
import com.parse.ParseObject

interface WorkoutBrowserView : MvpLceView<List<ParseObject>> {
    fun setFeaturedUsers(users: List<User>)
}