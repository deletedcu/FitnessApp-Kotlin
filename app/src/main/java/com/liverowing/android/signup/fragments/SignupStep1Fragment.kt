package com.liverowing.android.activity.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import com.liverowing.android.extensions.isValidEmail
import com.liverowing.android.signup.fragments.BaseStepFragment
import com.liverowing.android.signup.fragments.ResultListener
import kotlinx.android.synthetic.main.fragment_signup_1.*

class SignupStep1Fragment(override var listener: ResultListener) : BaseStepFragment() {

    var username: String = ""
        get() {
            return a_signup_username_text.text.toString()
        }
    var email: String = ""
        get() {
            return a_signup_email_text.text.toString()
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
    }

    override fun checkValidation() {
        if (username.isEmpty()) {
            a_signup_username.editText!!.error = "Username is empty!"
            listener!!.onResultListener(false, null)
            return
        }
        if (email.isEmpty()) {
            a_signup_email.editText!!.error = "Email is empty!"
            listener!!.onResultListener(false, null)
            return
        } else if (!email.isValidEmail()) {
            a_signup_email.editText!!.error = "Invalid email!"
            listener!!.onResultListener(false, null)
            return
        }
        var map = HashMap<String, String>()
        map.put("username", username)
        map.put("email", email)
        listener!!.onResultListener(true, map)
    }
}