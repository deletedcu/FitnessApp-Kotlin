package com.liverowing.android.model.pm

import com.liverowing.android.extensions.FORMAT_UINT16
import com.liverowing.android.extensions.FORMAT_UINT8
import com.liverowing.android.extensions.calcLogEntryDateTime
import com.liverowing.android.extensions.getIntValue
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class ExtraRowingSummary(val logDateTime: Date,
                              val type: IntervalType,
                              val size: Int,
                              val count: Int,
                              val calories: Int,
                              val watts: Int,
                              val restDistance: Int,
                              val restTime: Int,
                              val averageCalories: Int
) {
    companion object {
        fun fromByteArray(data: ByteArray) : ExtraRowingSummary {
            val logEntryDateTime = data.calcLogEntryDateTime(0)
            val splitIntType = IntervalType.fromInt(data.getIntValue(FORMAT_UINT8, 4))
            val splitIntSize = data.getIntValue(FORMAT_UINT16, 5)
            val splitIntCount = data.getIntValue(FORMAT_UINT8, 7)
            val calories = data.getIntValue(FORMAT_UINT16, 8)
            val watts = data.getIntValue(FORMAT_UINT16, 10)
            val restDistance = data.getIntValue(FORMAT_UINT16, 12)
            val restTime = data.getIntValue(FORMAT_UINT16, 15)
            val avgCalories = data.getIntValue(FORMAT_UINT16, 17)

            return ExtraRowingSummary(
                    logEntryDateTime, splitIntType, splitIntSize, splitIntCount, calories, watts, restDistance, restTime, avgCalories
            )
        }
    }
}