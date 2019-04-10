package com.edoctor.presentation.app.doctor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.edoctor.R
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.mapper.UserMapper
import com.edoctor.presentation.app.chat.ChatActivity
import com.edoctor.utils.CheckedIntentBuilder
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.edoctor.utils.hide
import com.edoctor.utils.lazyFind
import com.edoctor.utils.session
import com.google.android.material.textfield.TextInputEditText

class DoctorActivity : AppCompatActivity() {

    companion object {
        const val DOCTOR_PARAM = "doctor"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)
    private val imageView by lazyFind<ImageView>(R.id.image_view)
    private val name by lazyFind<TextView>(R.id.name)
    private val specialization by lazyFind<TextView>(R.id.specialization)
    private val category by lazyFind<TextView>(R.id.category)
    private val openChat by lazyFind<Button>(R.id.open_chat)

    private val labelCareer by lazyFind<TextView>(R.id.label_career)
    private val clinicalInterests by lazyFind<TextInputEditText>(R.id.clinical_interests)
    private val education by lazyFind<TextInputEditText>(R.id.education)
    private val workExperience by lazyFind<TextInputEditText>(R.id.work_experience)
    private val trainings by lazyFind<TextInputEditText>(R.id.trainings)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        try {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        } catch (e: IllegalStateException) {
            // NOTE: https://issuetracker.google.com/issues/68454482
        }

        window.decorView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                window.decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val size = minOf(window.decorView.width, window.decorView.height)
                imageView.layoutParams = LinearLayout.LayoutParams(size, size)
            }
        })

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
            title = getString(R.string.doctor).capitalize()
        }

        clinicalInterests.isFocusable = false
        education.isFocusable = false
        workExperience.isFocusable = false
        trainings.isFocusable = false

        val doctor = intent?.getSerializableExtra(DOCTOR_PARAM) as DoctorModel
        showDoctorInfo(doctor)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("SetTextI18n")
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

        name.text = doctor.fullName ?: getString(R.string.name_not_set)

        if (doctor.specialization != null) {
            specialization.text = doctor.specialization
        } else {
            specialization.hide()
        }

        val categoryText = when (doctor.category) {
            0 -> getString(R.string.doctor_highest_category)
            1 -> getString(R.string.doctor_first_category)
            2 -> getString(R.string.doctor_second_category)
            else -> getString(R.string.doctor_no_category)
        }
        val yearsOfExperienceText = doctor.yearsOfExperience
            ?.let { ", ${getString(R.string.years_of_experince_param, it)}" }
            ?: ""
        category.text = categoryText + yearsOfExperienceText

        if (doctor.clinicalInterests == null && doctor.education == null &&
            doctor.workExperience == null && doctor.trainings == null
        ) {
            labelCareer.hide()
        }

        if (doctor.clinicalInterests != null) {
            clinicalInterests.setText(doctor.clinicalInterests)
        } else {
            clinicalInterests.hideParent()
        }

        if (doctor.education != null) {
            education.setText(doctor.education)
        } else {
            education.hideParent()
        }

        if (doctor.workExperience != null) {
            workExperience.setText(doctor.workExperience)
        } else {
            workExperience.hideParent()
        }

        if (doctor.trainings != null) {
            trainings.setText(doctor.trainings)
        } else {
            trainings.hideParent()
        }

        openChat.setOnClickListener {
            openChatWithDoctor(doctor)
        }
    }

    private fun openChatWithDoctor(doctor: DoctorModel) {
        session.runIfOpened { sessionInfo ->
            ChatActivity.IntentBuilder(this)
                .recipientUser(doctor)
                .currentUser(requireNotNull(UserMapper.unwrapResponse(sessionInfo.account)))
                .start()
        } ?: run {
            onSessionException()
        }
    }

    private fun AppCompatEditText.hideParent() {
        (parent.parent as? View)?.hide()
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