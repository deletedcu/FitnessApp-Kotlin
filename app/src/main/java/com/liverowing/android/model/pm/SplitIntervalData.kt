package com.liverowing.android.model.pm

import com.liverowing.android.extensions.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class SplitIntervalData(val elapsedTime: Double,
                             val distance: Double,
                             val splitTime: Double,
                             val splitDistance: Double,
                             val restTime: Double,
                             val restDistance: Int,
                             val intervalType: IntervalType,
                             val intervalNumber: Int

) {
    companion object {
        fun fromByteArray(data: ByteArray): SplitIntervalData {
            val elapsedTime = data.calcTime(0)
            val distance = data.calcDistance(3)
            val splitTime = data.calcSplitTime(6)
            val splitDistance = data.calcSplitDistance(9)
            val restTime = data.calcRestTime(12)
            val restDistance = data.getIntValue(FORMAT_UINT16, 14)
            val type = data.getIntValue(FORMAT_UINT8, 16)
            val count = data.getIntValue(FORMAT_UINT8, 17)

            return SplitIntervalData(
                    elapsedTime, distance, splitTime, splitDistance, restTime, restDistance, IntervalType.fromInt(type), count
            )
        }
    }
}