package com.liverowing.liverowing.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.v4.content.res.ResourcesCompat
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import com.liverowing.liverowing.R

class MultiRaceProgressView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RaceProgressView(context, attrs, defStyle) {
    var image: Bitmap? = null
    private var mBackgroundPaint: Paint
    private val mPersonalBestFlagPath = Path()
    private var mPersonalBestFlagPaint: Paint
    private val mPersonalBestRect = RectF()
    private var mSecondaryBackgroundPaint: Paint
    private var mPersonalBestTextPaint: TextPaint

    init {
        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ResourcesCompat.getColor(resources, R.color.progress_background, null)
            style = Paint.Style.FILL_AND_STROKE
        }

        mSecondaryBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ResourcesCompat.getColor(resources, R.color.progress_secondary_background, null)
            style = Paint.Style.FILL_AND_STROKE
        }

        mPersonalBestFlagPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 0f
            style = Paint.Style.FILL_AND_STROKE
            color = ResourcesCompat.getColor(resources, R.color.progress_personal_best_flag, null)
        }

        mPersonalBestTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG).apply {
            style = Paint.Style.FILL
            color = ResourcesCompat.getColor(resources, R.color.progress_personal_best_flag, null)
            textAlign = Paint.Align.RIGHT
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 26f
            setShadowLayer(1f, 0f, 0f, ResourcesCompat.getColor(resources, R.color.progress_text_shadow, null))
        }
    }

    var mAnimator: ValueAnimator? = null
    var hasPersonalBest = false
    private var mPersonalBestProgress = 0f
    fun setProgress(progress: Float, personalBestProgress: Float, animated: Boolean = false) {
        if (animated) {
            val from1 = mProgress
            val from2 = mPersonalBestProgress
            if (mAnimator is ValueAnimator && mAnimator!!.isRunning) {
                Log.d("LiveRowing", "Stopping animation: $mProgress")
                mAnimator?.cancel()
            }
            mAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = if (progress < mProgress) 100 else 450
                interpolator = AccelerateDecelerateInterpolator()
                addUpdateListener { animation ->
                    val newProgress = from1 + (progress - from1) * animation.animatedValue as Float
                    val newPersonalBestProgress = from2 + (personalBestProgress - from2) * animation.animatedValue as Float
                    setProgress(newProgress, newPersonalBestProgress, false)
                }
            }
            mAnimator?.start()
        } else {
            mProgress = progress
            mPersonalBestProgress = personalBestProgress
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), mBackgroundPaint)

        mProgressRect.set(0f, 0f, mOffset + (mProgress * (width - mOffset)), height.toFloat())
        canvas.drawRect(mProgressRect, mProgressPaint)

        if (hasPersonalBest) {
            mPersonalBestRect.set(0f, 0f, mOffset + (mPersonalBestProgress * (width - mOffset)), height.toFloat())
            canvas.drawRect(minOf(mProgressRect.right, mPersonalBestRect.right), 0f, maxOf(mProgressRect.right, mPersonalBestRect.right), height.toFloat(), mSecondaryBackgroundPaint)
            canvas.drawPath(getFlagPath(mPersonalBestFlagPath, mPersonalBestRect), mPersonalBestFlagPaint)
            canvas.drawText("PB", mOffset + ((canvas.width - mOffset) * mPersonalBestProgress) - 20, canvas.height.toFloat() - 12, mPersonalBestTextPaint)
        }

        if (image != null) {
            canvas.drawBitmap(image, mProgressRect.left + 6, 6f, mProgressPaint)
        }
        canvas.drawPath(getFlagPath(mFlagPath, mProgressRect), mFlagPaint)

    }
}