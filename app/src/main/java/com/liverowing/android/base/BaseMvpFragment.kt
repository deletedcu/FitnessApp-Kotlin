package com.liverowing.android.base

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.hannesdorfmann.mosby3.mvp.MvpView

abstract class BaseMvpFragment<V: MvpView, P: MvpBasePresenter<V>> : MvpFragment<V, P>() {
    abstract fun onBackPressed()
}