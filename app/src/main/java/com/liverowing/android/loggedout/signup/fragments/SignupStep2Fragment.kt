package com.liverowing.android.activity.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.liverowing.android.R
import com.liverowing.android.loggedout.signup.fragments.BaseStepFragment
import com.liverowing.android.loggedout.signup.fragments.ResultListener
import com.liverowing.android.util.Utils
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

        setupUI()
    }

    private fun setupUI() {
        layout_step2.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Utils.hideKeyboard(activity!!)
            }
        })

        a_signup_password_text.addTextChangedListener(object: TextWatcher {
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

        a_signup_password_text.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, actionId: Int, event: KeyEvent?): Boolean {
                if (event!!.keyCode == KeyEvent.ACTION_DOWN && actionId == EditorInfo.IME_ACTION_NEXT) {
                    a_signup_password_text.clearFocus()
                    a_signup_password_confirm_text.requestFocus()
                    return true
                }
                return false
            }
        })

        a_signup_password_confirm_text.addTextChangedListener(object: TextWatcher {
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

        a_signup_password_text.requestFocus()
        Utils.showKeyboard(activity!!)
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