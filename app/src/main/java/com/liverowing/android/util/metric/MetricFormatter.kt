package com.liverowing.android.util.metric

interface MetricFormatter {
    fun format(value: Float): String
}