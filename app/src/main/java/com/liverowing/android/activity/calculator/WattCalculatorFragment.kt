package com.liverowing.android.activity.calculator

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.liverowing.android.R
import kotlinx.android.synthetic.main.fragment_watt_calculator.*

class WattCalculatorFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_watt_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        f_watt_calculator_calculate_split.setOnClickListener {
            resetInputErrors()
            val watts = getAverageWatts()

            if (watts > 0) {
                val split = Math.cbrt(2.8 / watts) * 500

                f_watt_calculator_split_minutes.editText!!.setText(Math.floor((split / 60)).toInt().toString())
                f_watt_calculator_split_seconds.editText!!.setText(Math.floor(split.rem(60)).toInt().toString())
                f_watt_calculator_split_tenths.editText!!.setText((split.rem(60) - Math.floor(split.rem(60))).times(10).toInt().toString())
            }
        }

        f_watt_calculator_calculate_average_watt.setOnClickListener {
            resetInputErrors()
            val split = getSplitTime()

            if (split > 0) {
                f_watt_calculator_average_watt.editText!!.setText(String.format("%.1f", (2.8 / Math.pow(split / 500, 3.0))))
            }
        }
    }

    private fun getSplitTime(): Double {
        val splitMinutes = intFromEditableOrError(f_watt_calculator_split_minutes, true)
        val splitSeconds = intFromEditableOrError(f_watt_calculator_split_seconds, true)
        val splitTenths = intFromEditableOrError(f_watt_calculator_split_tenths, true)

        return (splitMinutes * 60.0) + splitSeconds + (splitTenths / 10.0)
    }

    private fun getAverageWatts() : Double {
        return doubleFromEditableOrError(f_watt_calculator_average_watt)
    }

    private fun resetInputErrors() {
        f_watt_calculator_average_watt.error = null
        f_watt_calculator_split_minutes.error = null
        f_watt_calculator_split_seconds.error = null
        f_watt_calculator_split_tenths.error = null
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

    private fun doubleFromEditableOrError(input: TextInputLayout) : Double {
        try {
            return input.editText!!.text.toString().toDouble()
        } catch (e: Exception) {}

        return 0.0
    }
}
