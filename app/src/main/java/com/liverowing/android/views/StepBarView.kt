package com.liverowing.android.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.liverowing.android.R
import com.liverowing.android.util.DpHandler

open class StepBarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var stepsLinePaint: Paint

    var maxCount: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var currentStep: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var normalColor: Int = Color.GRAY
        set(value) {
            field = value
            invalidate()
        }

    var reachedColor: Int = Color.parseColor("#61c6d0")
        set(value) {
            field = value
            invalidate()
        }

    var currentColor: Int = Color.parseColor("#e4ce4b")
        set(value) {
            field = value
            invalidate()
        }

    var gap: Float = 2F
        set(value) {
            field = value
            invalidate()
        }

    var isRtl : Boolean = false
        set(value) {
            field = value
            invalidate()
        }

    private val lineSize: Float
        get() {
            val allMarginsSize = (maxCount - 1) * gap
            val width = width - allMarginsSize
            return width / maxCount
        }

    init {
        stepsLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = DpHandler.dpToPx(context, 5).toFloat()
        }
        maxCount = 4
        currentStep = 1
        gap = DpHandler.dpToPx(context, 2).toFloat()
        normalColor = ContextCompat.getColor(context, R.color.secondaryDarkColor)
        reachedColor = ContextCompat.getColor(context, R.color.hat_color3)
        currentColor = ContextCompat.getColor(context, R.color.hat_color11)
    }

    fun getPosition(i: Int) : Float {
        val linesSize = lineSize
        val position = when(isRtl) {
            true -> width - (maxCount - i + 1) * (linesSize + gap)
            false -> (i - 1) * (linesSize + gap)
        }
        return position
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val linesSize = lineSize

        for (step in 1..maxCount) {
            if (step < currentStep) {
                stepsLinePaint.color = reachedColor
            } else if (step == currentStep) {
                stepsLinePaint.color = currentColor
            } else {
                stepsLinePaint.color = normalColor
            }

            val startXPoint = getPosition(step)
            val endXPoint = when(isRtl) {
                true -> startXPoint - linesSize
                false -> startXPoint + linesSize
            }

            canvas?.drawLine(startXPoint, 0F, endXPoint, 0F, stepsLinePaint)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.maxCount = maxCount
        ss.currentStep = currentStep
        ss.gap = gap
        ss.normalColor = normalColor
        ss.reachedColor = reachedColor
        ss.currentColor = currentColor
        ss.isRtl = isRtl
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        maxCount = savedState.maxCount
        currentStep = savedState.currentStep
        gap = savedState.gap
        normalColor = savedState.normalColor
        reachedColor = savedState.reachedColor
        currentColor = savedState.currentColor
        isRtl = savedState.isRtl
    }

    class SavedState : BaseSavedState{
        companion object CREATOR : Parcelable.Creator<SavedState>{
            override fun createFromParcel(source: Parcel?) = SavedState(source)
            override fun newArray(size: Int) = arrayOfNulls<SavedState?>(size)
        }

        var maxCount: Int = 1
        var currentStep: Int = 1
        var gap: Float = 2F
        var normalColor: Int = 1
        var reachedColor: Int = 1
        var currentColor: Int = 1
        var isRtl : Boolean = false

        constructor(parcelable: Parcelable) : super(parcelable)
        constructor(parcel : Parcel?) : super(parcel){
            parcel?.let {
                maxCount = it.readInt()
                currentStep = it.readInt()
                gap = it.readFloat()
                normalColor = it.readInt()
                reachedColor = it.readInt()
                currentColor = it.readInt()
                isRtl = it.readInt()==1
            }

        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)

            out?.let {
                it.writeInt(maxCount)
                it.writeInt(currentStep)
                it.writeFloat(gap)
                it.writeInt(normalColor)
                it.writeInt(reachedColor)
                it.writeInt(currentColor)
                it.writeInt(if(isRtl) 1 else 0)
            }
        }
    }

}