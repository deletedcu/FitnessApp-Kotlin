package com.liverowing.android.loggedout.signup.fragments

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

class SignupStep2Fragment : BaseStepFragment() {

    override lateinit var listener: ResultListener

    var password: String = ""
        get() = a_signup_password_text.text.toString()

    var confirmPassword: String = ""
        get() = a_signup_password_confirm_text.text.toString()

    companion object {
        fun newInstance(listener: ResultListener) : SignupStep2Fragment {
            var f = SignupStep2Fragment()
            f.listener = listener;
            return f
        }
    }

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

        a_signup_password_text.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (a_signup_password.isErrorEnabled) {
                    a_signup_password.isErrorEnabled = false
                }
            } else {
                checkPasswordValidation()
            }
        }

        a_signup_password_text.setOnKeyListener(View.OnKeyListener { _, actionId, event ->
            if (event!!.keyCode == KeyEvent.ACTION_DOWN && actionId == EditorInfo.IME_ACTION_NEXT) {
                if (checkPasswordValidation()) {
                    return@OnKeyListener true
                }
            }
            false
        })

        a_signup_password_confirm_text.setOnKeyListener(View.OnKeyListener { _, actionId, event ->
            if (event!!.keyCode == KeyEvent.ACTION_DOWN && actionId == EditorInfo.IME_ACTION_DONE) {
                if (checkConfirmPasswordValidation()) {
                    return@OnKeyListener true
                }
            }
            false
        })

        a_signup_password_text.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if (a_signup_password.isErrorEnabled) {
                    a_signup_password.isErrorEnabled = false
                }
            }
        })

        a_signup_password_confirm_text.addTextChangedListener(object: TextWatcher {
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (a_signup_password_confirm.isErrorEnabled) {
                    a_signup_password_confirm.isErrorEnabled = false
                }
            }
        })

        a_signup_password_text.requestFocus()
        Utils.showKeyboard(activity!!)
    }

    private fun checkPasswordValidation(): Boolean {
        if (password.isEmpty()) {
            a_signup_password.error = "Empty password!"
            return false
        } else if (password.length < 6) {
            a_signup_password.error = "Password must be of minimum 6 characters!"
            return false
        }
        return true
    }

    private fun checkConfirmPasswordValidation(): Boolean {
        if (!password.equals(confirmPassword)) {
            a_signup_password_text.clearFocus()
            a_signup_password_confirm_text.requestFocus()
            a_signup_password_confirm.error = "Password does not match the confirm password!"
            return false
        }
        return true
    }

    override fun checkValidation() {
        if (!checkPasswordValidation()) {
            a_signup_password_text.requestFocus()
        } else if (!checkConfirmPasswordValidation()) {
            a_signup_password_confirm_text.requestFocus()
        } else {
            var map = HashMap<String, String>()
            map.put("password", password)
            listener.onResultListener(true, map)
        }
    }

}