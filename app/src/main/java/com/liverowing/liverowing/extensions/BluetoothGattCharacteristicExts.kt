package com.liverowing.liverowing.extensions

import android.bluetooth.BluetoothGattCharacteristic
import java.util.*

/**
 * Created by henrikmalmberg on 2017-11-05.
 */
fun BluetoothGattCharacteristic.calcLogEntryDateTime(offset: Int): Date {
    return Date()
}

fun BluetoothGattCharacteristic.calcTime(offset: Int): Double {
    val uint8 = BluetoothGattCharacteristic.FORMAT_UINT8
    val time = (getIntValue(uint8, offset) or (getIntValue(uint8, offset + 1) shl 8) or (getIntValue(uint8, offset + 2) shl 16)).toDouble()

    // Time is in 0.01 sec resolution
    return time / 100
}

fun BluetoothGattCharacteristic.calcSplitTime(offset: Int): Double {
    val uint8 = BluetoothGattCharacteristic.FORMAT_UINT8
    val time = (getIntValue(uint8, offset) or (getIntValue(uint8, offset + 1) shl 8) or (getIntValue(uint8, offset + 2) shl 16)).toDouble()

    // Split time is in 0.1 sec resolution
    return time / 10
}

fun BluetoothGattCharacteristic.calcDistance(offset: Int): Double {
    val uint8 = BluetoothGattCharacteristic.FORMAT_UINT8
    val distance = (getIntValue(uint8, offset) or (getIntValue(uint8, offset + 1) shl 8) or (getIntValue(uint8, offset + 2) shl 16)).toDouble()

    // Distance is in 0.1 m resolution
    return distance / 10
}

fun BluetoothGattCharacteristic.calcSplitDistance(offset: Int): Double {
    val uint8 = BluetoothGattCharacteristic.FORMAT_UINT8
    val distance = (getIntValue(uint8, offset) or (getIntValue(uint8, offset + 1) shl 8)).toDouble()

    // Split distance is in 1 m resolution
    return distance
}

fun BluetoothGattCharacteristic.calcRestTime(offset: Int): Double {
    val uint8 = BluetoothGattCharacteristic.FORMAT_UINT8
    val time = (getIntValue(uint8, offset) or (getIntValue(uint8, offset + 1) shl 8)).toDouble()

    // Rest time is in 1 sec resolution
    return time
}

fun BluetoothGattCharacteristic.calcWorkoutDurationDistance(offset: Int): Double {
    val uint8 = BluetoothGattCharacteristic.FORMAT_UINT8
    val distance = (getIntValue(uint8, offset) or (getIntValue(uint8, offset + 1) shl 8)).toDouble()

    // Workout duration distance is in 1 m resolution
    return distance
}

fun BluetoothGattCharacteristic.calcSpeed(offset: Int): Double {
    val uint8 = BluetoothGattCharacteristic.FORMAT_UINT8
    val speed = (getIntValue(uint8, offset) or (getIntValue(uint8, offset + 1) shl 8)).toDouble()

    // Speed is in 0.001 m/s resolution
    return speed / 1000
}

fun BluetoothGattCharacteristic.calcCalories(offset: Int): Double {
    val uint8 = BluetoothGattCharacteristic.FORMAT_UINT8
    val calories = (getIntValue(uint8, offset) or (getIntValue(uint8, offset + 1) shl 8)).toDouble()

    return calories
}