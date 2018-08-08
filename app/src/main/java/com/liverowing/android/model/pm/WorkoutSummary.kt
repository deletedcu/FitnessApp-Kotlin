package com.liverowing.android.model.pm

import android.bluetooth.BluetoothGattCharacteristic
import com.liverowing.android.extensions.calcDistance
import com.liverowing.android.extensions.calcLogEntryDateTime
import com.liverowing.android.extensions.calcTime
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class WorkoutSummary(val logDateTime: Date,
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
        fun fromCharacteristic(data: BluetoothGattCharacteristic) : WorkoutSummary {
            val logEntryDateTime = data.calcLogEntryDateTime(0)
            val time = data.calcTime(4) //((data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4) or (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 5) shl 8) or (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6) shl 16)).toFloat()) / 100 // Time is in 0.01 sec resolution
            val distance = data.calcDistance(7) //((data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 7) or (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 8) shl 8) or (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 9) shl 16)).toFloat()) / 10 // Distance is in 0.1 m resolution
            val spm = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 10)
            val endHR = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 11)
            val avgHR = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 12)
            val minHR = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 13)
            val maxHR = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 14)
            val dragFactor = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 15)
            val recHR = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 16)
            val workoutType = WorkoutType.fromInt(data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 17))
            val avgPace = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 18).toFloat() / 10

            return WorkoutSummary(
                    logEntryDateTime, time, distance, spm, endHR, avgHR, minHR, maxHR, dragFactor, recHR, workoutType, avgPace
            )
        }
    }
}