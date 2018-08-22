package com.liverowing.android.race.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.liverowing.android.R

class RatioView : View {
    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private lateinit var mTickPaint: Paint
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        mTickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            style = Paint.Style.FILL
        }
    }

    private var mRatio = 0f
    private val mTickCount = 20
    private var mTickHeight = 0f
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        mTickHeight = height / (mTickCount * 2f)
    }

    fun setStrokeRatio(ratio: Float) {
        mRatio = ratio
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in 1..20) {
            val value = i.times(5f).div(100f)
            mTickPaint.color = if (value > mRatio) ResourcesCompat.getColor(resources, R.color.stroke_ratio_recovery, null) else ResourcesCompat.getColor(resources, R.color.stroke_ratio_stroke, null)
            if (value in 0.30f..0.35f) {
                canvas.drawRect(width * 0.1f, i * mTickHeight * 2, width - (width * 0.1f), (i * mTickHeight * 2) + mTickHeight, mTickPaint)
            } else {
                canvas.drawRect(0f, i * mTickHeight * 2, width.toFloat(), (i * mTickHeight * 2) + mTickHeight, mTickPaint)
            }
        }
    }
}
