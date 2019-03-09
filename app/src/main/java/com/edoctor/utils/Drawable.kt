package com.edoctor.utils

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Drawable.setColor(context: Context, @ColorRes color: Int) {
    when {
        this is ShapeDrawable -> this.paint.color = ContextCompat.getColor(context, color)
        this is GradientDrawable -> this.setColor(ContextCompat.getColor(context, color))
        this is ColorDrawable -> this.color = ContextCompat.getColor(context, color)
    }
}