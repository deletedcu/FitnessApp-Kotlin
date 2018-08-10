package com.liverowing.android.signup.fragments

interface ResultListener {
    fun onResultListener(state: Boolean, data: HashMap<String, String>)
}