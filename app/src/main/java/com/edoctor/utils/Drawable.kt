package com.edoctor.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

fun Drawable.setColor(context: Context, @ColorRes color: Int) {
    when {
        this is ShapeDrawable -> this.paint.color = ContextCompat.getColor(context, color)
        this is GradientDrawable -> this.setColor(ContextCompat.getColor(context, color))
        this is ColorDrawable -> this.color = ContextCompat.getColor(context, color)
    }
}

fun ImageView.setDrawableWithTint(@DrawableRes drawableResId: Int, @ColorRes colorResId: Int) {
    VectorDrawableCompat.create(resources, drawableResId, null)?.run {
        val wrapped = DrawableCompat.wrap(mutate())
        DrawableCompat.setTint(wrapped, androidx.core.content.res.ResourcesCompat.getColor(resources, colorResId, null))
        setImageDrawable(wrapped)
    } ?: run {
        setImageResource(drawableResId)
    }
}