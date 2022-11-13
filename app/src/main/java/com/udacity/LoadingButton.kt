package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var loadingColor = 0
    private var circleColor = 0
    private var textColor = 0
    private var rectangleColor = 0

    private var buttonTextWidth = 0.0f
    private val buttonTextSize = resources.getDimension(R.dimen.default_text_size)
    private var buttonText = Constants.DOWNLOAD_TEXT

    private var diametr = buttonTextSize * 1.3f
    private var progress = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = buttonTextSize
    }

    /*private val circle by lazy {
        RectF(
            widthSize.toFloat() / 2 + buttonTextWidth / 2,
            heightSize.toFloat() / 2 - diametr / 2,
            widthSize.toFloat() / 2 + buttonTextWidth / 2 + diametr,
            heightSize.toFloat() / 2 + diametr / 2
        )
    }*/

    private var valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> startLoadingAnimation()
            ButtonState.Completed -> stopLoadingAnimation()
        }
    }

    private fun stopLoadingAnimation() {
        buttonText = Constants.DOWNLOAD_TEXT
        progress=0f
        valueAnimator.cancel()
        invalidate()
    }

    private fun startLoadingAnimation() {
        buttonText = Constants.LOADING_TEXT
        valueAnimator.start()
    }

    init {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 4000
        valueAnimator.repeatCount=ValueAnimator.INFINITE
        valueAnimator.addUpdateListener {
            progress = it.animatedValue as Float
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                progress = 0f
            }
        })

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, Color.DKGRAY)
            rectangleColor = getColor(R.styleable.LoadingButton_rectangleColor, Color.BLUE )
            circleColor = getColor(R.styleable.LoadingButton_circleColor, Color.YELLOW)
            textColor = getColor(R.styleable.LoadingButton_textColor,Color.WHITE)
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // rect
        paint.color = rectangleColor
        canvas?.drawRect(0f,0f,widthSize.toFloat(),heightSize.toFloat(),paint)
        //background
        paint.color = loadingColor
        canvas?.drawRect(0f, 0f, progress * widthSize.toFloat(), heightSize.toFloat(), paint)
        // text
        paint.color = textColor
        buttonTextWidth = paint.measureText(buttonText)
        canvas?.drawText(buttonText,widthSize.toFloat() / 2, heightSize.toFloat() / 2 - (paint.descent() + paint.ascent()) / 2, paint)
        // circle
        paint.color = circleColor
        canvas?.drawArc( RectF(
            widthSize.toFloat() / 2 + buttonTextWidth / 2,
            heightSize.toFloat() / 2 - diametr / 2,
            widthSize.toFloat() / 2 + buttonTextWidth / 2 + diametr,
            heightSize.toFloat() / 2 + diametr / 2
        ), 0F, progress * 360f, true, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}