package com.liverowing.liverowing.model.pm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class AdditionalStatus1(val elapsedTime: Float,
                             val speed: Float,
                             val strokeRate: Int,
                             val heartRate: Int,
                             val currentPace: Float,
                             val avgPace: Float,
                             val restDistance: Int,
                             val restTime: Float
) : Parcelable