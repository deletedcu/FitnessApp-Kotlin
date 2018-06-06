package com.liverowing.android.activity.calculator

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.liverowing.android.R
import kotlinx.android.synthetic.main.fragment_pace_calculator.*
import kotlin.math.roundToInt

class PaceCalculatorFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pace_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_pace_calculator_calculate_distance.setOnClickListener {
            resetInputErrors()
            val split = getSplitTime()
            val time = getTime()

            if (split > 0 && time > 0) {
                val distance = ((time / split) * 500).roundToInt()
                f_pace_calculator_meters.editText!!.setText(distance.toString())
                f_pace_calculator_average_watts.text = String.format("%.1f", (2.8 / Math.pow(split / 500, 3.0)))
            }
        }

        f_pace_calculator_calculate_split.setOnClickListener {
            resetInputErrors()
            val time = getTime()
            val distance = getDistance()

            if (time > 0 && distance > 0) {
                val split = 500 * (time / distance)
                f_pace_calculator_split_minutes.editText!!.setText(Math.floor((split / 60)).toInt().toString())
                f_pace_calculator_split_seconds.editText!!.setText(Math.floor(split.rem(60)).toInt().toString())
                f_pace_calculator_split_tenths.editText!!.setText((split.rem(60) - Math.floor(split.rem(60))).times(10).toInt().toString())
                f_pace_calculator_average_watts.text = String.format("%.1f", (2.8 / Math.pow(split / 500, 3.0)))
            }
        }

        f_pace_calculator_calculate_time.setOnClickListener {
            resetInputErrors()
            val split = getSplitTime()
            val distance = getDistance()

            if (split > 0 && distance > 0) {
                val time = split * (distance / 500)
                f_pace_calculator_time_minutes.editText!!.setText(Math.floor((time / 60)).toInt().toString())
                f_pace_calculator_time_seconds.editText!!.setText(time.rem(60).toInt().toString())
                f_pace_calculator_time_tenths.editText!!.setText((time.rem(60) - Math.floor(time.rem(60))).times(10).toInt().toString())
                f_pace_calculator_average_watts.text = String.format("%.1f", (2.8 / Math.pow(split / 500, 3.0)))
            }
        }
    }

    private fun getTime(): Double {
        val timeMinutes = intFromEditableOrError(f_pace_calculator_time_minutes)
        val timeSeconds = intFromEditableOrError(f_pace_calculator_time_seconds)
        val timeTenths = intFromEditableOrError(f_pace_calculator_time_tenths)

        return (timeMinutes * 60.0) + timeSeconds + (timeTenths / 10.0)
    }

    private fun getSplitTime(): Double {
        val splitMinutes = intFromEditableOrError(f_pace_calculator_split_minutes, true)
        val splitSeconds = intFromEditableOrError(f_pace_calculator_split_seconds, true)
        val splitTenths = intFromEditableOrError(f_pace_calculator_split_tenths, true)

        return (splitMinutes * 60.0) + splitSeconds + (splitTenths / 10.0)
    }

    private fun getDistance() : Double {
        return intFromEditableOrError(f_pace_calculator_meters).toDouble()
    }

    private fun resetInputErrors() {
        f_pace_calculator_meters.error = null
        f_pace_calculator_split_minutes.error = null
        f_pace_calculator_split_seconds.error = null
        f_pace_calculator_split_tenths.error = null
        f_pace_calculator_time_minutes.error = null
        f_pace_calculator_time_seconds.error = null
        f_pace_calculator_time_tenths.error = null
    }

    private fun intFromEditableOrError(input: TextInputLayout, allowEmpty: Boolean = false) : Int {
        try {
            return input.editText!!.text.toString().toInt()
        } catch (e: Exception) {}

        if (!allowEmpty) {
            input.error = "Enter a value"
        } else {
            input.editText!!.setText("0")
        }
        return 0
    }
}
