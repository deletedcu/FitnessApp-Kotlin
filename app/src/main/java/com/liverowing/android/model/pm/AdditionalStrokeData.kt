package com.liverowing.android.model.pm

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Parcelable
import com.liverowing.android.extensions.calcTime
import kotlinx.android.parcel.Parcelize

/**
 * Created by henrikmalmberg on 2017-11-03.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class AdditionalStrokeData(val elapsedTime: Double,
                                val power: Int,
                                val calories: Int,
                                val strokeCount: Int,
                                val projWorkTime: Int,
                                val projWorkDist: Int
) : Parcelable {
    companion object {
        fun fromCharacteristic(data: BluetoothGattCharacteristic): AdditionalStrokeData {
            val elapsedTime = data.calcTime(0)
            val strokePower = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 3)
            val strokeCalories = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 5)
            val strokeCount = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 7)
            val projWorkTime = (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 9) or (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 10) shl 8) or (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 11) shl 16))
            val projWorkDist = (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 12) or (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 13) shl 8) or (data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 14) shl 16))

            return AdditionalStrokeData(
                    elapsedTime, strokePower, strokeCalories, strokeCount, projWorkTime, projWorkDist
            )
        }
    }
}