package ru.skillbranch.devintensive.ui.custom

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import androidx.core.graphics.toRect
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R

object InitalsDrawable : Drawable() {

    private var bgColor: Int = -500033
    private var text: String = ""
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
    private val rectF = RectF()

    init {
        textPaint.textSize = 48f * App.applicationContext().resources.displayMetrics.scaledDensity
        textPaint.color = Color.WHITE
        val s = App.applicationContext().resources.getDimension(R.dimen.avatar_round_size)
        rectF.set(0f, 0f, s, s)
        bounds = rectF.toRect()
    }


    override fun draw(canvas: Canvas) {
        val centerX = canvas.clipBounds.centerX()
        val centerY = canvas.clipBounds.centerY()
        val textWidth = textPaint.measureText(text) * 0.5f
        val textBaseLineHeight = textPaint.fontMetrics.ascent * -0.4f
        paint.color = bgColor
        canvas.drawRect(rectF, paint)
        canvas.drawText(text, centerX - textWidth, centerY + textBaseLineHeight, textPaint)
    }

    override fun setAlpha(p0: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    override fun setColorFilter(p0: ColorFilter?) {
    }

    fun setText(s: String) {
        text = s
        invalidateSelf()
    }

    fun setBgColor(c: Int) {
        bgColor = c
        invalidateSelf()
    }
}