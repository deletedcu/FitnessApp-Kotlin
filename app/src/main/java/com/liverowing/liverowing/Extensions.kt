package com.liverowing.liverowing

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.text.DecimalFormat
import java.util.regex.Pattern

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}



fun Context.inflate(res: Int, parent: ViewGroup? = null): View {
    return LayoutInflater.from(this).inflate(res, parent, false)
}

fun Int.darker(): Int {
    val ratio = 1.0f - 0.2f
    val a = this shr 24 and 0xFF
    val r = ((this shr 16 and 0xFF) * ratio).toInt()
    val g = ((this shr 8 and 0xFF) * ratio).toInt()
    val b = ((this and 0xFF) * ratio).toInt()

    return a shl 24 or (r shl 16) or (g shl 8) or b
}

fun Int.dpToPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = this * (metrics.densityDpi / 160f)
    return Math.round(px)
}

fun Float.dpToPx(): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = this * (metrics.densityDpi / 160f)
    return Math.round(px)
}

fun Int.pxToDp(): Int {
    val metrics = Resources.getSystem().displayMetrics
    val dp = this / (metrics.densityDpi / 160f)
    return Math.round(dp)
}


inline fun Dialog.ifIsShowing(body: Dialog.() -> Unit) {
    if (isShowing) {
        body()
    }
}

inline fun Snackbar.ifIsShowing(body: Snackbar.() -> Unit) {
    if (isShown) {
        body()
    }
}

operator fun ViewGroup.get(position: Int): View? = getChildAt(position)

fun Activity.screenWidth(): Int {
    val metrics: DisplayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

fun Activity.screenHeight(): Int {
    val metrics: DisplayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.heightPixels
}

fun Activity.color(resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"

// used for validate if the current String is an email
fun String.isValidEmail(): Boolean {
    val pattern = Pattern.compile(EMAIL_PATTERN)
    return pattern.matcher(this).matches()
}

fun Double.format(fracDigits: Int): String {
    val df = DecimalFormat()
    df.maximumFractionDigits = fracDigits
    return df.format(this)
}

fun Float.format(fracDigits: Int): String {
    val df = DecimalFormat()
    df.maximumFractionDigits = fracDigits
    return df.format(this)
}

fun Int.secondsToTimespan(milliSecondPrecision: Boolean = false) : String {
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

    if (minutes < 10) sb.append("0")
    sb.append(minutes.toString())
    sb.append(":" + if (this % 60 < 10) "0" else "")

    sb.append(seconds)

    return sb.toString()
}

