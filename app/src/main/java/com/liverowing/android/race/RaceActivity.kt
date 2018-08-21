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
import com.liverowing.android.util.metric.Metric
import com.liverowing.android.views.HighlightFirstWordTextView
import kotlinx.android.synthetic.main.activity_race.*
import kotlinx.android.synthetic.main.race_racing.*

class RaceActivity : MvpLceActivity<ConstraintLayout, WorkoutType, RaceView, RacePresenter>(), RaceView {
    private lateinit var workoutType: WorkoutType

    override fun createPresenter() = RacePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_race)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        loadData(false)

        // Click on primary metrics
        race_racing_gauge_left.setOnClickListener { presenter.switchPrimaryMetricLeft() }
        race_racing_gauge_right.setOnClickListener { presenter.switchPrimaryMetricRight() }

        // Click on secondary metrics
        race_racing_metric_left.setOnClickListener { presenter.switchSecondaryMetricLeft() }
        race_racing_metric_center.setOnClickListener { presenter.switchSecondaryMetricCenter() }
        race_racing_metric_right.setOnClickListener { presenter.switchSecondaryMetricRight() }
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

    override fun setLoadingMessage(message: String) {
        (loadingView as HighlightFirstWordTextView).text = message
    }

    override fun primaryMetricLeftUpdated(metric: Metric.Primary, animated: Boolean) {
        race_racing_gauge_left.apply {
            title = metric.title
            subtitle = metric.subtitle
            scaleStartValue = metric.min
            scaleEndValue = metric.max
            formatter = metric.formatter
            setValue(metric.value, metric.subvalue, animated)
        }
    }

    override fun primaryMetricRightUpdated(metric: Metric.Primary, animated: Boolean) {
        race_racing_gauge_right.apply {
            title = metric.title
            subtitle = metric.subtitle
            scaleStartValue = metric.min
            scaleEndValue = metric.max
            formatter = metric.formatter
            setValue(metric.value, metric.subvalue, animated)
        }
    }

    override fun secondaryMetricLeftUpdated(metric: Metric.Secondary) {
        race_racing_metric_left.apply {
            title = metric.title
            value = metric.formattedValue
        }
    }

    override fun secondaryMetricCenterUpdated(metric: Metric.Secondary) {
        race_racing_metric_center.apply {
            title = metric.title
            value = metric.formattedValue
        }
    }

    override fun secondaryMetricRightUpdated(metric: Metric.Secondary) {
        race_racing_metric_right.apply {
            title = metric.title
            value = metric.formattedValue
        }
    }

    override fun workoutStarting() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun workoutResting() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun workoutContinuing() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun workoutFinishing() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun positionsUpdated() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
