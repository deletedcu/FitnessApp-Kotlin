package com.liverowing.android.calculators.wattcalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.extensions.doubleOrError
import com.liverowing.android.extensions.intOrError
import kotlinx.android.synthetic.main.fragment_watt_calculator.*

class WattCalculatorFragment : MvpFragment<WattCalculatorView, WattCalculatorPresenter>(), WattCalculatorView {
    override fun createPresenter(): WattCalculatorPresenter {
        return WattCalculatorPresenter()
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_watt_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        f_watt_calculator_calculate_watts.setOnClickListener {
            resetInputErrors()
            presenter.calculateWatts(
                    f_watt_calculator_pace_minutes.intOrError(0, "Enter a value"),
                    f_watt_calculator_pace_seconds.intOrError(0, "Enter a value"),
                    f_watt_calculator_pace_tenths.intOrError(0, "Enter a value")
            )
        }

        f_watt_calculator_calculate_pace.setOnClickListener {
            resetInputErrors()
            presenter.calculatePace(
                    f_watt_calculator_watts.doubleOrError(0.0, "Enter a value")
            )
        }
    }

    private fun resetInputErrors() {
        f_watt_calculator_watts.error = null
        f_watt_calculator_pace_minutes.error = null
        f_watt_calculator_pace_seconds.error = null
        f_watt_calculator_pace_tenths.error = null
    }

    override fun wattCalculated(watts: Double) {
        f_watt_calculator_watts_text.setText(String.format("%.1f", watts))
    }

    override fun paceCalculated(minutes: Int, seconds: Int, tenths: Int) {
        f_watt_calculator_pace_minutes_text.setText(minutes.toString())
        f_watt_calculator_pace_seconds_text.setText(seconds.toString())
        f_watt_calculator_pace_tenths_text.setText(tenths.toString())
    }
}
