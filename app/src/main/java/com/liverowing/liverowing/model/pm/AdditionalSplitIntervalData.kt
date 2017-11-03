package com.liverowing.liverowing.model.pm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class AdditionalSplitIntervalData(val elapsedTime: Float,
                                       val spm: Int,
                                       val workHeartRate: Int,
                                       val restHeartRate: Int,
                                       val pace: Float,
                                       val calories: Int,
                                       val averageCalories: Int,
                                       val speed: Float,
                                       val power: Int,
                                       val averageDragFactor: Int,
                                       val intervalNumber: Int
) : Parcelable