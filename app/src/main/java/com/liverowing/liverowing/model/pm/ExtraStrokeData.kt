package com.liverowing.liverowing.model.pm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class ExtraStrokeData(val elapsedTime: Float,
                           val power: Int,
                           val calories: Int,
                           val strokeCount: Int,
                           val projWorkTime: Int,
                           val projWorkDist: Int
) : Parcelable