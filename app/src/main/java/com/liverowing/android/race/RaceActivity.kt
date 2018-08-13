package com.liverowing.android.race

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.liverowing.android.R

class RaceActivity : MvpActivity<RaceView, RacePresenter>(), RaceView {
    override fun createPresenter() = RacePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }
}
