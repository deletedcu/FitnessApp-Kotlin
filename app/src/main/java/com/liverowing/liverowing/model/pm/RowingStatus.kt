package com.liverowing.liverowing.model.pm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class RowingStatus(val elapsedTime: Float,
                        val distance: Float,
                        val workoutType: WorkoutType,
                        val intervalType: IntervalType,
                        val workoutState: WorkoutState,
                        val rowingState: RowingState,
                        val strokeState: StrokeState,
                        val workoutDuration: Float,
                        val workoutDurationType: DurationType,
                        val totalWorkDistance: Float,
                        val dragFactor: Int
) : Parcelable