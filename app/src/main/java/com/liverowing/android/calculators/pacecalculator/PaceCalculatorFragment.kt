package com.liverowing.android.calculators.pacecalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.liverowing.android.LiveRowing
import com.liverowing.android.R
import com.liverowing.android.extensions.doubleOrError
import com.liverowing.android.extensions.intOrError
import kotlinx.android.synthetic.main.fragment_pace_calculator.*

class PaceCalculatorFragment : MvpFragment<PaceCalculatorView, PaceCalculatorPresenter>(), PaceCalculatorView{
    override fun createPresenter(): PaceCalculatorPresenter {
        return PaceCalculatorPresenter()
    }

    override fun onDestroy() {
        super.onDestroy()
        LiveRowing.refWatcher(this.activity).watch(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pace_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_pace_calculator_calculate_distance.setOnClickListener {
            presenter.calculateDistance(
                    f_pace_calculator_time_minutes.intOrError(0, "Enter a value"),
                    f_pace_calculator_time_seconds.intOrError(0, "Enter a value"),
                    f_pace_calculator_time_tenths.intOrError(0, "Enter a value"),
                    f_pace_calculator_pace_minutes.intOrError(0, "Enter a value"),
                    f_pace_calculator_pace_seconds.intOrError(0, "Enter a value"),
                    f_pace_calculator_pace_tenths.intOrError(0, "Enter a value")
            )
        }

        f_pace_calculator_calculate_pace.setOnClickListener {
            presenter.calculatePace(
                    f_pace_calculator_distance.doubleOrError(0.0, "Enter a value"),
                    f_pace_calculator_time_minutes.intOrError(0, "Enter a value"),
                    f_pace_calculator_time_seconds.intOrError(0, "Enter a value"),
                    f_pace_calculator_time_tenths.intOrError(0, "Enter a value")
            )
        }

        f_pace_calculator_calculate_time.setOnClickListener {
            presenter.calculateTime(
                    f_pace_calculator_distance.doubleOrError(0.0, "Enter a value"),
                    f_pace_calculator_pace_minutes.intOrError(0, "Enter a value"),
                    f_pace_calculator_pace_seconds.intOrError(0, "Enter a value"),
                    f_pace_calculator_pace_tenths.intOrError(0, "Enter a value")
            )
        }
    }

    private fun resetInputErrors() {
        f_pace_calculator_distance.error = null
        f_pace_calculator_pace_minutes.error = null
        f_pace_calculator_pace_seconds.error = null
        f_pace_calculator_pace_tenths.error = null
        f_pace_calculator_time_minutes.error = null
        f_pace_calculator_time_seconds.error = null
        f_pace_calculator_time_tenths.error = null
    }

    override fun distanceCalculated(distance: Double) {
        f_pace_calculator_distance_text.setText(String.format("%.1f", distance))
    }

    override fun paceCalculated(minutes: Int, seconds: Int, tenths: Int) {
        f_pace_calculator_pace_minutes_text.setText(minutes.toString())
        f_pace_calculator_pace_seconds_text.setText(seconds.toString())
        f_pace_calculator_pace_tenths_text.setText(tenths.toString())
    }

    override fun timeCalculated(minutes: Int, seconds: Int, tenths: Int) {
        f_pace_calculator_time_minutes_text.setText(minutes.toString())
        f_pace_calculator_time_seconds_text.setText(seconds.toString())
        f_pace_calculator_time_tenths_text.setText(tenths.toString())
    }

    override fun wattCalculated(watts: Double) {
        f_pace_calculator_average_watts.text = String.format("%.1f", watts)
    }
}