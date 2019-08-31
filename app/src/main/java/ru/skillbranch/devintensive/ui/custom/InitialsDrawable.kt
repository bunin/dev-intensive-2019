package ru.skillbranch.devintensive.ui.custom

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import androidx.core.graphics.toRect
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import kotlin.math.min

class InitialsDrawable(var bgColor: Int? = null) : Drawable() {

    private var text: String = ""
    private var textSizeFactor = 0.5f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()

    init {
//        textPaint.textSize = 48.px.toFloat()
        textPaint.color = Color.WHITE
        val s = App.applicationContext().resources.getDimension(R.dimen.avatar_round_size)
        rectF.set(0f, 0f, s, s)
        bounds = rectF.toRect()
    }


    override fun draw(canvas: Canvas) {
        canvas.getClipBounds(bounds)
        textPaint.textSize = min(bounds.height(), bounds.width()) * textSizeFactor
        val centerX = bounds.centerX()
        val centerY = bounds.centerY()
        val textWidth = textPaint.measureText(text) * 0.5f
        val textBaseLineHeight = textPaint.fontMetrics.ascent * -0.4f
        if (bgColor !== null) {
            paint.color = bgColor!!
        }
        canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), bounds.width() / 2f, paint)
        canvas.drawText(text, centerX - textWidth, centerY + textBaseLineHeight, textPaint)
    }

    override fun setAlpha(p0: Int) {
        throw UnsupportedOperationException()
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(p0: ColorFilter?) {
        throw UnsupportedOperationException()
    }

    fun setText(s: String) {
        text = s
//        invalidateSelf()
    }

    fun setBgColor(c: Int) {
        bgColor = c
//        invalidateSelf()
    }
}