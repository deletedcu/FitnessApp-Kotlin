package com.liverowing.android.activity.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.liverowing.android.R
import com.liverowing.android.extensions.isValidEmail
import kotlinx.android.synthetic.main.fragment_signup_1.*

class SignupStep1Fragment: Fragment() {

    var username: String = ""
        get() {
            return a_signup_username.editText!!.text.toString()
        }
    var email: String = ""
        get() {
            return a_signup_email.editText!!.text.toString()
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
    }

    private fun checkValidation() : Boolean {
        if (username.isEmpty()) {
            a_signup_username.editText!!.error = "Username is empty!"
            return false
        }
        if (email.isEmpty()) {
            a_signup_email.editText!!.error = "Email is empty!"
            return false
        } else if (!email.isValidEmail()) {
            a_signup_email.editText!!.error = "Invalid email!"
            return false
        }
        return true
    }
}