package com.liverowing.android.activity.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liverowing.android.R
import com.liverowing.android.extensions.isValidEmail
import com.liverowing.android.loggedout.signup.fragments.BaseStepFragment
import com.liverowing.android.loggedout.signup.fragments.ResultListener
import kotlinx.android.synthetic.main.fragment_signup_1.*
import com.kaopiz.kprogresshud.KProgressHUD
import com.liverowing.android.util.Utils
import com.parse.ParseUser

class SignupStep1Fragment(override var listener: ResultListener) : BaseStepFragment() {

    var username: String = ""
        get() {
            return a_signup_username_text.text.toString()
        }
    var email: String = ""
        get() {
            return a_signup_email_text.text.toString()
        }

    private lateinit var hud: KProgressHUD

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hud = KProgressHUD(context)

        setupUI()
    }

    private fun setupUI() {
        layout_step1.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                Utils.hideKeyboard(activity!!)
            }
        })

        a_signup_username_text.requestFocus()
        Utils.showKeyboard(activity!!)
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
            hud.show()
            isValidUserName(username, object: ResultListener {
                override fun onResultListener(state: Boolean, data: HashMap<String, String>?) {
                    if (state) {
                        isValidEmail(email, object: ResultListener {
                            override fun onResultListener(state: Boolean, data: HashMap<String, String>?) {
                                hud.dismiss()
                                if (state) {
                                    next()
                                } else {
                                    a_signup_email_text.requestFocus()
                                    a_signup_email_text.error = "The email address you have entered is already registered."
                                }
                            }
                        })
                    } else {
                        hud.dismiss()
                        a_signup_username_text.requestFocus()
                        a_signup_username_text.error = "The username you have entered is already registered."
                    }
                }
            })
        }
    }

    private fun next() {
        var map = HashMap<String, String>()
        map.put("username", username)
        map.put("email", email)
        listener.onResultListener(true, map)
    }

    private fun isValidUserName(username: String, listener: ResultListener) {
        val query = ParseUser.getQuery()
        query.whereEqualTo("username", username)
        query.countInBackground { count, e ->
            if (e == null && count == 0) {
                listener.onResultListener(true, null)
            } else {
                listener.onResultListener(false, null)
            }
        }
    }

    private fun isValidEmail(email: String, listener: ResultListener) {
        val query = ParseUser.getQuery()
        query.whereEqualTo("email", email)
        query.countInBackground { count, e ->
            if (e == null && count == 0) {
                listener.onResultListener(true, null)
            } else {
                listener.onResultListener(false, null)
            }
        }
    }

}