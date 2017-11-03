package com.liverowing.liverowing.model.pm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class StrokeData(val elapsedTime: Float,
                      val distance: Float,
                      val driveLength: Float,
                      val driveTime: Float,
                      val recoveryTime: Float,
                      val strokeDistance: Float,
                      val peakForce: Float,
                      val avgForce: Float,
                      val workPerStroke: Float,
                      val strokeCount: Int

) : Parcelable