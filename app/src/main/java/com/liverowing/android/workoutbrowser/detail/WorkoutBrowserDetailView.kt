package com.liverowing.android.workoutbrowser.detail

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.liverowing.android.model.parse.User

interface WorkoutBrowserDetailView : MvpView {
    fun setTitle(title: String?)
    fun setWorkoutImage(url: String?)

    fun setCreatedBy(createdBy: User?)
}