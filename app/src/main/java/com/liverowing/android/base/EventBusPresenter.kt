package com.liverowing.android.base

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

abstract class EventBusPresenter<V : MvpView> : MvpBasePresenter<V>() {
    protected var eventBus: EventBus = EventBus.getDefault()

    init {
        Timber.d("**** init")
    }

    override fun attachView(view: V) {
        Timber.d("**** attachView")
        super.attachView(view)
        eventBus.register(this)
    }

    override fun detachView(retainInstance: Boolean) {
        Timber.d("**** detachView")
        super.detachView(retainInstance)
        eventBus.unregister(this)
    }
}