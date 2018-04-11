package com.liverowing.liverowing.util.metric

import com.liverowing.liverowing.secondsToTimespan

class TimeMetricFormatter(private val milliSecondPrecision: Boolean) : MetricFormatter {
    override fun format(value: Float): String {
        return value.secondsToTimespan(milliSecondPrecision)
    }
}