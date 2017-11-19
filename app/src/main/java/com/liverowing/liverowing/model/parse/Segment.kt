package com.liverowing.liverowing.model.parse

import com.liverowing.liverowing.secondsToTimespan
import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import khronos.second

/**
 * Created by henrikmalmberg on 2017-10-01.
 */
@ParseClassName("Segment")
class Segment : ParseObject() {
    var value by ParseDelegate<Int?>()
    var name by ParseDelegate<String?>()
    var restType by ParseDelegate<Int?>()
    var restValue by ParseDelegate<Int?>()
    var valueType by ParseDelegate<Int?>()
    var restDescription by ParseDelegate<String?>()
    var author by ParseDelegate<User?>()
    var image by ParseDelegate<ParseFile?>()
    var targetRate by ParseDelegate<Int?>()

    val friendlyValue: String
        get() {
            return if (valueType == SegmentValueType.TIMED.value) value!!.secondsToTimespan() else value.toString() + "m"
        }

    val friendlyRestValue: String
        get() {
            return restValue!!.secondsToTimespan() + "r"
        }
}
