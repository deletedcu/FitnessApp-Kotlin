package com.liverowing.android.activity.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.liverowing.android.R
import com.liverowing.android.loggedout.signup.fragments.BaseStepFragment
import com.liverowing.android.loggedout.signup.fragments.ResultListener
import com.liverowing.android.loggedout.signup.views.CustomFeetsDialogFragment
import com.liverowing.android.loggedout.signup.views.NumberPickerListener
import kotlinx.android.synthetic.main.fragment_signup_3.*
import android.text.InputFilter
import com.liverowing.android.util.Utils

class SignupStep3Fragment(override var listener: ResultListener) : BaseStepFragment(), NumberPickerListener {

    override fun onNumberPickerSelected(value: String) {
        height = value
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        a_signup_radio_system.setOnCheckedChangeListener { radioGroup, i ->
            updateUI()
        }
        updateUI()
    }

    fun updateUI() {
        Utils.hideKeyboard(activity!!)
        if (isMetric) {
            a_signup_height_text.hint = "165 cm"
            a_signup_weight_text.hint = "80 kg"
            a_signup_height.helperText = "HEIGHT(cm)"
            a_signup_weight.helperText = "WEIGHT(kg)"
            a_signup_height_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
            a_signup_height_text.isFocusableInTouchMode = true
            a_signup_height_text.setText(Utils.convertFeetToCM(height), TextView.BufferType.EDITABLE)
            a_signup_weight_text.setText(Utils.convertPoundToKg(weight), TextView.BufferType.EDITABLE)
        } else {
            a_signup_height_text.hint = "5'6″ ft"
            a_signup_weight_text.hint = "156 lbs"
            a_signup_height.helperText = "HEIGHT(ft)"
            a_signup_weight.helperText = "WEIGHT(lbs)"
            a_signup_height_text.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
            a_signup_height_text.isFocusableInTouchMode = false
            a_signup_height_text.setText(Utils.convertCMToFeet(height), TextView.BufferType.EDITABLE)
            a_signup_weight_text.setText(Utils.convertKgToPound(weight), TextView.BufferType.EDITABLE)
        }

        a_signup_height_text.setOnClickListener {
            if (!isMetric) {
                val dialogFragment = CustomFeetsDialogFragment.newInstance(this)
                dialogFragment.show(fragmentManager, "dialog")
            }
        }
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
            return height
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