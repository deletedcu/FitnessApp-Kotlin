package com.liverowing.android.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.graphics.Paint.Align
import android.graphics.Typeface
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.provider.FontRequest
import android.support.v4.provider.FontsContractCompat
import android.view.animation.AccelerateDecelerateInterpolator
import com.liverowing.android.R
import com.liverowing.android.R.array.com_google_android_gms_fonts_certs
import com.liverowing.android.util.metric.Metric
import com.liverowing.android.util.metric.MetricFormatter
import com.liverowing.android.util.metric.NumericMetricFormatter

class GaugeView : View {
    companion object {
        const val TOP = 0.0f
        const val LEFT = 0.0f
        const val RIGHT = 1.0f
        const val BOTTOM = 1.0f
        const val CENTER = 0.5f
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

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        initFonts()
        initPaint()
        initDrawingRects()
        initScale()
    }

    var metricId: Int = Metric.PRIMARY_METRIC_LEFT_LOW

    private lateinit var mHandler: Handler
    private var mTypeface = Typeface.SANS_SERIF
    private fun initFonts() {
        val handlerThread = HandlerThread("fonts")
        handlerThread.start()
        mHandler = Handler(handlerThread.looper)

        val request = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Lato",
                com_google_android_gms_fonts_certs
        )

        val callback = object : FontsContractCompat.FontRequestCallback() {
            override fun onTypefaceRetrieved(typeface: Typeface?) {
                mTypeface = typeface
                invalidate()
            }

            override fun onTypefaceRequestFailed(reason: Int) {
                Log.d("LiveRowing", "Failed to fetch font: $reason")
            }
        }

        FontsContractCompat.requestFont(context, request, callback, mHandler)
    }

    private lateinit var mTextValuePaint: Paint
    private lateinit var mTitlePaint: Paint
    private lateinit var mSubtitlePaint: Paint
    private lateinit var mBackgroundPaint: Paint
    private lateinit var mTickPaint: Paint
    private lateinit var mProgressPaint: Paint
    private lateinit var mSecondaryProgressPaint: Paint
    private fun initPaint() {
        mTextValuePaint = Paint(Paint.LINEAR_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#ffffff")
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 0.005f
            textSize = 0.18f
            textAlign = Align.CENTER
            typeface = mTypeface
            //setShadowLayer(100f, 0f, 0f, Color.parseColor("#51bdcb"))
        }

        mTitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#fafafa")
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 0.003f
            textSize = 0.07f
            textAlign = Align.CENTER
            typeface = mTypeface
            //setShadowLayer(0.01f, 0.002f, 0.002f, Color.argb(100, 0, 0, 0))
        }
        //setLayerType(View.LAYER_TYPE_SOFTWARE, mTitlePaint)

        mSubtitlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#474849")
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 0.003f
            textSize = 0.05f
            textAlign = Align.CENTER
            typeface = mTypeface
            //setShadowLayer(0.01f, 0.002f, 0.002f, Color.argb(100, 0, 0, 0))
        }
        //setLayerType(View.LAYER_TYPE_SOFTWARE, mTitlePaint)

        mTickPaint = Paint(Paint.LINEAR_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.DKGRAY
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth = 0.005f
            textSize = 0.05f
            typeface = mTypeface
            textAlign = Align.CENTER
        }

        mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 0.045f
        }

        mSecondaryProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ResourcesCompat.getColor(resources, R.color.gauge_secondary, null)
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 0.035f
        }

        mBackgroundPaint = Paint().apply {
            isFilterBitmap = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val chosenWidth = chooseDimension(widthMode, widthSize)
        val chosenHeight = chooseDimension(heightMode, heightSize)
        setMeasuredDimension(chosenWidth, chosenHeight)
    }

    private fun chooseDimension(mode: Int, size: Int): Int {
        return when (mode) {
            View.MeasureSpec.AT_MOST, View.MeasureSpec.EXACTLY -> size
            else -> 300
        }
    }

    private fun initDrawingRects() {
        mScaleRect = RectF(LEFT + mScalePosition, TOP + mScalePosition, RIGHT - mScalePosition, BOTTOM - mScalePosition)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val scale = Math.min(width, height).toFloat()
        canvas.scale(scale, scale)
        canvas.translate(if (scale == height.toFloat()) (width - scale) / 2 / scale else 0f, if (scale == width.toFloat()) (height - scale) / 2 / scale else 0f)

        drawScale(canvas)
        drawText(canvas)
    }

    fun setValue(value: Float, subvalue: Float, animated: Boolean) {
        initScale()
        if (animated) {
            val from1 = mCurrentValue
            val from2 = mCurrentSubValue
            ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 200
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { animation ->
                    val newValue = from1 + (value - from1) * animation.animatedValue as Float
                    val newSubValue = from2 + (subvalue - from2) * animation.animatedValue as Float

                    setValue(newValue, newSubValue, false)
                }
            }.start()
        } else {
            mCurrentValue = value
            mCurrentSubValue = subvalue
            invalidate()
        }
    }

    private fun getValueColor(value: Float): Int {
        return if (scaleStartValue > scaleEndValue) {
            when {
                target == null -> ResourcesCompat.getColor(resources, R.color.gauge_neutral, null)
                value < target!! - tolerance -> ResourcesCompat.getColor(resources, R.color.gauge_below_target, null)
                value > target!! + tolerance -> ResourcesCompat.getColor(resources, R.color.gauge_over_target, null)
                else -> ResourcesCompat.getColor(resources, R.color.gauge_at_target, null)
            }
        } else {
            when {
                target == null -> ResourcesCompat.getColor(resources, R.color.gauge_neutral, null)
                value > target!! - tolerance -> ResourcesCompat.getColor(resources, R.color.gauge_below_target, null)
                value < target!! + tolerance -> ResourcesCompat.getColor(resources, R.color.gauge_over_target, null)
                else -> ResourcesCompat.getColor(resources, R.color.gauge_at_target, null)
            }
        }
    }

    private lateinit var mScaleRect: RectF
    private val mScalePosition = 0.015f
    private var mScaleRotation = 0f
    private val mScaleStartAngle = 60.0f
    private var mDivisionValue = 0f
    private var mSubdivisionValue = 0f
    private var mCurrentValue = 0f
    private var mCurrentSubValue = 0f
    private var mSubdivisionAngle = 0.0f

    var scaleStartValue = 180f
    var scaleEndValue = 0f
    var divisions = 8
    var subdivisions = 15
    var target: Float? = null
    var tolerance = 3f

    fun initScale() {
        mScaleRotation = (mScaleStartAngle + 180) % 360
        mDivisionValue = (scaleEndValue - scaleStartValue) / divisions;
        mSubdivisionValue = mDivisionValue / subdivisions;
        mSubdivisionAngle = (360 - 2 * mScaleStartAngle) / (divisions * subdivisions)
    }

    private fun getValueForTick(tick: Int): Float {
        return scaleStartValue + (tick * (mDivisionValue / subdivisions))
    }

    private fun getAngleForValue(value: Float): Float {
        return if (scaleStartValue < scaleEndValue) {
            when {
                value <= scaleStartValue -> 0f
                value >= scaleEndValue -> 240f
                else -> (240 / (scaleEndValue - scaleStartValue)) * (value - scaleStartValue)
            }
        } else {
            when {
                value >= scaleStartValue -> 0f
                value <= scaleEndValue -> 240f
                else -> (240 / (scaleEndValue - scaleStartValue)) * (value - scaleStartValue)
            }
        }
    }

    private fun drawScale(canvas: Canvas) {
        canvas.save()
        // On canvas, North is 0 degrees, East is 90 degrees, South is 180 etc.
        // We start the scale somewhere South-West so we need to first rotate the canvas.
        canvas.rotate(mScaleRotation, 0.5f, 0.5f)

        val totalTicks = divisions * subdivisions + 1
        for (i in 0 until totalTicks) {
            val y1 = mScaleRect.top
            val y2 = y1 + 0.045f // height of division

            canvas.drawLine(0.5f, y1, 0.5f, y2, mTickPaint)
            canvas.rotate(mSubdivisionAngle, 0.5f, 0.5f)
        }
        canvas.restore()

        canvas.save()
        canvas.rotate(150f, 0.5f, 0.5f)
        val startAngle = 0f
        mProgressPaint.color = getValueColor(mCurrentValue)

        canvas.drawArc(0.038f, 0.038f, 1f - 0.038f, 1f - 0.038f, startAngle, getAngleForValue(mCurrentValue), false, mProgressPaint)
        canvas.drawArc(0.090f, 0.090f, 1f - 0.090f, 1f - 0.090f, startAngle, getAngleForValue(mCurrentSubValue), false, mSecondaryProgressPaint)

        canvas.restore()
    }

    private fun drawTextOnCanvasWithMagnifier(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
        val originalStrokeWidth = paint.strokeWidth
        val originalTextSize = paint.textSize
        val magnifier = 1000f

        canvas.save()
        canvas.scale(1f / magnifier, 1f / magnifier)

        paint.textSize = originalTextSize * magnifier
        paint.strokeWidth = originalStrokeWidth * magnifier

        canvas.drawText(text, x * magnifier, y * magnifier, paint)
        canvas.restore()

        paint.textSize = originalTextSize
        paint.strokeWidth = originalStrokeWidth

    }

    var title: String = ""
        set(value) {
            field = value.toUpperCase()
        }
    var subtitle: String = ""
        set(value) {
            field = value.toUpperCase()
        }
    var formatter: MetricFormatter = NumericMetricFormatter("%.0f")
    private fun drawText(canvas: Canvas) {
        val startX = CENTER
        val startY = CENTER + 0.070f

        val value = formatter.format(mCurrentValue)
        drawTextOnCanvasWithMagnifier(canvas, value, startX, startY, mTextValuePaint)
        drawTextOnCanvasWithMagnifier(canvas, title, startX, startY + 0.15f, mTitlePaint)
        drawTextOnCanvasWithMagnifier(canvas, subtitle, startX, startY + 0.22f, mSubtitlePaint)
    }
}