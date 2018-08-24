package com.liverowing.android.workoutbrowser.detail

import com.liverowing.android.base.EventBusPresenter
import com.liverowing.android.model.parse.WorkoutType
import com.parse.ParseException
import com.parse.ParseQuery
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WorkoutBrowserDetailPresenter : EventBusPresenter<WorkoutBrowserDetailView>() {
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
                eventBus.postSticky(workoutType)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onWorkoutTypeMainThread(workoutType: WorkoutType) {
        ifViewAttached {
            it.setData(workoutType)
            it.showContent()
        }
    }
}