package com.liverowing.android.calculators.wattcalculator

import com.hannesdorfmann.mosby3.mvp.MvpView

interface WattCalculatorView : MvpView {
    fun wattCalculated(watts: Double)
    fun paceCalculated(minutes: Int, seconds: Int, tenths: Int)
}