package com.edoctor.presentation.app.doctor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.utils.CheckedIntentBuilder
import com.edoctor.utils.lazyFind

class DoctorActivity : AppCompatActivity() {

    companion object {
        const val DOCTOR_PARAM = "doctor"
    }

    private val imageView by lazyFind<ImageView>(R.id.image_view)
    private val name by lazyFind<TextView>(R.id.name)
    private val specialization by lazyFind<TextView>(R.id.specialization)
    private val category by lazyFind<TextView>(R.id.category)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        window.decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val size = minOf(window.decorView.width, window.decorView.height)
                imageView.layoutParams = LinearLayout.LayoutParams(size, size)
            }
        })


        imageView.viewTreeObserver.addOnGlobalLayoutListener({

        })

        val doctor = intent?.getSerializableExtra(DOCTOR_PARAM) as DoctorModel
        showDoctorInfo(doctor)
    }

    private fun showDoctorInfo(doctor: DoctorModel) {
        Glide.with(this)
            .load(doctor.relativeImageUrl)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.lightLightGrey)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            )
            .into(imageView)

        name.text = doctor.fullName
        specialization.text = doctor.specialization
        category.text = when (doctor.category) {
            0 -> getString(R.string.doctor_highest_category)
            1 -> getString(R.string.doctor_first_category)
            2 -> getString(R.string.doctor_second_category)
            else -> getString(R.string.doctor_no_category)
        }
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var doctor: DoctorModel? = null

        fun doctor(doctor: DoctorModel) = apply { this.doctor = doctor }

        override fun areParamsValid() = doctor != null

        override fun get(): Intent =
            Intent(context, DoctorActivity::class.java)
                .putExtra(DoctorActivity.DOCTOR_PARAM, doctor)

    }

}