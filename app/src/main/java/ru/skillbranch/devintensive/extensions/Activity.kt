package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_main.*

fun Activity.hideKeyboard() {
    val inputManager: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.SHOW_FORCED)
}

fun Activity.isKeyboardOpen(): Boolean {
    return getDiff() > 100
}

fun Activity.isKeyboardClosed(): Boolean {
    return getDiff() <= 100
}

fun Activity.getDiff(): Float {
    val r2 = Rect()
    rootView.getWindowVisibleDisplayFrame(r2)

    return convertPixelsToDp((rootView.height - (r2.bottom - r2.top)).toFloat())
}

fun Activity.convertPixelsToDp(px: Float): Float {
    return px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}