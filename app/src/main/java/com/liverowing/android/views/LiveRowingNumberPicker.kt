package com.liverowing.android.views

import android.widget.NumberPicker
import android.os.Build
import android.annotation.TargetApi
import android.content.Context
import android.util.AttributeSet

@TargetApi(Build.VERSION_CODES.HONEYCOMB)//For backward-compability
class LiveRowingNumberPicker : NumberPicker {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        processAttributeSet(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        processAttributeSet(attrs)
    }

    private fun processAttributeSet(attrs: AttributeSet) {
        //This method reads the parameters given in the xml file and sets the properties according to it
        this.minValue = attrs.getAttributeIntValue(null, "min", 0)
        this.maxValue = attrs.getAttributeIntValue(null, "max", 0)
        this.value = attrs.getAttributeIntValue(null, "val", 0)
    }
}