package com.liverowing.android.model.pm

import com.liverowing.android.extensions.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class ExtraRowingStatus2(val elapsedTime: Double,
                              val intervalCount: Int,
                              val avgPower: Int,
                              val caloriesBurned: Int,
                              val splitIntAvgPace: Float,
                              val splitIntAvgPower: Int,
                              val splitIntAvgCals: Int,
                              val lastSplitTime: Double,
                              val lastSplitDistance: Double
) {
    companion object {
        fun fromByteArray(data: ByteArray): ExtraRowingStatus2 {
            val elapsedTime = data.calcTime(0)
            val intervalCount = data.getIntValue(FORMAT_UINT8, 3)
            val averagePower = data.getIntValue(FORMAT_UINT16, 4)
            val totalCalories = data.getIntValue(FORMAT_UINT16, 6)
            val splitIntervalAveragePace = data.getIntValue(FORMAT_UINT16, 8).toFloat() / 100
            val splitIntervalAveragePower = data.getIntValue(FORMAT_UINT16, 10)
            val splitIntervalAverageCalories = data.getIntValue(FORMAT_UINT16, 12)
            val lastSplitTime = data.calcTime(14)
            val lastSplitDistance = data.calcDistance(17)

            return ExtraRowingStatus2(
                    elapsedTime, intervalCount, averagePower, totalCalories, splitIntervalAveragePace, splitIntervalAveragePower, splitIntervalAverageCalories, lastSplitTime, lastSplitDistance
            )
        }
    }
}