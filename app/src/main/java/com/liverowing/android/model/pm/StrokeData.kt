package com.liverowing.android.model.pm

import com.liverowing.android.extensions.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class StrokeData(val elapsedTime: Double,
                      val distance: Double,
                      val driveLength: Float,
                      val driveTime: Float,
                      val recoveryTime: Float,
                      val strokeDistance: Float,
                      val peakForce: Float,
                      val avgForce: Float,
                      val workPerStroke: Float,
                      val strokeCount: Int

) {
    companion object {
        fun fromByteArray(data: ByteArray): StrokeData {
            val elapsedTime = data.calcTime(0)
            val distance = data.calcDistance(3)
            val driveLength = data.getIntValue(FORMAT_UINT8,6).toFloat() / 100
            val driveTime = data.getIntValue(FORMAT_UINT8,7).toFloat() / 100
            val strokeRecoveryTime = data.getIntValue(FORMAT_UINT16,8).toFloat() / 100
            val strokeDistance = data.getIntValue(FORMAT_UINT16,10).toFloat() / 100
            val peakDriveForce = data.getIntValue(FORMAT_UINT16,12).toFloat() / 10
            val avgDriveForce = data.getIntValue(FORMAT_UINT16,14).toFloat() / 10
            val workPerStroke = data.getIntValue(FORMAT_UINT16,16).toFloat() / 10
            val strokeCount = data.getIntValue(FORMAT_UINT16,18)

            return StrokeData(
                    elapsedTime, distance, driveLength, driveTime, strokeRecoveryTime, strokeDistance, peakDriveForce, avgDriveForce, workPerStroke, strokeCount
            )
        }
    }
}