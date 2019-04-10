package com.edoctor.presentation.app.patient

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.mapper.UserMapper
import com.edoctor.presentation.app.chat.ChatActivity
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.onSessionException

class PatientActivity : AppCompatActivity() {

    companion object {
        const val PATIENT_PARAM = "patient"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)
    private val imageView by lazyFind<ImageView>(R.id.image_view)
    private val name by lazyFind<TextView>(R.id.name)
    private val cityAndYears by lazyFind<TextView>(R.id.city_and_years)
    private val bloodGroup by lazyFind<TextView>(R.id.blood_group)
    private val openChat by lazyFind<Button>(R.id.open_chat)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

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
            title = getString(R.string.patient).capitalize()
        }

        val patient = intent?.getSerializableExtra(PATIENT_PARAM) as PatientModel
        showPatientInfo(patient)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun showPatientInfo(patient: PatientModel) {
        Glide.with(this)
            .load(patient.relativeImageUrl)
            .apply(
                RequestOptions()
                    .centerCrop()
                    .placeholder(R.color.lightLightGrey)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            )
            .into(imageView)

        name.text = patient.fullName ?: getString(R.string.name_not_set)

        if (patient.city != null || patient.dateOfBirthTimestamp != null) {
            val ageString: String? = if (patient.dateOfBirthTimestamp != null) {
                val age = ((currentUnixTime() - patient.dateOfBirthTimestamp) / 3.15576e+7).toInt()
                getString(R.string.years_param, age)
            } else {
                null
            }

            val cityAndYearsText = when {
                patient.city != null && ageString != null -> "${patient.city}, $ageString"
                patient.city != null -> patient.city
                else -> ageString
            }
            cityAndYears.text = cityAndYearsText
        } else {
            cityAndYears.hide()
        }

        val bloodGroupString = when (patient.bloodGroup) {
            0 -> getString(R.string.first_negative)
            1 -> getString(R.string.first_positive)
            2 -> getString(R.string.second_negative)
            3 -> getString(R.string.second_positive)
            4 -> getString(R.string.third_negative)
            5 -> getString(R.string.third_positive)
            6 -> getString(R.string.fourth_negative)
            7 -> getString(R.string.fourth_positive)
            else -> null
        }
        if (bloodGroupString != null) {
            bloodGroup.text = "${getString(R.string.blood_group_hint).capitalize()}: $bloodGroupString"
        } else {
            bloodGroup.hide()
        }

        openChat.setOnClickListener {
            openChatWithPatient(patient)
        }
    }

    private fun openChatWithPatient(patient: PatientModel) {
        session.runIfOpened { userInfo ->
            ChatActivity.IntentBuilder(this)
                .recipientUser(patient)
                .currentUser(userInfo)
                .start()
        } ?: run {
            onSessionException()
        }
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var patient: PatientModel? = null

        fun patient(patient: PatientModel) = apply { this.patient = patient }

        override fun areParamsValid() = patient != null

        override fun get(): Intent =
            Intent(context, PatientActivity::class.java)
                .putExtra(PATIENT_PARAM, patient)

    }

}