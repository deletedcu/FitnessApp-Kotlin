package com.liverowing.android.base

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import org.greenrobot.eventbus.EventBus

abstract class EventBusPresenter<V : MvpView> : MvpBasePresenter<V>() {
    protected var eventBus: EventBus = EventBus.getDefault()

    fun onStart() {
        eventBus.register(this)
    }

    fun onStop() {
        eventBus.unregister(this)
    }
}