package com.liverowing.liverowing.model.pm

import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcelable
import com.liverowing.liverowing.extensions.*
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@Parcelize
data class SplitIntervalData(val elapsedTime: Float,
                             val distance: Float,
                             val splitTime: Float,
                             val splitDistance: Float,
                             val restTime: Float,
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