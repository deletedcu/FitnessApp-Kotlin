package com.liverowing.android.race

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation.findNavController
import com.bumptech.glide.Glide
import com.hannesdorfmann.mosby3.mvp.lce.MvpLceFragment
import com.liverowing.android.LiveRowing
import com.liverowing.android.MainActivity
import com.liverowing.android.R
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.util.metric.Metric
import com.liverowing.android.views.HighlightFirstWordTextView
import kotlinx.android.synthetic.main.fragment_race.*
import kotlinx.android.synthetic.main.race_racing.*

class RaceFragment : MvpLceFragment<ConstraintLayout, WorkoutType, RaceView, RacePresenter>(), RaceView {
    private lateinit var workoutType: WorkoutType

    override fun createPresenter() = RacePresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_race, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData(false)

        // Click on primary metrics
        race_racing_gauge_left.setOnClickListener { presenter.switchPrimaryMetricLeft() }
        race_racing_gauge_right.setOnClickListener { presenter.switchPrimaryMetricRight() }

        // Click on secondary metrics
        race_racing_metric_left.setOnClickListener { presenter.switchSecondaryMetricLeft() }
        race_racing_metric_center.setOnClickListener { presenter.switchSecondaryMetricCenter() }
        race_racing_metric_right.setOnClickListener { presenter.switchSecondaryMetricRight() }

        loadingView.setOnClickListener {
            if (!LiveRowing.deviceReady) {
                findNavController(view).navigate(R.id.deviceScanAction)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity is MainActivity) {
            (activity as MainActivity).setImmersiveModeState(false)
        }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onResume() {
        super.onResume()

        if (activity is MainActivity) {
            (activity as MainActivity).setImmersiveModeState(true)
        }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
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

        if (workoutType.image?.url != null) {
            Glide
                    .with(this@RaceFragment)
                    .load(workoutType.image?.url)
                    .into(a_race_background)
        } else {
            // TODO: Remember to remove and have WorkoutType decide a proper default image (Remote Config?)'
            Glide
                    .with(this@RaceFragment)
                    .load("https://stmed.net/sites/default/files/rowing-wallpapers-31335-7292265.jpg")
                    .into(a_race_background)
        }

    }

    override fun loadData(pullToRefresh: Boolean) {
        val workoutTypeId = RaceFragmentArgs.fromBundle(arguments).workoutTypeId
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

    override fun setStrokeRatioVisible(visible: Boolean) {
        race_racing_stroke_ratio.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun strokeRatioUpdated(ratio: Float) {
        race_racing_stroke_ratio.setStrokeRatio(ratio)
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
