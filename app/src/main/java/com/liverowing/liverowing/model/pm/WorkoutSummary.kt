package com.liverowing.liverowing.model.pm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class WorkoutSummary(val logDateTime: Date,
                          val elapsedTime: Float,
                          val distance: Float,
                          val averageSpm: Int,
                          val endHeartRate: Int,
                          val averageHeartRate: Int,
                          val minimumHeartRate: Int,
                          val maximumHeartRate: Int,
                          val averageDragFactor: Int,
                          val recoveryHeartRate: Int,
                          val workoutType: WorkoutType,
                          val averagePace: Float
) : Parcelable