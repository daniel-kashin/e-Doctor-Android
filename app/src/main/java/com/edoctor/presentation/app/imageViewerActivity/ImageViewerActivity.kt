package com.edoctor.presentation.app.imageViewerActivity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.edoctor.R
import com.edoctor.utils.*
import com.github.chrisbanes.photoview.PhotoView

class ImageViewerActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_IMAGE_URL = "image_url"

        fun show(activity: Activity, imageUrl: String) {
            val intent = Intent(activity, ImageViewerActivity::class.java)
                .putExtra(EXTRA_IMAGE_URL, imageUrl)

            activity.startActivity(intent)
            (activity as? Activity)?.overridePendingTransition(0, 0)
        }
    }

    private lateinit var imageUrl: String

    private val imageView by lazyFind<PhotoView>(R.id.image)
    private val loader by lazyFind<ProgressBar>(R.id.image_loader)
    private val iconBack by lazyFind<View>(R.id.icon_back)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        iconBack.setOnClickListener {
            onBackPressed()
        }

        imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)

        showImage(imageUrl)
    }

    private fun showImage(imageUrl: String) {
        loader.show()

        GlideApp.with(imageView.context)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    showErrorView()
                    loader.hide()
                    toast(R.string.unhandled_error_message, Duration.LONG)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    loader.hide()
                    return false
                }
            })
            .apply(
                RequestOptions()
                    .fitCenter()
                    .dontAnimate()
            )
            .into(imageView)
    }

    private fun showErrorView() {
        imageView.setImageResource(R.drawable.ic_error_red)
    }

}