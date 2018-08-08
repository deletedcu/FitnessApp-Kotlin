package com.liverowing.android.model.pm

import android.bluetooth.BluetoothGattCharacteristic
import com.liverowing.android.extensions.calcSpeed
import com.liverowing.android.extensions.calcTime

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
data class AdditionalRowingStatus1(val elapsedTime: Double,
                                   val speed: Double,
                                   val strokeRate: Int,
                                   val heartRate: Int,
                                   val currentPace: Float,
                                   val avgPace: Float,
                                   val restDistance: Int,
                                   val restTime: Double
) {
    companion object {
        fun fromCharacteristic(data: BluetoothGattCharacteristic): AdditionalRowingStatus1 {
            val elapsedTime = data.calcTime(0)
            val speed = data.calcSpeed(3)
            val strokeRate = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 5)
            val heartRate = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6)
            val currentPace = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 7).toFloat() / 100
            val averagePace = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 9).toFloat() / 100
            val restDistance = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 11)
            val restTime = data.calcTime(13)

            return AdditionalRowingStatus1(
                    elapsedTime, speed, strokeRate, heartRate, currentPace, averagePace, restDistance, restTime
            )
        }
    }
}