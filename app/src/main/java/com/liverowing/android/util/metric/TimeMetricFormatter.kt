package com.liverowing.android.util.metric

import com.liverowing.android.extensions.secondsToTimespan

class TimeMetricFormatter(private val milliSecondPrecision: Boolean) : MetricFormatter {
    override fun format(value: Float): String {
        return value.secondsToTimespan(milliSecondPrecision)
    }
}