package com.liverowing.android.race

import com.liverowing.android.Preferences
import com.liverowing.android.model.pm.*
import com.liverowing.android.util.metric.Metric
import com.liverowing.android.util.metric.NumericMetricFormatter
import com.liverowing.android.util.metric.TimeMetricFormatter

class MetricsHolder {
    data class AggregateMetric(var num: Int, var total: Float, var high: Float)

    private var mPrimaryMetricLeft = Preferences.primaryMetricLeft
    private var mPrimaryMetricRight = Preferences.primaryMetricRight
    private var mSecondaryMetricLeft = Preferences.secondaryMetricLeft
    private var mSecondaryMetricCenter = Preferences.secondaryMetricCenter
    private var mSecondaryMetricRight = Preferences.secondaryMetricRight

    val primaryMetricLeft get() = metrics[mPrimaryMetricLeft] as Metric.Primary
    val primaryMetricRight get() = metrics[mPrimaryMetricRight] as Metric.Primary
    val secondaryMetricLeft get() = metrics[mSecondaryMetricLeft] as Metric.Secondary
    val secondaryMetricCenter get() = metrics[mSecondaryMetricCenter] as Metric.Secondary
    val secondaryMetricRight get() = metrics[mSecondaryMetricRight] as Metric.Secondary

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

    fun switchPrimaryMetricLeft() {
        mPrimaryMetricLeft++
        if (mPrimaryMetricLeft >= Metric.PRIMARY_METRIC_LEFT_HIGH) {
            mPrimaryMetricLeft = Metric.PRIMARY_METRIC_LEFT_LOW
        }
        Preferences.primaryMetricLeft = mPrimaryMetricLeft
    }
    
    fun switchPrimaryMetricRight() {
        mPrimaryMetricRight++
        if (mPrimaryMetricRight >= Metric.PRIMARY_METRIC_RIGHT_HIGH) {
            mPrimaryMetricRight = Metric.PRIMARY_METRIC_RIGHT_LOW
        }
        Preferences.primaryMetricRight = mPrimaryMetricRight
    }
    
    fun switchSecondaryMetricLeft() {
        mSecondaryMetricLeft++
        if (mSecondaryMetricLeft >= Metric.SECONDARY_METRIC_HIGH) {
            mSecondaryMetricLeft = Metric.SECONDARY_METRIC_LOW
        }
        Preferences.secondaryMetricLeft = mSecondaryMetricLeft
    }

    fun switchSecondaryMetricCenter() {
        mSecondaryMetricCenter++
        if (mSecondaryMetricCenter >= Metric.SECONDARY_METRIC_HIGH) {
            mSecondaryMetricCenter = Metric.SECONDARY_METRIC_LOW
        }
        Preferences.secondaryMetricCenter = mSecondaryMetricCenter
    }
    
    fun switchSecondaryMetricRight() {
        mSecondaryMetricRight++
        if (mSecondaryMetricRight >= Metric.SECONDARY_METRIC_HIGH) {
            mSecondaryMetricRight = Metric.SECONDARY_METRIC_LOW
        }
        Preferences.secondaryMetricRight = mSecondaryMetricRight
    }
    
    
    
    fun onAdditionalRowingStatus1(data: ExtraRowingStatus1) {
        (metrics[Metric.SECONDARY_METRIC_RATE] as Metric.Secondary).value = data.strokeRate.toFloat()
        if (data.heartRate != 255) {
            (metrics[Metric.SECONDARY_METRIC_HEART_RATE] as Metric.Secondary).value = data.heartRate.toFloat()
        }
        (metrics[Metric.SECONDARY_METRIC_SPEED] as Metric.Secondary).value = data.speed.toFloat()
        (metrics[Metric.SECONDARY_METRIC_PACE] as Metric.Secondary).value = data.currentPace


        primaryMetricAggregates[Metric.SECONDARY_METRIC_PACE]!!.apply {
            num += 1
            total += data.currentPace
            high = if (high > data.currentPace) data.currentPace else high
        }
        (metrics[Metric.PRIMARY_METRIC_LEFT_PACE_WITH_AVG] as Metric.Primary).apply {
            value = data.currentPace
            subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_PACE]!!.total / primaryMetricAggregates[Metric.SECONDARY_METRIC_PACE]!!.num
        }
        (metrics[Metric.PRIMARY_METRIC_LEFT_PACE_WITH_PEAK] as Metric.Primary).apply {
            value = data.currentPace
            subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_PACE]!!.high
        }

        primaryMetricAggregates[Metric.SECONDARY_METRIC_RATE]!!.apply {
            num += 1
            total += data.strokeRate
            high = if (high < data.strokeRate) data.strokeRate.toFloat() else high
        }
        (metrics[Metric.PRIMARY_METRIC_RIGHT_RATE_WITH_AVG] as Metric.Primary).apply {
            value = data.strokeRate.toFloat()
            subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_RATE]!!.total / primaryMetricAggregates[Metric.SECONDARY_METRIC_RATE]!!.num
        }

        (metrics[Metric.PRIMARY_METRIC_RIGHT_RATE_WITH_PEAK] as Metric.Primary).apply {
            value = data.strokeRate.toFloat()
            subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_RATE]!!.high
        }
    }

    fun onAdditionalRowingStatus2(data: ExtraRowingStatus2) {
        (metrics[Metric.SECONDARY_METRIC_AVERAGE_PACE] as Metric.Secondary).value = data.splitIntAvgPace
        (metrics[Metric.SECONDARY_METRIC_CALORIE_COUNT] as Metric.Secondary).value = data.caloriesBurned.toFloat()
        (metrics[Metric.SECONDARY_METRIC_AVERAGE_POWER] as Metric.Secondary).value = data.splitIntAvgPower.toFloat()
        (metrics[Metric.SECONDARY_METRIC_AVERAGE_CALORIES_PER_HOUR] as Metric.Secondary).value = data.splitIntAvgCals.toFloat()
    }

    fun onStrokeData(data: StrokeData) {
        (metrics[Metric.SECONDARY_METRIC_STROKE_LENGTH] as Metric.Secondary).value = data.driveLength
        (metrics[Metric.SECONDARY_METRIC_STROKE_DISTANCE] as Metric.Secondary).value = data.strokeDistance
        (metrics[Metric.SECONDARY_METRIC_STROKE_COUNT] as Metric.Secondary).value = data.strokeCount.toFloat()
        (metrics[Metric.SECONDARY_METRIC_STROKE_RECOVERY_TIME] as Metric.Secondary).value = data.recoveryTime
        (metrics[Metric.SECONDARY_METRIC_DRIVE_TIME] as Metric.Secondary).value = data.driveTime
        (metrics[Metric.SECONDARY_METRIC_STROKE_RATIO] as Metric.Secondary).value = (data.recoveryTime / (data.driveTime + data.recoveryTime)) * 100
    }

    fun onAdditionalStrokeData(data: ExtraStrokeData) {
        (metrics[Metric.SECONDARY_METRIC_PROJECTED_WORK_DISTANCE] as Metric.Secondary).value = data.projWorkDist.toFloat()
        (metrics[Metric.SECONDARY_METRIC_PROJECTED_WORK_TIME] as Metric.Secondary).value = data.projWorkTime.toFloat()
        (metrics[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR] as Metric.Secondary).value = data.calories.toFloat()
        (metrics[Metric.SECONDARY_METRIC_POWER] as Metric.Secondary).value = data.power.toFloat()
        val spm = (metrics[Metric.SECONDARY_METRIC_RATE] as Metric.Secondary).value
        if (spm != null && spm > 0) {
            (metrics[Metric.SECONDARY_METRIC_SPI] as Metric.Secondary).value = data.power.toFloat() / spm
        }

        primaryMetricAggregates[Metric.SECONDARY_METRIC_POWER]!!.apply {
            num += 1
            total += data.power
            high = if (high < data.power) data.power.toFloat() else high
        }
        (metrics[Metric.PRIMARY_METRIC_LEFT_POWER_WITH_AVG] as Metric.Primary).apply {
            value = data.power.toFloat()
            subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_POWER]!!.total / primaryMetricAggregates[Metric.SECONDARY_METRIC_POWER]!!.num
        }
        (metrics[Metric.PRIMARY_METRIC_LEFT_POWER_WITH_PEAK] as Metric.Primary).apply {
            value = data.power.toFloat()
            subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_POWER]!!.high
        }

        primaryMetricAggregates[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR]!!.apply {
            num += 1
            total += data.calories
            high = if (high < data.calories) data.calories.toFloat() else high
        }
        (metrics[Metric.PRIMARY_METRIC_RIGHT_CALORIES_WITH_AVG] as Metric.Primary).apply {
            value = data.calories.toFloat()
            subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR]!!.total / primaryMetricAggregates[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR]!!.num
        }
        (metrics[Metric.PRIMARY_METRIC_RIGHT_CALORIES_WITH_PEAK] as Metric.Primary).apply {
            value = data.calories.toFloat()
            subvalue = primaryMetricAggregates[Metric.SECONDARY_METRIC_CALORIES_PER_HOUR]!!.high
        }
    }

    fun onAdditionalSplitIntervalData(data: ExtraSplitIntervalData) {
        (metrics[Metric.SECONDARY_METRIC_PREVIOUS_POWER] as Metric.Secondary).value = data.power.toFloat()
        (metrics[Metric.SECONDARY_METRIC_PREVIOUS_RATE] as Metric.Secondary).value = data.spm.toFloat()
        (metrics[Metric.SECONDARY_METRIC_PREVIOUS_PACE] as Metric.Secondary).value = data.pace
        (metrics[Metric.SECONDARY_METRIC_SPLIT_INTERVAL_NUMBER] as Metric.Secondary).value = data.intervalNumber + 1f
    }
}