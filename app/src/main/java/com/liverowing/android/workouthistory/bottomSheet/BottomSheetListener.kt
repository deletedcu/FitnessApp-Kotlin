package com.liverowing.android.workouthistory.bottomSheet

import com.liverowing.android.model.parse.Workout

interface BottomSheetListener {
    fun onViewClick(workout: Workout)
    fun onShareToFriend(workout: Workout)
    fun onShareToSocial(workout: Workout)
    fun onShareToConcept2(workout: Workout)
    fun onSendToStrava(workout: Workout)
    fun onDeleteWorkout(workout: Workout)
}