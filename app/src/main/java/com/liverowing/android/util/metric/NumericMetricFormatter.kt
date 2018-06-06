package com.liverowing.android.util.metric

class NumericMetricFormatter(val format: String) : MetricFormatter {
    override fun format(value: Float): String {
        return format.format(value)
    }
}