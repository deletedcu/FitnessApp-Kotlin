package com.liverowing.android.loggedout.signup.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.liverowing.android.R
import com.liverowing.android.views.FeetDialogFragment
import com.liverowing.android.views.NumberPickerListener
import kotlinx.android.synthetic.main.fragment_signup_3.*
import android.text.InputFilter
import com.liverowing.android.util.Utils

class SignupStep3Fragment : BaseStepFragment(), NumberPickerListener {

    override lateinit var listener: ResultListener

    var height: String = "0"
        get() = a_signup_height_text.text.toString()
        set(value) {
            field = value
            a_signup_height_text.setText(value, TextView.BufferType.EDITABLE)
        }

    var weight: String = "0"
        get() = a_signup_weight_text.text.toString()

    var isMetric: Boolean = false
        get() {
            return a_signup_system_metric.isChecked
        }

    companion object {
        fun newInstance(listener: ResultListener) : SignupStep3Fragment {
            var f = SignupStep3Fragment()
            f.listener = listener;
            return f
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    private fun setupUI() {
        layout_step3.setOnClickListener { Utils.hideKeyboard(activity!!) }

        a_signup_radio_system.setOnCheckedChangeListener { _, _ ->
            updateUI()
        }

        a_signup_height_text.setOnClickListener {
            if (!isMetric) {
                if (a_signup_height_text.error != null) {
                    a_signup_height_text.error = null
                }
                val dialogFragment = FeetDialogFragment.newInstance(this)
                dialogFragment.show(fragmentManager, "dialog")
            }
        }

        updateUI()
    }

    private fun updateUI() {
        Utils.hideKeyboard(activity!!)
        if (isMetric) {
            a_signup_height.hint = "HEIGHT(cm)"
            a_signup_weight.hint = "WEIGHT(kg)"
            a_signup_height_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
            a_signup_height_text.isFocusableInTouchMode = true
            a_signup_height_text.setText(Utils.convertFeetToCM(height), TextView.BufferType.EDITABLE)
            a_signup_weight_text.setText(Utils.convertPoundToKg(weight), TextView.BufferType.EDITABLE)
        } else {
            a_signup_height.hint = "HEIGHT(ft)"
            a_signup_weight.hint = "WEIGHT(lbs)"
            a_signup_height_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
            a_signup_height_text.isFocusableInTouchMode = false
            a_signup_height_text.setText(Utils.convertCMToFeet(height), TextView.BufferType.EDITABLE)
            a_signup_weight_text.setText(Utils.convertKgToPound(weight), TextView.BufferType.EDITABLE)
        }
    }

    // NumberPickerListener
    override fun onNumberPickerSelected(value: String) {
        height = value
    }

    override fun checkValidation() {
        if (checkHeightValidation() && checkWeightValidation()) {
            val heightValue = getHeightValue()
            var data = HashMap<String, String>()
            data.put("height", heightValue)
            data.put("weight", weight)
            data.put("isMetric", isMetric.toString())
            listener.onResultListener(true, data)
        }
    }

    private fun getHeightValue(): String {
        if (isMetric) {
            return Utils.cmToMeter(height)
        } else {
            return Utils.feetToInch(height)
        }
    }

    private fun checkHeightValidation() : Boolean {
        if (height.isEmpty() || height.equals("0")) {
            a_signup_height_text.requestFocus()
            a_signup_height_text.error = "Empty height!"
            return false
        } else {
            if (isMetric) {
                val value = height.toInt()
                if (value >= 90 && value <= 300) {
                    return true
                } else {
                    a_signup_height_text.requestFocus()
                    a_signup_height_text.error = "Invalid height!"
                    return false
                }
            } else {
                return true
            }
        }
    }

    private fun checkWeightValidation(): Boolean {
        if (weight.isEmpty() || weight.equals("0")) {
            a_signup_weight_text.requestFocus()
            a_signup_weight_text.error = "Empty weight!"
            return false
        } else {
            var value = weight.toInt()
            if (isMetric) {
                if (value >= 22 && value <= 360) {
                    return true
                } else {
                    a_signup_weight_text.requestFocus()
                    a_signup_weight_text.error = "Invalid weight!"
                    return false
                }
            } else {
                if (value >= 50 && value <= 800) {
                    return true
                } else {
                    a_signup_weight_text.requestFocus()
                    a_signup_weight_text.error = "Invalid weight!"
                    return false
                }
            }
        }
    }

}