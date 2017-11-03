package com.liverowing.liverowing.model.pm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class AdditionalStatus2(val elapsedTime: Float,
                             val intervalCount: Int,
                             val avgPower: Float,
                             val caloriesBurned: Int,
                             val splitIntAvgPace: Float,
                             val splitIntAvgPower: Int,
                             val splitIntAvgCals: Int,
                             val lastSplitTime: Float,
                             val lastSplitDistance: Float
) : Parcelable