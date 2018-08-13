package com.liverowing.android.workouthistory.detail

import com.liverowing.android.base.EventBusPresenter
import com.liverowing.android.model.parse.Workout
import com.parse.ParseQuery
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WorkoutHistoryDetailPresenter : EventBusPresenter<WorkoutHistoryDetailView>() {
    fun getWorkout(workoutId: String) {
        if (workoutId.isEmpty()) {
            eventBus.register(this)
        } else {

            ifViewAttached { it.showLoading(false) }

            val query = ParseQuery.getQuery(Workout::class.java)
            query.include("workoutType.createdBy")
            query.getInBackground(workoutId) { workout, e ->
                if (e !== null) {
                    ifViewAttached { it.showError(e, false) }
                } else {
                    EventBus.getDefault().postSticky(workout)
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onWorkoutMainThread(workout: Workout) {
        ifViewAttached {
            it.setData(workout)
            it.showContent()
        }
    }
}