package com.liverowing.android.signup.fragments

import androidx.fragment.app.Fragment

abstract class BaseStepFragment: Fragment() {
    abstract fun checkValidation()
    abstract var listener: ResultListener
}