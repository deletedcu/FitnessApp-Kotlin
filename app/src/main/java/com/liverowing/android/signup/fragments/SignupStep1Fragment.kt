package com.liverowing.android.activity.login

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import com.liverowing.android.extensions.isValidEmail
import com.liverowing.android.signup.fragments.BaseStepFragment
import com.liverowing.android.signup.fragments.ResultListener
import kotlinx.android.synthetic.main.fragment_signup_1.*
import android.text.Spanned

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

        a_signup_username_text.filters = arrayOf<InputFilter>(object : InputFilter.AllCaps() {
            override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence {
                return source.toString().toLowerCase().replace(" ", "")
            }
        })
    }

    override fun checkValidation() {
        if (username.isEmpty()) {
            a_signup_username_text.requestFocus()
            a_signup_username_text.error = "Username is empty!"
        } else if (email.isEmpty()) {
            a_signup_email_text.requestFocus()
            a_signup_email_text.error = "Email is empty!"
        } else if (!email.isValidEmail()) {
            a_signup_email_text.requestFocus()
            a_signup_email_text.error = "Invalid email!"
        } else {
            var map = HashMap<String, String>()
            map.put("username", username)
            map.put("email", email)
            listener!!.onResultListener(true, map)
        }
    }
}