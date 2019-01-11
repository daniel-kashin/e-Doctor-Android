package com.edoctor.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.*
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

val View.isVisible get() = visibility == View.VISIBLE

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.showOrInvisible(show: Boolean) {
    if (show) {
        show()
    } else {
        invisible()
    }
}

fun View.show(show: Boolean, animationDuration: Long? = null, animationStartDelay: Long? = null) {
    if (show && visibility == View.VISIBLE || !show && visibility == View.GONE)
        return

    if (animationDuration != null) {
        alpha = if (show) 0f else 1f
    }

    if (show) show() else hide()

    if (animationDuration != null) {
        animate()
            .alpha(if (show) 1f else 0f)
            .setDuration(animationDuration)
            .withStartAction { if (show) visibility = View.VISIBLE }
            .withEndAction { if (!show) visibility = View.GONE }
            .setStartDelay(animationStartDelay ?: 0)
            .start()
    }
}

fun View.setPadding(padding: Int) = apply {
    setPadding(padding, padding, padding, padding)
}

fun View.setPaddings(
    left: Int = paddingLeft,
    top: Int = paddingTop,
    right: Int = paddingRight,
    bottom: Int = paddingBottom
) = apply {
    setPadding(left, top, right, bottom)
}

inline fun View.setOnClickIf(condition: Boolean, crossinline action: (View) -> Unit) {
    setOnClickListener(if (condition) View.OnClickListener { action(this) } else null)
}

inline fun TextView.setTextIf(condition: Boolean, lazyMessage: TextView.() -> CharSequence?) {
    text = if (condition) lazyMessage() else null
}

inline fun TextView.setTextOrHide(condition: Boolean, lazyMessage: () -> CharSequence?) {
    show(condition)
    if (condition) text = lazyMessage()
}

var View.backgroundResource: Int
    get() = throw IllegalStateException("Property does not have a getter")
    set(value) = setBackgroundResource(value)

var ImageView.colorFilterRes: Int
    get() = throw IllegalStateException("Property does not have a getter")
    set(value) = setColorFilter(ContextCompat.getColor(context, value))

var TextView.textColorRes: Int
    get() = throw IllegalStateException("Property does not have a getter")
    set(value) = setTextColor(ContextCompat.getColor(context, value))

var TextView.hintTextColorRes: Int
    get() = throw IllegalStateException("Property does not have a getter")
    set(value) = setHintTextColor(ContextCompat.getColor(context, value))

fun View.setMargins(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        left?.let { this.leftMargin = it }
        top?.let { this.topMargin = it }
        right?.let { this.rightMargin = it }
        bottom?.let { this.bottomMargin = it }

        layoutParams = this
    }
}

fun TextView.underlineText() {
    paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

@Suppress("DEPRECATION")
fun TextView.forceExpandText(textToSet: String?) {
    if (textToSet == null) {
        text = null
        return
    }

    movementMethod = LinkMovementMethod.getInstance()
    text = SpannableStringBuilder(Html.fromHtml(textToSet))
}

fun measureTextHeightOf(textView: TextView, textSize: Float): Int {
    val paint = TextPaint().apply {
        set(textView.paint)
        setTextSize(textSize)
    }
    return StaticLayout(textView.text, paint, textView.width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true).height
}

fun EditText.moveCursorToTheEnd() = setSelection(text.length)

operator fun ViewGroup.plusAssign(view: View) {
    addView(view)
}

fun View.focusAndShowKeyboard() {
    if (requestFocus()) showKeyboard()
}

fun View.showKeyboard() {
    context.inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    context.inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

inline fun <R> ViewGroup.runIfEmpty(action: ViewGroup.() -> R): R? = if (childCount == 0) action(this) else null

fun ViewGroup.clearChildren() {
    if (childCount > 0) removeAllViews()
}

fun ImageView.applyColor(@ColorRes colorRes: Int) {
    background.apply {
        when {
            this is ShapeDrawable -> this.paint.color = ContextCompat.getColor(context, colorRes)
            this is GradientDrawable -> this.setColor(ContextCompat.getColor(context, colorRes))
            this is ColorDrawable -> this.color = ContextCompat.getColor(context, colorRes)
        }
    }
}

inline val Activity.contentView: ViewGroup? get() = findViewById(android.R.id.content)

inline fun <reified T : View> View.lazyFind(@IdRes id: Int) = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }
inline fun <reified T : View> Activity.lazyFind(@IdRes id: Int) = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }
inline fun <reified T : View> Fragment.lazyFind(@IdRes id: Int) = lazy(LazyThreadSafetyMode.NONE) { view?.findViewById<T>(id) }
inline fun <reified T : View> Dialog.lazyFind(@IdRes id: Int) = lazy(LazyThreadSafetyMode.NONE) { findViewById<T>(id) }

fun Activity.lazyBind(@ArrayRes id: Int) = lazy(LazyThreadSafetyMode.NONE) { resources.getStringArray(id) }

fun Activity.onClick(@IdRes id: Int, onClick: (View) -> Unit) = findViewById<View>(id)?.setOnClickListener(onClick)
fun View.onClick(@IdRes id: Int, onClick: (View) -> Unit) = findViewById<View>(id)?.setOnClickListener(onClick)

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Fragment> T.withArguments(vararg params: Pair<String, Any?>) = apply {
    arguments = bundleOf(*params)
}

inline val Context.displayMetrics: DisplayMetrics
    get() = resources.displayMetrics

inline val Context.configuration: Configuration
    get() = resources.configuration

inline val Configuration.isPortrait: Boolean
    get() = orientation == Configuration.ORIENTATION_PORTRAIT

inline val Configuration.isLandscape: Boolean
    get() = orientation == Configuration.ORIENTATION_LANDSCAPE

inline val Configuration.isLong: Boolean
    get() = (screenLayout and Configuration.SCREENLAYOUT_LONG_YES) != 0

inline fun <reified T : View> ViewGroup.children(): List<T> = (0 until childCount).mapNotNull { getChildAt(it) as? T }

inline fun <reified T : View> ViewGroup.forEachChild(action: (T) -> Unit) {
    for (i in 0 until childCount) {
        (getChildAt(i) as? T)?.let { action(it) }
    }
}

inline fun <reified T : View> ViewGroup.forEachChildIndexed(action: (Int, T) -> Unit) {
    for (i in 0 until childCount) {
        (getChildAt(i) as? T)?.let { action(i, it) }
    }
}

fun Window.setStatusBarColorRes(@ColorRes colorRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = ContextCompat.getColor(context, colorRes)
    }
}

fun AlertDialog.setPositiveButtonColor(@ColorInt color: Int) = apply {
    getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(color)
}

fun AlertDialog.setNegativeButtonColor(@ColorInt color: Int) = apply {
    getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(color)
}

fun AlertDialog.setPositiveButtonColorRes(@ColorRes color: Int) =
    setPositiveButtonColor(ContextCompat.getColor(context, color))

fun AlertDialog.setNegativeButtonColorRes(@ColorRes color: Int) =
    setNegativeButtonColor(ContextCompat.getColor(context, color))

class SimpleSeekBarListener(
    private val onProgressChanged: (s: SeekBar, p1: Int, p2: Boolean) -> Unit = { _, _, _ -> },
    private val onStartTrackingTouch: (s: SeekBar) -> Unit = {},
    private val onStopTrackingTouch: (s: SeekBar) -> Unit = {}
) : SeekBar.OnSeekBarChangeListener {

    override fun onProgressChanged(p0: SeekBar, p1: Int, p2: Boolean) = onProgressChanged.invoke(p0, p1, p2)
    override fun onStartTrackingTouch(p0: SeekBar) = onStartTrackingTouch.invoke(p0)
    override fun onStopTrackingTouch(p0: SeekBar) = onStopTrackingTouch.invoke(p0)
}

fun Context.asyncInflate(
    parentGroup: ViewGroup,
    @LayoutRes layoutId: Int,
    onInflateCompleted: () -> Unit
) {
    AsyncLayoutInflater(this).inflate(layoutId, parentGroup) { view, _, parent ->
        parent?.addView(view)
        onInflateCompleted()
    }
}

val View.screenLocation: Point
    get() {
        val locations = IntArray(2)
        getLocationOnScreen(locations)
        return Point(locations[0], locations[1])
    }