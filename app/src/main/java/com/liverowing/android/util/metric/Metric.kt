package com.liverowing.android.util.metric

class Metric {
    companion object {
        const val SECONDARY_METRIC_LOW = 0
        const val SECONDARY_METRIC_STROKE_COUNT = 0
        const val SECONDARY_METRIC_CALORIE_COUNT = 1
        const val SECONDARY_METRIC_SPLIT_INTERVAL_NUMBER = 2
        const val SECONDARY_METRIC_HEART_RATE = 3
        const val SECONDARY_METRIC_PREVIOUS_RATE = 4
        const val SECONDARY_METRIC_PREVIOUS_PACE = 5
        const val SECONDARY_METRIC_PREVIOUS_POWER = 6
        const val SECONDARY_METRIC_PROJECTED_WORK_TIME = 7
        const val SECONDARY_METRIC_PROJECTED_WORK_DISTANCE = 8
        const val SECONDARY_METRIC_AVERAGE_PACE = 9
        const val SECONDARY_METRIC_AVERAGE_POWER = 10
        const val SECONDARY_METRIC_AVERAGE_RATE = 11                // Only count half of this metric if it is the very first stroke, or the first stroke after rest.
        const val SECONDARY_METRIC_AVERAGE_CALORIES_PER_HOUR = 12
        const val SECONDARY_METRIC_TARGET_RATE = 13
        const val SECONDARY_METRIC_STROKE_RECOVERY_TIME = 14
        const val SECONDARY_METRIC_DRIVE_TIME = 15
        const val SECONDARY_METRIC_SPI = 16
        const val SECONDARY_METRIC_CALORIES_PER_HOUR = 17
        const val SECONDARY_METRIC_TOTAL_TIME = 18                  // Timer to update every second, autocorrect this value from SplitIntervalData.time so we're in sync with PM
        const val SECONDARY_METRIC_DISTANCE = 19
        const val SECONDARY_METRIC_TIME = 20
        const val SECONDARY_METRIC_STROKE_DISTANCE = 21
        const val SECONDARY_METRIC_STROKE_LENGTH = 22
        const val SECONDARY_METRIC_STROKE_RATIO = 23
        const val SECONDARY_METRIC_SPEED = 24
        const val SECONDARY_METRIC_RATE = 25
        const val SECONDARY_METRIC_PACE = 26
        const val SECONDARY_METRIC_POWER = 27
        const val SECONDARY_METRIC_HIGH = 27

        const val PRIMARY_METRIC_LEFT_LOW = 100
        const val PRIMARY_METRIC_LEFT_PACE_WITH_AVG = 100
        const val PRIMARY_METRIC_LEFT_PACE_WITH_PEAK = 101
        const val PRIMARY_METRIC_LEFT_POWER_WITH_AVG = 102
        const val PRIMARY_METRIC_LEFT_POWER_WITH_PEAK = 103
        const val PRIMARY_METRIC_LEFT_HIGH = 103

        const val PRIMARY_METRIC_RIGHT_LOW = 200
        const val PRIMARY_METRIC_RIGHT_RATE_WITH_AVG = 200
        const val PRIMARY_METRIC_RIGHT_RATE_WITH_PEAK = 201
        const val PRIMARY_METRIC_RIGHT_CALORIES_WITH_AVG = 202
        const val PRIMARY_METRIC_RIGHT_CALORIES_WITH_PEAK = 203
        const val PRIMARY_METRIC_RIGHT_HIGH = 203
    }

    interface SomeKindOfMetric

    data class Secondary(val title: String, private val formatter: MetricFormatter, var value: Float?, private val default: String = "") : SomeKindOfMetric {
        val formattedValue: String
            get() = if (value != null) formatter.format(value!!) else default
    }

    data class Primary(val title: String, val subtitle: String, val formatter: MetricFormatter, val min: Float, val max: Float, var value: Float, var subvalue: Float) : SomeKindOfMetric
}