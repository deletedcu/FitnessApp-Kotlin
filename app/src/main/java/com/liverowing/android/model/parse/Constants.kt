package com.liverowing.android.model.parse

/**
 * Created by henrikmalmberg on 2017-11-10.
 */
enum class SegmentValueType (val value: Int) {
    TIMED(1),
    DISTANCE(2);

    companion object {
        private val map = SegmentValueType.values().associateBy(SegmentValueType::value)
        fun fromInt(type: Int) = map[type]!!
    }
}