package com.liverowing.android.loggedout.signup

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.User
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseUser

class SignupPresenter: MvpBasePresenter<SignupView>() {

    fun signup(newUser: User, bitmapBytes: ByteArray?) {
        ifViewAttached { it.showLoading() }
        newUser.signUpInBackground {e: ParseException? ->
            if (e != null) {
                ifViewAttached { it.showError(e) }
            } else {
                saveUser(newUser, bitmapBytes)
                ifViewAttached { it.signupuccessful() }
            }
        }
    }

    private fun saveUser(newUser: User, bitmapBytes: ByteArray?) {
        val user = ParseUser.getCurrentUser() as User
        user.isMetric = newUser.isMetric
        user.gender = newUser.gender
        user.weight = newUser.weight
        user.height = newUser.height
        user.dob = newUser.dob
        user.boatColor = newUser.boatColor
        user.hatColor = newUser.hatColor
        user.`class` = newUser.`class`
        user.maxHR = newUser.maxHR
        if (bitmapBytes != null) {
            val parseFile = ParseFile("profilePicture.png", bitmapBytes)
            user.image = parseFile
        }

        user.saveInBackground {e: ParseException? ->
            if (e != null) {
                ifViewAttached { it.showError(e) }
            }
        }
    }

}