package com.liverowing.android.model.pm

import com.liverowing.android.extensions.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class ExtraSplitIntervalData(val elapsedTime: Double,
                                  val spm: Int,
                                  val workHeartRate: Int,
                                  val restHeartRate: Int,
                                  val pace: Float,
                                  val calories: Int,
                                  val averageCalories: Int,
                                  val speed: Double,
                                  val power: Int,
                                  val averageDragFactor: Int,
                                  val intervalNumber: Int
) {
    companion object {
        fun fromByteArray(data: ByteArray): ExtraSplitIntervalData {
            val elapsedTime = data.calcTime(0)
            val spm = data.getIntValue(FORMAT_UINT8, 3)
            val workHR = data.getIntValue(FORMAT_UINT8, 4)
            val restHR = data.getIntValue(FORMAT_UINT8, 5)
            val pace = data.getIntValue(FORMAT_UINT16, 6).toFloat() / 10
            val calories = data.getIntValue(FORMAT_UINT16, 8)
            val avgCalories = data.getIntValue(FORMAT_UINT16, 10)
            val speed = data.calcSpeed(12)
            val power = data.getIntValue(FORMAT_UINT16, 14)
            val avgDragF = data.getIntValue(FORMAT_UINT8, 16)
            val count = data.getIntValue(FORMAT_UINT8, 17)

            return ExtraSplitIntervalData(
                    elapsedTime, spm, workHR, restHR, pace, calories, avgCalories, speed, power, avgDragF, count
            )
        }
    }
}