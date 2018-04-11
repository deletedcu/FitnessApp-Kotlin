package com.liverowing.liverowing.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.liverowing.liverowing.dpToPx
import android.animation.ValueAnimator
import android.support.v4.content.res.ResourcesCompat
import android.view.animation.AccelerateDecelerateInterpolator
import com.liverowing.liverowing.R
import kotlin.math.PI


class SplitIntervalOverviewView : View {
    private val progressBars = arrayListOf<SplitInterval>()

    companion object {
        const val MULTIPLIER = PI
        val MARGIN = 2f.dpToPx()
    }

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private lateinit var racedPaint: Paint
    private lateinit var notRacedPaint: Paint
    private lateinit var currentPaint: Paint
    private fun init(attrs: AttributeSet?, defStyle: Int) {
        racedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, R.color.overview_raced, null)
        }

        notRacedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, R.color.overview_background, null)
        }

        currentPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, R.color.overview_not_raced, null)
        }

    }

    fun reset() {
        progressBars.clear()
        invalidate()
    }

    fun addDistanceWithSetSplitSize(distance: Int, splitSize: Int) {
        val splitDistance = distance / splitSize
        for (split in 1..splitSize) {
            progressBars.add(SplitInterval(SplitIntervalType.SplitIntervalDistance, splitDistance, 0f))
        }
        invalidate()
    }

    fun addTimeWithSetSplitSize(time: Int, splitSize: Int) {
        val splitTime = time / splitSize
        for (split in 1..splitSize) {
            progressBars.add(SplitInterval(SplitIntervalType.SplitIntervalTime, (splitTime * MULTIPLIER).toInt(), 0f))
        }
        invalidate()
    }

    fun addInterval(type: SplitIntervalType, value: Int) {
        progressBars.add(SplitInterval(type, if (type == SplitIntervalType.SplitIntervalTime) (value * MULTIPLIER).toInt() else value))
        invalidate()
    }

    private var mCurrentIndex = 0
    fun setProgress(num: Int, progress: Float, animate: Boolean = false) {
        if (num > -1 && num < progressBars.size) {
            mCurrentIndex = num
            progressBars.subList(0, num).forEach({ it.progress = it.value.toFloat() })
            val bar = progressBars[num]

            if (animate) {
                val progress1: Float = if (bar.type == SplitIntervalType.SplitIntervalTime) progress * MULTIPLIER.toFloat() else progress

                ValueAnimator.ofFloat(bar.progress, progress1).apply {
                    duration = 500
                    interpolator = AccelerateDecelerateInterpolator()
                    addUpdateListener {  animation ->
                        val interpolation = animation.animatedValue as Float
                        setProgress(num, interpolation,false)
                    }
                }.start()
            } else {
                bar.progress = progress
                invalidate()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var offset = 0f
        val total = progressBars.sumBy { it.value }
        val width = canvas.width - ((progressBars.size - 1) * MARGIN)
        val ratio = width.toFloat() / total

        progressBars.forEachIndexed { index, splitInterval ->
            val w = splitInterval.value * ratio
            val paint = if (index == mCurrentIndex) currentPaint else notRacedPaint
            canvas.drawRect(offset, 0f, offset+w, canvas.height.toFloat(), paint)

            if (splitInterval.progress > 0) {
                val w2 = splitInterval.progress * ratio
                canvas.drawRect(offset, 0f, offset+w2, canvas.height.toFloat(), racedPaint)
            }

            offset += w + MARGIN
        }
    }

    data class SplitInterval(val type: SplitIntervalType, val value: Int, var progress: Float = 0f)

    enum class SplitIntervalType {
        SplitIntervalTime,
        SplitIntervalDistance
    }
}
