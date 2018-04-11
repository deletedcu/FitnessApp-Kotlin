package com.liverowing.liverowing.util.metric

interface MetricFormatter {
    fun format(value: Float): String
}