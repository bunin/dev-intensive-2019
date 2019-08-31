package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.ImageView

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private val initialsDrawable = InitialsDrawable()
    private var initials = "??"

    override fun draw(canvas: Canvas?) {
        if (drawable != null) {
            super.draw(canvas)
        } else {
            setInitials(initials)
            super.draw(canvas)
        }
    }

    fun setInitials(s: String) {
        initials = s
        initialsDrawable.setText(initials)
        setImageDrawable(initialsDrawable)
    }

}