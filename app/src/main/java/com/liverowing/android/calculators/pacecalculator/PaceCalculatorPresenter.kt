package com.liverowing.android.calculators.pacecalculator

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import kotlin.math.roundToInt

class PaceCalculatorPresenter : MvpBasePresenter<PaceCalculatorView>() {
    fun calculateDistance(timeMinutes: Int, timeSeconds: Int, timeTenths: Int, paceMinutes: Int, paceSeconds: Int, paceTenths: Int) {
        val time = (timeMinutes * 60.0) + timeSeconds + (timeTenths / 10.0)
        val pace = (paceMinutes * 60.0) + paceSeconds + (paceTenths / 10.0)
        val distance = ((time / pace) * 500)
        val watts = (2.8 / Math.pow(pace / 500, 3.0))

        ifViewAttached {
            it.distanceCalculated(distance)
            it.wattCalculated(watts)
        }
    }

    fun calculatePace(distance: Double, timeMinutes: Int, timeSeconds: Int, timeTenths: Int) {
        val time = (timeMinutes * 60.0) + timeSeconds + (timeTenths / 10.0)
        val pace = 500 * (time / distance)
        val minutes = Math.floor((pace / 60)).toInt()
        val seconds = Math.floor(pace.rem(60)).toInt()
        val tenths = (pace.rem(60) - Math.floor(pace.rem(60))).times(10).toInt()
        val watts = (2.8 / Math.pow(pace / 500, 3.0))

        ifViewAttached {
            it.paceCalculated(minutes, seconds, tenths)
            it.wattCalculated(watts)
        }
    }

    fun calculateTime(distance: Double, paceMinutes: Int, paceSeconds: Int, paceTenths: Int) {
        val pace = (paceMinutes * 60.0) + paceSeconds + (paceTenths / 10.0)
        val time = pace * (distance / 500)
        val minutes = Math.floor((time / 60)).toInt()
        val seconds = time.rem(60).toInt()
        val tenths = (time.rem(60) - Math.floor(time.rem(60))).times(10).toInt()
        val watts = (2.8 / Math.pow(pace / 500, 3.0))

        ifViewAttached {
            it.timeCalculated(minutes, seconds, tenths)
            it.wattCalculated(watts)
        }
    }
}