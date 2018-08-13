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
                // Save user information
                saveUser(realUser, newUser)

                // Login with new user
                ParseUser.logInInBackground(realUser.username, password) { user, e ->
                    if (e != null) {
                        ifViewAttached { it.showError(e) }
                    } else {
                        ifViewAttached { it.signupuccessful(user) }
                    }
                }
            }
        }
    }

    fun saveUser(realUser: ParseUser, newUser: User) {
        val user: User = realUser as User
        user.isMetric = newUser.isMetric
        user.gender = newUser.gender
        user.weight = newUser.weight
        user.height = newUser.height
        user.dob = newUser.dob
        user.image = newUser.image

        user.saveInBackground {e: ParseException? ->
            if (e != null) {
                ifViewAttached { it.showError(e) }
            }
        }
    }

}