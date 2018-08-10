package com.liverowing.android.signup

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.User
import com.parse.ParseException
import com.parse.ParseUser

class SignupPresenter: MvpBasePresenter<SignupView>() {

    fun signup(newUser: User, password: String) {
        ifViewAttached { it.showLoading() }
        newUser.signUpInBackground {e: ParseException? ->
            if (e != null) {
                ifViewAttached { it.showError(e) }
            } else {
                newUser.saveInBackground()
                login(newUser, password)
            }
        }
    }

    fun login(newUser: User, password: String) {
        var query = ParseUser.getQuery()
        query.whereEqualTo(newUser.email, newUser.username)
        query.getFirstInBackground { realUser, e ->
            if (e != null) {
                ifViewAttached { it.showError(e) }
            } else {
                ParseUser.logInInBackground(newUser.username, password) { user, e ->
                    if (e != null) {
                        ifViewAttached { it.showError(e) }
                    } else {
                        ifViewAttached { it.signupuccessful(user) }
                    }
                }
            }
        }
    }

}