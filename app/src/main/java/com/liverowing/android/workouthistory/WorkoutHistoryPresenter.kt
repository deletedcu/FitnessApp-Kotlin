package com.liverowing.android.workouthistory

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.Workout
import com.liverowing.android.util.Utils
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.*


class WorkoutHistoryPresenter : MvpBasePresenter<WorkoutHistoryView>() {
    private var query: ParseQuery<Workout>? = null

    fun loadWorkouts(createdAt: Date? = Utils.getBeforeDate(7), isDESC: Boolean = true, page: Int = 0) {
        ifViewAttached { it.showLoading(page == 0) }

        if (query !== null && query!!.isRunning) {
            query?.cancel()
        }

        query = Workout.forUser(ParseUser.getCurrentUser(), createdAt, isDESC, page)
        query?.findInBackground { objects, e ->
            run {
                if (e != null) {
                    if (e.code != ParseException.CACHE_MISS) {
                        ifViewAttached { it.showError(e, page == 0) }
                    }
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