package com.liverowing.android.model.pm

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcelable
import com.liverowing.android.extensions.*
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class SplitIntervalData(val elapsedTime: Double,
                             val distance: Double,
                             val splitTime: Double,
                             val splitDistance: Double,
                             val restTime: Double,
                             val restDistance: Int,
                             val intervalType: IntervalType,
                             val intervalNumber: Int

) : Parcelable {
    companion object {
        fun fromCharacteristic(data: BluetoothGattCharacteristic): SplitIntervalData {
            val elapsedTime = data.calcTime(0)
            val distance = data.calcDistance(3)
            val splitTime = data.calcSplitTime(6)
            val splitDistance = data.calcSplitDistance(9)
            val restTime = data.calcRestTime(12)
            val restDistance = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 14)
            val type = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 16)
            val count = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 17)

            return SplitIntervalData(
                    elapsedTime, distance, splitTime, splitDistance, restTime, restDistance, IntervalType.fromInt(type), count
            )
        }
    }
}