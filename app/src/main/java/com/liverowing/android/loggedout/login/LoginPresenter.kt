package com.liverowing.android.loggedout.login

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.parse.ParseUser

class LoginPresenter : MvpBasePresenter<LoginView>() {
    fun login(username: String, password: String) {
        ifViewAttached { it.showLoading() }
        ParseUser.logInInBackground(username, password) { user, e ->
            if (e != null) {
                ifViewAttached { it.showError(e) }
            } else {
                ifViewAttached { it.loginSuccessful(user) }
            }
        }
    }
}