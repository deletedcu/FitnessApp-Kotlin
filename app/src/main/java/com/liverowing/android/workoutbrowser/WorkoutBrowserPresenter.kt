package com.liverowing.android.workoutbrowser

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.WorkoutType
import com.parse.ParseQuery

class WorkoutBrowserPresenter : MvpBasePresenter<WorkoutBrowserView>() {

    private var query: ParseQuery<WorkoutType>? = null

    fun loadWorkoutTypes(pullToRefresh: Boolean) {
        if (query !== null && query!!.isRunning) {
            query?.cancel()
        }

        ifViewAttached { it.showLoading(pullToRefresh) }

        query = WorkoutType.featuredWorkouts()
        query?.findInBackground { objects, e ->
            run {
                if (e !== null) {
                    ifViewAttached { it.showError(e, pullToRefresh) }
                } else {
                    ifViewAttached {
                        it.setData(objects)
                        it.showContent()
                    }
                }
            }
        }
    }
}