package com.liverowing.android.calculators.wattcalculator

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter

class WattCalculatorPresenter : MvpBasePresenter<WattCalculatorView>() {
    fun calculateWatts(minutes: Int, seconds: Int, tenths: Int) {
        ifViewAttached {
            val pace = (minutes * 60.0) + seconds + (tenths / 10.0)
            val watts = (2.8 / Math.pow(pace / 500, 3.0))

            it.wattCalculated(watts)
        }
    }

    fun calculatePace(watts: Double) {
        ifViewAttached {
            val split = Math.cbrt(2.8 / watts) * 500
            val minutes = Math.floor((split / 60)).toInt()
            val seconds = Math.floor(split.rem(60)).toInt()
            val tenths = (split.rem(60) - Math.floor(split.rem(60))).times(10).toInt()

            it.paceCalculated(minutes, seconds, tenths)
        }
    }
}