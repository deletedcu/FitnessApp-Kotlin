package com.liverowing.android.loggedout.signup

import android.graphics.Bitmap
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.liverowing.android.model.parse.User
import com.parse.ParseException
import com.parse.ParseFile
import com.parse.ParseUser
import kotlinx.io.ByteArrayOutputStream

class SignupPresenter: MvpBasePresenter<SignupView>() {

    fun signup(newUser: User, password: String, bitmap: Bitmap?) {
        ifViewAttached { it.showLoading() }
        newUser.signUpInBackground {e: ParseException? ->
            if (e != null) {
                ifViewAttached { it.showError(e) }
            } else {
                saveUser(newUser, bitmap)
                ifViewAttached { it.signupuccessful() }
            }
        }
    }

    private fun saveUser(newUser: User, bitmap: Bitmap?) {
        val user = ParseUser.getCurrentUser() as User
        user.isMetric = newUser.isMetric
        user.gender = newUser.gender
        user.weight = newUser.weight
        user.height = newUser.height
        user.dob = newUser.dob
        if (bitmap != null) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val bytes = baos.toByteArray()
            val parseFile = ParseFile("profilePicture.png", bytes)
            user.image = parseFile
        }

        user.saveInBackground {e: ParseException? ->
            if (e != null) {
                ifViewAttached { it.showError(e) }
            }
        }
    }

}