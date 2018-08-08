package com.liverowing.android.workouthistory.detail

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.Workout
import com.parse.ParseQuery
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

// TODO: Should refactor out EventBus from here eventually..
class WorkoutHistoryDetailPresenter : MvpBasePresenter<WorkoutHistoryDetailView>() {
    fun getWorkout(workoutId: String? = null) {
        Timber.d("** getWorkout ($workoutId)")
        if (workoutId !== null) {
            ifViewAttached { it.showLoading(false) }

            val query = ParseQuery.getQuery(Workout::class.java)
            query.include("workoutType.createdBy")
            query.getInBackground(workoutId) { workout, e ->
                if (e !== null) {
                    ifViewAttached { it.showError(e, false) }
                } else {
                    ifViewAttached {
                        EventBus.getDefault().postSticky(workout)
                        it.setData(workout)
                        it.showContent()
                    }
                }
            }

        } else {
            val workout = EventBus.getDefault().getStickyEvent(Workout::class.java)

            ifViewAttached {
                it.setData(workout)
                it.showContent()
            }
        }
    }

}