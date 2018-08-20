package com.liverowing.android.workouthistory

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.Workout
import com.parse.ParseUser
import com.parse.ParseQuery


class WorkoutHistoryPresenter : MvpBasePresenter<WorkoutHistoryView>() {
    private var query: ParseQuery<Workout>? = null

    fun loadWorkouts(pullToRefresh: Boolean) {
        ifViewAttached { it.showLoading(pullToRefresh) }

        if (query !== null && query!!.isRunning) {
            query?.cancel()
        }

        query = Workout.forUser(ParseUser.getCurrentUser())
        query?.findInBackground { objects, e ->
            run {
                if (e != null) {
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