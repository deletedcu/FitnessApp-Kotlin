package com.liverowing.android.activity.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import com.liverowing.android.loggedout.signup.fragments.BaseStepFragment
import com.liverowing.android.loggedout.signup.fragments.ResultListener
import kotlinx.android.synthetic.main.fragment_signup_2.*
import kotlinx.android.synthetic.main.fragment_signup_2.view.*

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

        view.a_signup_password_text.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (editable!!.length > 0) {
                    if (a_signup_password.isErrorEnabled) {
                        a_signup_password.isErrorEnabled = false
                    }
                }
            }
        })

        view.a_signup_password_confirm_text.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (editable!!.length > 0) {
                    if (a_signup_password_confirm.isErrorEnabled) {
                        a_signup_password_confirm.isErrorEnabled = false
                    }
                }
            }
        })
    }

    override fun checkValidation() {
        if (password.isEmpty()) {
            a_signup_password_text.requestFocus()
            a_signup_password.error = "Empty password!"
        } else if (password.length < 6) {
            a_signup_password_text.requestFocus()
            a_signup_password.error = "Password must be of minimum 6 characters!"
        } else if (!password.equals(confirmPassword)) {
            a_signup_password_confirm_text.requestFocus()
            a_signup_password_confirm.error = "Password does not match the confirm password!"
        } else {
            var map = HashMap<String, String>()
            map.put("password", password)
            listener.onResultListener(true, map)
        }
    }

}