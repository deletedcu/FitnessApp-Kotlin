package com.liverowing.android.extensions

import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.LiveRowing
import java.util.*
import java.util.regex.Pattern

val Context.application: LiveRowing
    get() = applicationContext as LiveRowing

fun Int.pxToDp(): Int {
    val metrics = Resources.getSystem().displayMetrics
    val dp = this / (metrics.densityDpi / 160f)
    return Math.round(dp)
}

fun Int.dpToPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = this * (metrics.densityDpi / 160f)
    return Math.round(px)
}

fun Float.pxToDp(): Float {
    val metrics = Resources.getSystem().displayMetrics
    val dp = this / (metrics.densityDpi / 160f)
    return dp
}

fun Float.dpToPx(): Float {
    val metrics = Resources.getSystem().displayMetrics
    val px = this * (metrics.densityDpi / 160f)
    return px
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Int.secondsToTimespan(milliSecondPrecision: Boolean = false) : String {
    return this.toDouble().secondsToTimespan(milliSecondPrecision)
}

fun Number.secondsToTimespan(milliSecondPrecision: Boolean = false) : String {
    return this.toDouble().secondsToTimespan(milliSecondPrecision)
}

fun Float.secondsToTimespan(milliSecondPrecision: Boolean = false): String {
    return this.toDouble().secondsToTimespan(milliSecondPrecision)
}

fun Double.secondsToTimespan(milliSecondPrecision: Boolean = false): String {
    val hours = Math.floor(this / 3600).toInt()
    val minutes = Math.floor((this % 3600) / 60).toInt()
    val seconds = if (milliSecondPrecision) "%02.1f".format(this % 60) else Math.floor(this % 60).toInt().toString()

    val sb = StringBuffer()
    if (hours > 0) { sb.append(hours.toString() + ':') }

    if (hours > 0 && minutes < 10) sb.append("0")
    sb.append(minutes.toString())
    sb.append(":" + if (this % 60 < 10) "0" else "")

    sb.append(seconds)

    return sb.toString()
}

val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"

// used for validate if the current String is an email
fun String.isValidEmail(): Boolean {
    val pattern = Pattern.compile(EMAIL_PATTERN)
    return pattern.matcher(this).matches()
}

fun String.isValidUserName(): Boolean {
    val pattern = Pattern.compile("[a-z]")
    return pattern.matcher(this).matches()
}

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