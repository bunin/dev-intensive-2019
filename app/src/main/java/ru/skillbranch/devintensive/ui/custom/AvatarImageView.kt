package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.core.graphics.toRectF

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CircleImageView(context, attrs, defStyleAttr) {

    private val initialsDrawable = InitialsDrawable()
    private var initials = "??"

    override fun onDraw(canvas: Canvas) {
        if (drawable != null) {
            super.onDraw(canvas)
        } else {
            setInitials(initials, canvas)
            super.onDraw(canvas)
        }
    }

    fun setInitials(s: String, c: Canvas? = null) {
        initials = s
        initialsDrawable.setText(initials)
        if (c != null) initialsDrawable.setBouds(c.clipBounds.toRectF())
        setImageDrawable(initialsDrawable)
    }

}