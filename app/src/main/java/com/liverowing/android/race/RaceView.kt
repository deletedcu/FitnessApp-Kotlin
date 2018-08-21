package com.liverowing.android.race

import com.hannesdorfmann.mosby3.mvp.lce.MvpLceView
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.util.metric.Metric

interface RaceView : MvpLceView<WorkoutType> {
    fun setLoadingMessage(message: String)

    fun primaryMetricLeftUpdated(metric: Metric.Primary, animated: Boolean)
    fun primaryMetricRightUpdated(metric: Metric.Primary, animated: Boolean)

    fun secondaryMetricLeftUpdated(metric: Metric.Secondary)
    fun secondaryMetricCenterUpdated(metric: Metric.Secondary)
    fun secondaryMetricRightUpdated(metric: Metric.Secondary)

    fun workoutStarting()
    fun workoutResting()
    fun workoutContinuing()
    fun workoutFinishing()

    fun positionsUpdated()
}