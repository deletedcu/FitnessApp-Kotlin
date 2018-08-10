package com.liverowing.android.activity.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import com.liverowing.android.signup.fragments.BaseStepFragment
import com.liverowing.android.signup.fragments.ResultListener
import kotlinx.android.synthetic.main.fragment_signup_2.*

class SignupStep2Fragment(override var listener: ResultListener) : BaseStepFragment() {

    var password: String = ""
        get() = a_signup_password_text.text.toString()

    var confirmPassword: String = ""
        get() = a_signup_password_confirm_text.text.toString()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun checkValidation() {
        if (password.isEmpty()) {
            a_signup_password_text.requestFocus()
            a_signup_password_text.error = "Empty password!"
        } else if (password.length < 6) {
            a_signup_password_text.requestFocus()
            a_signup_password_text.error = "Password must be of minimum 6 characters!"
        } else if (!password.equals(confirmPassword)) {
            a_signup_password_confirm_text.requestFocus()
            a_signup_password_confirm_text.error = "Password does not match the confirm password!"
        } else {
            var map = HashMap<String, String>()
            map.put("password", password)
            listener.onResultListener(true, map)
        }
    }

}