package com.liverowing.android.loggedout.signup

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.parse.ParseException

interface SignupView: MvpView {
    fun showLoading()
    fun showError(e: ParseException)
    fun signupuccessful()
}