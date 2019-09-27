package com.fabiosanto.infinite

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator

class LoadingBlock(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = context.getColor(R.color.light_gray)
    }
    private val paint2 = Paint().apply {
        color = context.getColor(R.color.white_alpha50)
    }

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var animatedX = 0f
    private var barSize: Int = 0
    private val accelerateInterpolator = AccelerateInterpolator()
    private val radius = 15f
    private val speed: Long = 1000

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), radius, radius, paint)

        canvas.drawRoundRect(
            animatedX, 0f, animatedX + barSize, height.toFloat(),
            radius,
            radius, paint2
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredWidth
        barSize = mWidth / 2

        ValueAnimator.ofFloat(-barSize.toFloat(), mWidth.toFloat()).apply {
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = speed
            interpolator = accelerateInterpolator
            addUpdateListener {
                animatedX = it.animatedValue as Float
                invalidate()
            }
        }.start()
    }
}