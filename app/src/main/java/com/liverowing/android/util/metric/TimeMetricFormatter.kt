package com.liverowing.android.util.metric

import com.liverowing.android.secondsToTimespan

class TimeMetricFormatter(private val milliSecondPrecision: Boolean) : MetricFormatter {
    override fun format(value: Float): String {
        return value.secondsToTimespan(milliSecondPrecision)
    }
}