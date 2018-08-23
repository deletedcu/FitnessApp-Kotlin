package com.liverowing.android.base

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

abstract class EventBusPresenter<V : MvpView> : MvpBasePresenter<V>() {
    protected var eventBus: EventBus = EventBus.getDefault()

    fun onStart() {
        Timber.d("Subscriber onStart")
        eventBus.register(this)
    }

    fun onStop() {
        Timber.d("Subscriber onStop")
        eventBus.unregister(this)
    }
}