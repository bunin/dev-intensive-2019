package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.DrawableRes
import ru.skillbranch.devintensive.R
import kotlin.math.min

open class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private val drawableRect = RectF()
    private val borderRect = RectF()

    private val shaderMatrix = Matrix()
    private val bitmapPaint = Paint()
    private val borderPaint = Paint()
    private val circleBackgroundPaint = Paint()

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    private var bitmap: Bitmap? = null
    private var bitmapShader: BitmapShader? = null
    private var bitmapWidth: Int = 0
    private var bitmapHeight: Int = 0

    private var drawableRadius: Float = 0.toFloat()
    private var borderRadius: Float = 0.toFloat()

    private var colorFilter: ColorFilter? = null

    private var ready: Boolean = false
    private var setupPending: Boolean = false
    private var borderOverlay: Boolean = false
    private var isDisableCircularTransformation: Boolean = false
        set(disableCircularTransformation) {
            if (isDisableCircularTransformation == disableCircularTransformation) {
                return
            }

            field = disableCircularTransformation
            initializeBitmap()
        }

    init {

        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)

        try {
            borderWidth = a.getDimensionPixelSize(
                R.styleable.CircleImageView_cv_borderWidth,
                DEFAULT_BORDER_WIDTH
            )
            borderColor =
                a.getColor(R.styleable.CircleImageView_cv_borderColor, DEFAULT_BORDER_COLOR)
        } finally {
            a.recycle()
        }

        init()
    }

    private fun init() {
        super.setScaleType(SCALE_TYPE)
        ready = true

        if (setupPending) {
            setup()
            setupPending = false
        }
    }

    override fun getScaleType(): ScaleType {
        return SCALE_TYPE
    }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != SCALE_TYPE) {
            throw IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType))
        }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) {
            throw IllegalArgumentException("adjustViewBounds not supported.")
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (isDisableCircularTransformation) {
            super.onDraw(canvas)
            return
        }

        if (bitmap == null) {
            return
        }

        canvas.drawCircle(
            drawableRect.centerX(),
            drawableRect.centerY(),
            drawableRadius,
            bitmapPaint
        )
        if (borderWidth > 0) {
            canvas.drawCircle(borderRect.centerX(), borderRect.centerY(), borderRadius, borderPaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    @Deprecated(
        "Use {@link #setBorderColor(int)} instead",
        ReplaceWith("borderColor = context.resources.getColor(borderColorRes)")
    )
    fun setBorderColorResource(@ColorRes borderColorRes: Int) {
        setBorderColor(borderColorRes)
    }

    override fun setImageBitmap(bm: Bitmap) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf === colorFilter) {
            return
        }

        colorFilter = cf
        applyColorFilter()
        invalidate()
    }

    override fun getColorFilter(): ColorFilter? {
        return colorFilter
    }

    private fun applyColorFilter() {
        bitmapPaint.colorFilter = colorFilter
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        return try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG)
            } else {
                val w =
                    if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else drawable.bounds.width()
                val h =
                    if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else drawable.bounds.height()
                Bitmap.createBitmap(w, h, BITMAP_CONFIG)
            }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }

    private fun initializeBitmap() {
        bitmap = if (isDisableCircularTransformation) {
            null
        } else {
            getBitmapFromDrawable(drawable)
        }
        setup()
    }

    private fun setup() {
        if (!ready) {
            setupPending = true
            return
        }

        if (width == 0 && height == 0) {
            return
        }

        if (bitmap == null) {
            invalidate()
            return
        }

        bitmapShader = BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        bitmapPaint.isAntiAlias = true
        bitmapPaint.shader = bitmapShader

        borderPaint.style = Paint.Style.STROKE
        borderPaint.isAntiAlias = true
        borderPaint.color = borderColor
        borderPaint.strokeWidth = borderWidth.toFloat()

        circleBackgroundPaint.style = Paint.Style.FILL
        circleBackgroundPaint.isAntiAlias = true

        bitmapHeight = bitmap!!.height
        bitmapWidth = bitmap!!.width

        borderRect.set(calculateBounds())
        borderRadius =
            min(
                (borderRect.height() - borderWidth) / 2.0f,
                (borderRect.width() - borderWidth) / 2.0f
            )

        drawableRect.set(borderRect)
        if (!borderOverlay && borderWidth > 0) {
            drawableRect.inset(borderWidth - 1.0f, borderWidth - 1.0f)
        }
        drawableRadius = min(drawableRect.height() / 2.0f, drawableRect.width() / 2.0f)

        applyColorFilter()
        updateShaderMatrix()
        invalidate()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom

        val sideLength = min(availableWidth, availableHeight)

        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f

        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f

        shaderMatrix.set(null)

        if (bitmapWidth * drawableRect.height() > drawableRect.width() * bitmapHeight) {
            scale = drawableRect.height() / bitmapHeight.toFloat()
            dx = (drawableRect.width() - bitmapWidth * scale) * 0.5f
        } else {
            scale = drawableRect.width() / bitmapWidth.toFloat()
            dy = (drawableRect.height() - bitmapHeight * scale) * 0.5f
        }

        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(
            (dx + 0.5f).toInt() + drawableRect.left,
            (dy + 0.5f).toInt() + drawableRect.top
        )

        bitmapShader!!.setLocalMatrix(shaderMatrix)
    }

    fun getBorderWidth(): Int {
        return borderWidth
    }

    fun setBorderWidth(@Dimension dp: Int) {
        if (dp == borderWidth) {
            return
        }
        borderWidth = dp
        setup()
    }

    fun getBorderColor(): Int {
        return borderColor
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        val c = context.resources.getColor(colorId, context.theme)
        if (borderColor == c) {
            return
        }
        borderColor = c
        borderPaint.color = borderColor
        invalidate()
    }

    fun setBorderColor(hex: String) {
        val c = Color.parseColor(hex)
        if (c == borderColor) {
            return
        }
        borderColor = c
        borderPaint.color = borderColor
        invalidate()
    }

    companion object {

        private val SCALE_TYPE = ScaleType.CENTER_CROP

        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private const val COLORDRAWABLE_DIMENSION = 2

        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }

}