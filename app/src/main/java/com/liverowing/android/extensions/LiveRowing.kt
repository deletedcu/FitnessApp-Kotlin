package com.liverowing.android.extensions

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.LiveRowing
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

fun Double.roundToDecimals(numDecimalPlaces: Int): Double {
    val factor = Math.pow(10.0, numDecimalPlaces.toDouble())
    return Math.round(this * factor) / factor
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
