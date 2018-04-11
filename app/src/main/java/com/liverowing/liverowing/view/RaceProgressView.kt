package com.liverowing.liverowing.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.graphics.RectF
import android.support.v4.content.res.ResourcesCompat
import android.text.TextPaint
import com.liverowing.liverowing.R
import com.liverowing.liverowing.dpToPx

open class RaceProgressView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : View(context, attrs, defStyle) {
    protected val mOffset = 60.dpToPx()
    protected var mProgress = 0.0f
    protected val mProgressRect = RectF()
    protected val mFlagPath = Path()
    protected var mProgressPaint: Paint
    protected var mFlagPaint: Paint
    private lateinit var mTextPaint: TextPaint

    var name: String? = null
        set(value) { field = value?.toUpperCase() }
    open var flagColor: Int? = null
        set(value) { mFlagPaint.color = value!!; invalidate() }

    init {
        mFlagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 0f
            style = Paint.Style.FILL_AND_STROKE
        }

        mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, R.color.progress_raced_background, null)
        }

        mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG).apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, R.color.progress_text_color, null)
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            setShadowLayer(1f, 0f, 0f, ResourcesCompat.getColor(resources, R.color.progress_text_shadow, null))
        }

        mFlagPath.fillType = Path.FillType.EVEN_ODD
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (name != null) {
            adjustTextSize(name!!)
        }
    }

    private fun adjustTextSize(name: String) {
        mTextPaint.textSize = 100f
        mTextPaint.textScaleX = 1.0f
        val bounds = Rect()
        mTextPaint.getTextBounds(name, 0, name.length, bounds)

        val h = bounds.bottom - bounds.top
        val target = height * .6f
        val size = (target / h) * 100f

        mTextPaint.textSize = size
    }

    fun setProgress(progress: Float, animated: Boolean = false) {
        if (animated) {
            ValueAnimator.ofFloat(mProgress, progress).apply {
                duration = 450
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { animation ->
                    val interpolation = animation.animatedValue as Float
                    setProgress(interpolation, false)
                }
            }.start()
        } else {
            mProgress = progress
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mProgressRect.set(0f, 0f, mOffset + (mProgress * (width - mOffset)), height.toFloat())
        canvas.drawRect(mProgressRect, mProgressPaint)

        val path = getFlagPath(mFlagPath, mProgressRect)
        canvas.drawPath(path, mFlagPaint)

        if (name != null) {
            canvas.drawText(name, mOffset + ((canvas.width - mOffset) * mProgress) - mProgressRect.bottom + 6, canvas.height.toFloat() * .80f, mTextPaint)
        }
    }

    protected fun getFlagPath(path: Path, rect: RectF): Path {
        path.reset()
        path.moveTo(rect.right, rect.top)
        path.lineTo(rect.right, rect.bottom)
        path.lineTo(rect.right - 3.dpToPx(), rect.bottom)
        path.lineTo(rect.right - 3.dpToPx(), 15.dpToPx().toFloat())
        path.lineTo(rect.right - 15.dpToPx(), rect.top)
        path.close()

        return path
    }
}