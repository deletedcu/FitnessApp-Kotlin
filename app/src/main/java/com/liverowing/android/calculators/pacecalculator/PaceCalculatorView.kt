package com.liverowing.android.calculators.pacecalculator

import com.hannesdorfmann.mosby3.mvp.MvpView

interface PaceCalculatorView : MvpView {
    fun distanceCalculated(distance: Double)
    fun paceCalculated(minutes: Int, seconds: Int, tenths: Int)
    fun timeCalculated(minutes: Int, seconds: Int, tenths: Int)
    fun wattCalculated(watts: Double)
}