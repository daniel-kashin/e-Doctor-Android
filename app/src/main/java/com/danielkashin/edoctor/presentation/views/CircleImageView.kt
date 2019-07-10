package com.danielkashin.edoctor.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.danielkashin.edoctor.R
import com.danielkashin.edoctor.utils.lazyFind
import com.danielkashin.edoctor.utils.setColor


class CircleImageView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    private val circleView by lazyFind<FrameLayout>(R.id.circle_view)
    private val imageView by lazyFind<ImageView>(R.id.image_view)

    init {
        inflate(context, R.layout.view_circle_image, this)
    }

    fun bind(
        @ColorRes circleColorResId: Int,
        @ColorRes imageColorResId: Int,
        @DrawableRes imageDrawableResId: Int
    ) {
        circleView.background.setColor(context, circleColorResId)

        VectorDrawableCompat.create(resources, imageDrawableResId, null)?.run {
            val wrapped = DrawableCompat.wrap(this)
            DrawableCompat.setTint(wrapped, ResourcesCompat.getColor(resources, imageColorResId, null))
            imageView.setImageDrawable(wrapped)
        } ?: run {
            imageView.setImageResource(imageDrawableResId)
        }
    }

}