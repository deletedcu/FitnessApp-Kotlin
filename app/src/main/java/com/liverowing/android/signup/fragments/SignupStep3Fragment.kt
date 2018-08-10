package com.liverowing.android.activity.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import com.liverowing.android.signup.fragments.BaseStepFragment
import com.liverowing.android.signup.fragments.ResultListener
import kotlinx.android.synthetic.main.fragment_signup_3.*

class SignupStep3Fragment(override var listener: ResultListener) : BaseStepFragment() {

    var height: String = "0"
        get() = a_signup_height_text.text.toString()

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
    }

    override fun checkValidation() {
        if (height.isEmpty() || height.equals("0")) {
            a_signup_height_text.requestFocus()
            a_signup_height_text.error = "Empty height!"
        } else if (weight.isEmpty() || weight.equals("0")) {
            a_signup_weight_text.requestFocus()
            a_signup_weight_text.error = "Empty weight!"
        } else {
            var data = HashMap<String, String>()
            data.put("height", height)
            data.put("weight", weight)
            data.put("isMetric", isMetric.toString())
            listener.onResultListener(true, data)
        }
    }
}