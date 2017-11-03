package com.liverowing.liverowing.model.pm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class SplitIntervalData(val elapsedTime: Float,
                             val distance: Float,
                             val splitTime: Float,
                             val splitDistance: Float,
                             val restTime: Float,
                             val restDistance: Int,
                             val intervalType: IntervalType,
                             val intervalNumber: Int

) : Parcelable