package com.liverowing.android.workoutbrowser.detail

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.WorkoutType
import com.parse.ParseException
import com.parse.ParseQuery

class WorkoutBrowserDetailPresenter : MvpBasePresenter<WorkoutBrowserDetailView>() {
    fun loadWorkoutTypeById(id: String) {
        ifViewAttached { it.showLoading(false) }
        val query = ParseQuery.getQuery(WorkoutType::class.java)
        query.include("createdBy")
        query.include("segments")
        query.getInBackground(id) { workoutType: WorkoutType?, e: ParseException? ->
            if (e !== null) {
                if (e.code != ParseException.CACHE_MISS) {
                    ifViewAttached { it.showError(e, false) }
                }
            } else {
                ifViewAttached {
                    it.setData(workoutType)
                    it.showContent()
                }
            }
        }
    }
}