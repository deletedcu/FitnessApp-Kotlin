package com.liverowing.android.race.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.liverowing.android.R

class TextMetricView constructor(context: Context?, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    private var mTitle: TextView
    private var mValue: TextView

    var title: String?
        get() = mTitle.text.toString()
        set(value) {
            mTitle.text = value
        }

    var value: String?
        get() = mValue.text.toString()
        set(value) {
            mValue.text = value
        }

    init {
        val inflater = context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.view_text_metric, this, true)

        mTitle = getChildAt(0) as TextView
        mValue = getChildAt(1) as TextView

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TextMetricView, 0, 0)
        val gravity = a.getInt(R.styleable.TextMetricView_position, Gravity.START)
        if (a.hasValue(R.styleable.TextMetricView_title)) {
            title = a.getString(R.styleable.TextMetricView_title)
        }
        if (a.hasValue(R.styleable.TextMetricView_value)) {
            value = a.getString(R.styleable.TextMetricView_value)
        }
        a.recycle()

        /*
        val res = ResourcesCompat.getDrawable(resources, R.drawable.ic_more_horz_white, null)
        when (gravity) {
            Gravity.START -> mValue.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, res, null)
            Gravity.END -> mValue.setCompoundDrawablesRelativeWithIntrinsicBounds(res, null, null, null)
            Gravity.CENTER_HORIZONTAL -> mValue.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, res)
        }
        */

        mValue.gravity = Gravity.TOP or gravity
        mTitle.gravity = Gravity.BOTTOM or gravity
    }

}