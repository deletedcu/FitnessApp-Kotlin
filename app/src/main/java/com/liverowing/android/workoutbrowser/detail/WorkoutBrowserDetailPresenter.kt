package com.liverowing.android.workoutbrowser.detail

import com.liverowing.android.base.EventBusPresenter
import com.liverowing.android.model.parse.WorkoutType
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WorkoutBrowserDetailPresenter : EventBusPresenter<WorkoutBrowserDetailView>() {
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onWorkoutTypeMainThread(workoutType: WorkoutType) {
        ifViewAttached {
            it.setTitle(workoutType.name)
            it.setWorkoutImage(workoutType.image?.url)
            it.setCreatedBy(workoutType.createdBy)
        }
    }
}