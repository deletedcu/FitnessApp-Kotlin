package com.liverowing.android.signup

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.parse.ParseException
import com.parse.ParseUser

interface SignupView: MvpView {
    fun showLoading()
    fun showError(e: ParseException)
    fun signupuccessful(user: ParseUser)
}