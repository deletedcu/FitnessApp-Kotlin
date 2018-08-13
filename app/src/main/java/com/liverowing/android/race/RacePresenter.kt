package com.liverowing.android.race

import com.liverowing.android.base.EventBusPresenter
import com.liverowing.android.model.pm.RowingStatus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RacePresenter : EventBusPresenter<RaceView>() {

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onRowingStatus(data: RowingStatus) {

    }
}