package com.liverowing.android.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.SpannableString
import android.text.style.ReplacementSpan
import android.util.AttributeSet
import android.widget.TextView

class HighlightFirstWordTextView: TextView {
    private var mDelayedSetter: Runnable? = null
    private var mConstructorCallDone: Boolean = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mConstructorCallDone = true
        isAllCaps = true
    }

    override fun setText(text: CharSequence, type: TextView.BufferType) {
        if (!mConstructorCallDone) {
            super.setText(text, type)

            // Postponing setting text via XML until the constructor has finished calling
            mDelayedSetter = Runnable { this@HighlightFirstWordTextView.setText(text, type) }
            post(mDelayedSetter)
        } else {
            removeCallbacks(mDelayedSetter)
            val s = getCustomSpannableString(text)
            super.setText(s, TextView.BufferType.SPANNABLE)
        }
    }

    private fun getCustomSpannableString(text: CharSequence?): CharSequence? {
        val span = SpannableString(text)
        val spacePosition = span.indexOf(' ')

        if (spacePosition > 0) {
            span.setSpan(TagSpan(Color.WHITE, Color.BLACK), 0, spacePosition, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
            span.setSpan(TagSpan(Color.TRANSPARENT, Color.WHITE), spacePosition+1, text!!.length, SpannableString.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        return span
    }

    inner class TagSpan(private val mBackgroundColor: Int, private val mForegroundColor: Int) : ReplacementSpan() {
        private val mRect: RectF = RectF()

        override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
            // Background
            mRect.set(x, top.toFloat(), x + paint.measureText(text, start, end) + 50.0f, bottom.toFloat())
            paint.color = mBackgroundColor
            canvas.drawRect(mRect, paint)

            // Text
            paint.color = mForegroundColor
            val xPos = Math.round(x + 50.0f / 2)
            val yPos = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2).toInt()
            canvas.drawText(text, start, end, xPos.toFloat(), yPos.toFloat(), paint)
        }

        override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
            return Math.round(paint.measureText(text, start, end) + 50.0f)
        }

    }
}