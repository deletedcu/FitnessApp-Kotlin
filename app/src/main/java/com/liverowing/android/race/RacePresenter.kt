package com.liverowing.android.race

import com.liverowing.android.Preferences
import com.liverowing.android.base.EventBusPresenter
import com.liverowing.android.model.parse.WorkoutType
import com.liverowing.android.model.pm.RowingStatus
import com.liverowing.android.util.metric.Metric
import com.liverowing.android.util.metric.NumericMetricFormatter
import com.liverowing.android.util.metric.TimeMetricFormatter
import com.parse.ParseException
import com.parse.ParseQuery
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class RacePresenter : EventBusPresenter<RaceView>() {
    data class AggregateMetric(var num: Int, var total: Float, var high: Float)

    private val primaryMetricAggregates = mapOf(
            Metric.SECONDARY_METRIC_POWER to AggregateMetric(0, 0f, 0f),
            Metric.SECONDARY_METRIC_CALORIES_PER_HOUR to AggregateMetric(0, 0f, 0f),
            Metric.SECONDARY_METRIC_RATE to AggregateMetric(0, 0f, 0f),
            Metric.SECONDARY_METRIC_PACE to AggregateMetric(0, 0f, 0f)
    )
    private val metrics = mapOf(
            Metric.PRIMARY_METRIC_LEFT_PACE_WITH_AVG to Metric.Primary("Pace", "/Avg", TimeMetricFormatter(false), 180f, 60f, 0f, 0f),
            Metric.PRIMARY_METRIC_LEFT_PACE_WITH_PEAK to Metric.Primary("Pace", "/Peak", TimeMetricFormatter(false), 180f, 60f, 0f, 0f),
            Metric.PRIMARY_METRIC_LEFT_POWER_WITH_AVG to Metric.Primary("Power", "/Avg", NumericMetricFormatter("%.0f"), 20f, 260f, 0f, 0f),
            Metric.PRIMARY_METRIC_LEFT_POWER_WITH_PEAK to Metric.Primary("Power", "/Peak", NumericMetricFormatter("%.0f"), 20f, 260f, 0f, 0f),
            Metric.PRIMARY_METRIC_RIGHT_RATE_WITH_AVG to Metric.Primary("Rate", "/Avg", NumericMetricFormatter("%.0f"), 16f, 40f, 0f, 0f),
            Metric.PRIMARY_METRIC_RIGHT_RATE_WITH_PEAK to Metric.Primary("Rate", "/Peak", NumericMetricFormatter("%.0f"), 16f, 40f, 0f, 0f),
            Metric.PRIMARY_METRIC_RIGHT_CALORIES_WITH_AVG to Metric.Primary("Cals/hr", "/Avg", NumericMetricFormatter("%.0f"), 400f, 1200f, 0f, 0f),
            Metric.PRIMARY_METRIC_RIGHT_CALORIES_WITH_PEAK to Metric.Primary("Cals/hr", "/Peak", NumericMetricFormatter("%.0f"), 400f, 1200f, 0f, 0f),

            Metric.SECONDARY_METRIC_POWER to Metric.Secondary("Power", NumericMetricFormatter("%.0f"), null, "N/A"),
            Metric.SECONDARY_METRIC_PACE to Metric.Secondary("Pace", TimeMetricFormatter(true), 0f),
            Metric.SECONDARY_METRIC_RATE to Metric.Secondary("Rate", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_HEART_RATE to Metric.Secondary("Heart rate", NumericMetricFormatter("%.0f"), null, "N/A"),
            Metric.SECONDARY_METRIC_SPEED to Metric.Secondary("Speed", NumericMetricFormatter("%.2f"), 0f),
            Metric.SECONDARY_METRIC_PREVIOUS_RATE to Metric.Secondary("Previous rate", NumericMetricFormatter("%.0f"), null, "N/A"),
            Metric.SECONDARY_METRIC_PREVIOUS_PACE to Metric.Secondary("Previous pace", TimeMetricFormatter(true), null, "N/A"),
            Metric.SECONDARY_METRIC_PREVIOUS_POWER to Metric.Secondary("Previous avg. power", NumericMetricFormatter("%.0f"), null, "N/A"),
            Metric.SECONDARY_METRIC_STROKE_LENGTH to Metric.Secondary("Stroke length", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_STROKE_DISTANCE to Metric.Secondary("Stroke distance", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_STROKE_RATIO to Metric.Secondary("Stroke ratio", NumericMetricFormatter("%.0f%%"), 0f),
            Metric.SECONDARY_METRIC_STROKE_COUNT to Metric.Secondary("Stroke count", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_TIME to Metric.Secondary("Time", TimeMetricFormatter(true), 0f),
            Metric.SECONDARY_METRIC_DISTANCE to Metric.Secondary("Distance", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_TOTAL_TIME to Metric.Secondary("Total time", TimeMetricFormatter(true), 0f),
            Metric.SECONDARY_METRIC_CALORIE_COUNT to Metric.Secondary("Calorie count", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_CALORIES_PER_HOUR to Metric.Secondary("Calories/hr", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_SPI to Metric.Secondary("Stroke Power Index", NumericMetricFormatter("%.1f"), null, "N/A"),
            Metric.SECONDARY_METRIC_SPLIT_INTERVAL_NUMBER to Metric.Secondary("Split/Interval", NumericMetricFormatter("%.0f"), 1f),
            Metric.SECONDARY_METRIC_AVERAGE_PACE to Metric.Secondary("Avg. pace", TimeMetricFormatter(true), 0f),
            Metric.SECONDARY_METRIC_AVERAGE_POWER to Metric.Secondary("Avg. power", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_AVERAGE_RATE to Metric.Secondary("Avg. rate", NumericMetricFormatter("%.0f"), 0f),
            Metric.SECONDARY_METRIC_AVERAGE_CALORIES_PER_HOUR to Metric.Secondary("Avg. calories/hr", NumericMetricFormatter("%.0f"), 0f),

            Metric.SECONDARY_METRIC_DRIVE_TIME to Metric.Secondary("Drive time", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_STROKE_RECOVERY_TIME to Metric.Secondary("Stroke recovery time", NumericMetricFormatter("%.2f"), null, "N/A"),
            Metric.SECONDARY_METRIC_TARGET_RATE to Metric.Secondary("Target rate", NumericMetricFormatter("%.0f"), null, "Not set"),
            Metric.SECONDARY_METRIC_PROJECTED_WORK_TIME to Metric.Secondary("Projected time", TimeMetricFormatter(true), null, "N/A"),
            Metric.SECONDARY_METRIC_PROJECTED_WORK_DISTANCE to Metric.Secondary("Projected distance", NumericMetricFormatter("%.0f"), null, "N/A")
    )

    fun loadWorkoutTypeById(id: String) {
        ifViewAttached { it.showLoading(false) }
        val query = ParseQuery.getQuery(WorkoutType::class.java)
        query.include("createdBy")
        query.include("segments")
        query.getInBackground(id) { workoutType: WorkoutType?, e: ParseException? ->
            if (e !== null) {
                ifViewAttached { it.showError(e, false) }
            } else {
                eventBus.postSticky(workoutType)
            }
        }
    }

    fun switchPrimaryMetricLeft() {
        var current = Preferences.primaryMetricLeft + 1
        if (current >= Metric.PRIMARY_METRIC_LEFT_HIGH) {
            current = Metric.PRIMARY_METRIC_LEFT_LOW
        }
        Preferences.primaryMetricLeft = current

        ifViewAttached {
            it.primaryMetricLeftUpdated(metrics[current] as Metric.Primary, true)
        }
    }

    fun switchPrimaryMetricRight() {
        var current = Preferences.primaryMetricRight + 1
        if (current >= Metric.PRIMARY_METRIC_RIGHT_HIGH) {
            current = Metric.PRIMARY_METRIC_RIGHT_LOW
        }
        Preferences.primaryMetricRight = current

        ifViewAttached {
            it.primaryMetricRightUpdated(metrics[current] as Metric.Primary, true)
        }
    }

    fun switchSecondaryMetricLeft() {
        var current = Preferences.secondaryMetricLeft + 1
        if (current >= Metric.SECONDARY_METRIC_HIGH) {
            current = Metric.SECONDARY_METRIC_LOW
        }
        Preferences.secondaryMetricLeft = current

        ifViewAttached {
            it.secondaryMetricLeftUpdated(metrics[current] as Metric.Secondary)
        }
    }
    
    fun switchSecondaryMetricCenter() {
        var current = Preferences.secondaryMetricCenter + 1
        if (current >= Metric.SECONDARY_METRIC_HIGH) {
            current = Metric.SECONDARY_METRIC_LOW
        }
        Preferences.secondaryMetricCenter = current

        ifViewAttached {
            it.secondaryMetricCenterUpdated(metrics[current] as Metric.Secondary)
        }
    }
    
    fun switchSecondaryMetricRight() {
        var current = Preferences.secondaryMetricRight + 1
        if (current >= Metric.SECONDARY_METRIC_HIGH) {
            current = Metric.SECONDARY_METRIC_LOW
        }
        Preferences.secondaryMetricRight = current

        ifViewAttached {
            it.secondaryMetricRightUpdated(metrics[current] as Metric.Secondary)
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onWorkoutTypeMainThread(workoutType: WorkoutType) {
        ifViewAttached {
            it.primaryMetricLeftUpdated(metrics[Preferences.primaryMetricLeft] as Metric.Primary, false)
            it.primaryMetricRightUpdated(metrics[Preferences.primaryMetricRight] as Metric.Primary, false)
            
            it.secondaryMetricLeftUpdated(metrics[Preferences.secondaryMetricLeft] as Metric.Secondary)
            it.secondaryMetricCenterUpdated(metrics[Preferences.secondaryMetricCenter] as Metric.Secondary)
            it.secondaryMetricRightUpdated(metrics[Preferences.secondaryMetricRight] as Metric.Secondary)
            
            
            it.setData(workoutType)
            it.showContent()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onRowingStatus(data: RowingStatus) {

    }
}