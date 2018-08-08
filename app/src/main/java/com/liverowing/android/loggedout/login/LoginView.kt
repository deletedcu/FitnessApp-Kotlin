package com.liverowing.android.loggedout.login

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.parse.ParseException
import com.parse.ParseUser

interface LoginView : MvpView {
    fun showLoginForm()
    fun showLoading()
    fun showError(e: ParseException)
    fun loginSuccessful(user: ParseUser)
}