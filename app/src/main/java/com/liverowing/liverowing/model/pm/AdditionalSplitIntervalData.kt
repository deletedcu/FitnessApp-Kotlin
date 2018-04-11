package com.liverowing.liverowing.model.pm

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcelable
import com.liverowing.liverowing.extensions.calcSpeed
import com.liverowing.liverowing.extensions.calcTime
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class AdditionalSplitIntervalData(val elapsedTime: Double,
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
) : Parcelable {
    companion object {
        fun fromCharacteristic(data: BluetoothGattCharacteristic): AdditionalSplitIntervalData {
            val elapsedTime = data.calcTime(0)
            val spm = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3)
            val workHR = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4)
            val restHR = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 5)
            val pace = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 6).toFloat() / 10
            val calories = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 8)
            val avgCalories = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 10)
            val speed = data.calcSpeed(12)
            val power = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 14)
            val avgDragF = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 16)
            val count = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 17)

            return AdditionalSplitIntervalData(
                    elapsedTime, spm, workHR, restHR, pace, calories, avgCalories, speed, power, avgDragF, count
            )
        }
    }
}