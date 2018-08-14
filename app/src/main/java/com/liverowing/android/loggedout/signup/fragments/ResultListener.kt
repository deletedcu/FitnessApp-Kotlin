package com.liverowing.android.loggedout.signup.fragments

interface ResultListener {
    fun onResultListener(state: Boolean, data: HashMap<String, String>?)
}