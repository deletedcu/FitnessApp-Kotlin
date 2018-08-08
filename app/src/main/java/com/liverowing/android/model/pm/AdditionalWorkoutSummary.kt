package com.liverowing.android.model.pm

import android.bluetooth.BluetoothGattCharacteristic
import com.liverowing.android.extensions.calcLogEntryDateTime
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class AdditionalWorkoutSummary(val logDateTime: Date,
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
        fun fromCharacteristic(data: BluetoothGattCharacteristic) : AdditionalWorkoutSummary {
            val logEntryDateTime = data.calcLogEntryDateTime(0)
            val splitIntType = IntervalType.fromInt(data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4))
            val splitIntSize = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 5)
            val splitIntCount = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 7)
            val calories = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 8)
            val watts = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 10)
            val restDistance = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 12)
            val restTime = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 15)
            val avgCalories = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 17)

            return AdditionalWorkoutSummary(
                    logEntryDateTime, splitIntType, splitIntSize, splitIntCount, calories, watts, restDistance, restTime, avgCalories
            )
        }
    }
}