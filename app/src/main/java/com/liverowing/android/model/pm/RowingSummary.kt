package com.liverowing.android.model.pm

import com.liverowing.android.extensions.*
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class RowingSummary(val logDateTime: Date,
                         val elapsedTime: Double,
                         val distance: Double,
                         val averageSpm: Int,
                         val endHeartRate: Int,
                         val averageHeartRate: Int,
                         val minimumHeartRate: Int,
                         val maximumHeartRate: Int,
                         val averageDragFactor: Int,
                         val recoveryHeartRate: Int,
                         val workoutType: WorkoutType,
                         val averagePace: Float
) {
    companion object {
        fun fromByteArray(data: ByteArray) : RowingSummary {
            val logEntryDateTime = data.calcLogEntryDateTime(0)
            val time = data.calcTime(4)
            val distance = data.calcDistance(7)
            val spm = data.getIntValue(FORMAT_UINT8, 10)
            val endHR = data.getIntValue(FORMAT_UINT8, 11)
            val avgHR = data.getIntValue(FORMAT_UINT8, 12)
            val minHR = data.getIntValue(FORMAT_UINT8, 13)
            val maxHR = data.getIntValue(FORMAT_UINT8, 14)
            val dragFactor = data.getIntValue(FORMAT_UINT8, 15)
            val recHR = data.getIntValue(FORMAT_UINT8, 16)
            val workoutType = WorkoutType.fromInt(data.getIntValue(FORMAT_UINT8, 17))
            val avgPace = data.getIntValue(FORMAT_UINT16, 18).toFloat() / 10

            return RowingSummary(
                    logEntryDateTime, time, distance, spm, endHR, avgHR, minHR, maxHR, dragFactor, recHR, workoutType, avgPace
            )
        }
    }
}