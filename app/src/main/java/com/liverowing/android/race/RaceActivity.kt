package com.liverowing.android.race

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceActivity
import com.liverowing.android.R
import com.liverowing.android.model.parse.WorkoutType
import kotlinx.android.synthetic.main.activity_race.*

class RaceActivity : MvpLceActivity<ConstraintLayout, WorkoutType, RaceView, RacePresenter>(), RaceView {
    private lateinit var workoutType: WorkoutType

    override fun createPresenter() = RacePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        loadData(false)
    }

    override fun onDestroy() {
        super.onDestroy()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            val decorView = window.decorView
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

    }

    override fun onResume() {
        super.onResume()

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun setData(data: WorkoutType) {
        workoutType = data

        Glide
                .with(this@RaceActivity)
                .load(workoutType.image?.url)
                .into(a_race_background)
    }

    override fun loadData(pullToRefresh: Boolean) {
        val workoutTypeId = RaceActivityArgs.fromBundle(intent.extras).workoutTypeId
        if (workoutTypeId.isNotEmpty()) {
            presenter.loadWorkoutTypeById(workoutTypeId)
        }
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String {
        return e.toString()
    }

}
