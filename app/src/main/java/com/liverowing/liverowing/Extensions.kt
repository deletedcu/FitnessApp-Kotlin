package com.liverowing.liverowing

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import java.util.regex.Pattern

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun ImageView.loadUrl(url: String) {
    Picasso.with(this.context)
            .load(url)
            .into(this)
}

fun ImageView.loadUrl(url: String, transformation: Transformation) {
    Picasso.with(this.context)
            .load(url)
            .transform(transformation)
            .into(this)
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

fun Double.milliSecondsToTimespan(milliSecondPrecision: Int = 0): String {
    return this.toLong().milliSecondsToTimespan(milliSecondPrecision)
}

fun Int.milliSecondsToTimespan(milliSecondPrecision: Int = 0): String {
    return this.toLong().milliSecondsToTimespan(milliSecondPrecision)
}

fun Long.milliSecondsToTimespan(milliSecondPrecision: Int = 0): String {
    val sb = StringBuffer()
    val diffInSeconds = this / 1000
    val milliseconds = this % 1000
    val seconds = if (diffInSeconds >= 60) diffInSeconds % 60 else diffInSeconds
    val minutes = if ((diffInSeconds / 60) >= 60) (diffInSeconds / 60) % (60) else diffInSeconds / 60
    val hours = if ((diffInSeconds / 3600) >= 24) (diffInSeconds / 3600) % (24) else diffInSeconds / 3600

    if (hours > 0) {
        sb.append(hours)
        sb.append(":")
    }

    if (minutes > 0) {
        sb.append(minutes.toString().padStart(2, '0'))
        sb.append(":")
    }

    sb.append(seconds.toString().padStart(2, '0'))
    if (milliSecondPrecision > 0) {
        sb.append(".")
        sb.append(milliseconds.toString().subSequence(0, milliSecondPrecision))
    }
    return sb.toString()
}

