package com.liverowing.liverowing.model.pm

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class AdditionalWorkoutSummary(val logDateTime: Date,
                                    val type: IntervalType,
                                    val size: Int,
                                    val count: Int,
                                    val calories: Int,
                                    val watts: Int,
                                    val restDistance: Int,
                                    val restTime: Int,
                                    val averageCalories: Int
) : Parcelable