package com.infostrategic.edoctor.presentation.app.doctor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.infostrategic.edoctor.R
import com.infostrategic.edoctor.data.entity.remote.model.user.DoctorModel
import com.infostrategic.edoctor.data.entity.remote.model.user.PatientModel
import com.infostrategic.edoctor.data.injection.ApplicationComponent
import com.infostrategic.edoctor.presentation.app.chat.ChatActivity
import com.infostrategic.edoctor.presentation.app.doctor.DoctorPresenter.Event
import com.infostrategic.edoctor.presentation.app.doctor.DoctorPresenter.ViewState
import com.infostrategic.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessActivity
import com.infostrategic.edoctor.presentation.app.requestedRecordsForPatient.RequestedRecordsForPatientActivity
import com.infostrategic.edoctor.presentation.architecture.activity.BaseActivity
import com.infostrategic.edoctor.utils.*
import com.infostrategic.edoctor.utils.SessionExceptionHelper.onSessionException
import com.google.android.material.textfield.TextInputEditText
import javax.inject.Inject

class DoctorActivity : BaseActivity<DoctorPresenter, ViewState, Event>("DoctorActivity") {

    companion object {
        const val DOCTOR_PARAM = "doctor"
        const val PATIENT_PARAM = "patient"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)
    private val imageView by lazyFind<ImageView>(R.id.image_view)
    private val name by lazyFind<TextView>(R.id.name)
    private val readyForConsultation by lazyFind<TextView>(R.id.ready_for_consultation)
    private val readyForAudio by lazyFind<TextView>(R.id.ready_for_audio)
    private val specialization by lazyFind<TextView>(R.id.specialization)
    private val category by lazyFind<TextView>(R.id.category)
    private val openChat by lazyFind<Button>(R.id.open_chat)

    private val labelCareer by lazyFind<TextView>(R.id.label_career)
    private val clinicalInterests by lazyFind<TextInputEditText>(R.id.clinical_interests)
    private val education by lazyFind<TextInputEditText>(R.id.education)
    private val workExperience by lazyFind<TextInputEditText>(R.id.work_experience)
    private val trainings by lazyFind<TextInputEditText>(R.id.trainings)
    private val careerDelimiter by lazyFind<View>(R.id.career_delimiter)

    private val labelMedcard by lazyFind<TextView>(R.id.label_medcard)
    private val medicalAccess by lazyFind<TextInputEditText>(R.id.medcard_access)
    private val recordRequest by lazyFind<TextInputEditText>(R.id.record_request)
    private val medcardDelimiter by lazyFind<View>(R.id.medcard_delimiter)

    @Inject
    override lateinit var presenter: DoctorPresenter

    override val layoutRes: Int = R.layout.activity_doctor

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalAccessComponent.inject(this)
        val doctor = intent?.getSerializableExtra(DOCTOR_PARAM) as DoctorModel
        val patient = intent?.getSerializableExtra(PATIENT_PARAM) as PatientModel
        presenter.init(doctor, patient)
    }

    override fun createScreenConfig(): ScreenConfig {
        return ScreenConfig(isPortraitOrientationRequired = true, isOpenedSessionRequired = true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        medicalAccess.isFocusable = false
        recordRequest.isFocusable = false

        showDoctorInfo(presenter.doctor)
    }

    override fun onStart() {
        super.onStart()
        presenter.updateDoctorInfo()
    }

    override fun render(viewState: ViewState) {
        val medcardInfo = viewState.medcardInfo
        if (medcardInfo == null) {
            labelMedcard.hide()
            medicalAccess.hideParent()
            recordRequest.hideParent()
            medcardDelimiter.hide()
        } else {
            val availableTypes = medcardInfo.first.medicalAccess.availableTypes
            val allTypes = medcardInfo.first.allTypes

            medicalAccess.setText(
                if (availableTypes.isEmpty()) {
                    getString(R.string.doctor_has_no_access_to_medcard)
                } else {
                    getString(R.string.read_access_types_count_param, availableTypes.size, allTypes.size)
                }
            )

            medicalAccess.setOnClickListener {
                EditMedicalAccessActivity.IntentBuilder(this)
                    .allMedicalRecordTypes(allTypes)
                    .medicalAccessesForPatient(medcardInfo.first.medicalAccess)
                    .start()
            }

            recordRequest.setText(
                if (medcardInfo.second.isEmpty()) {
                    getString(R.string.record_request_for_patient_emtpy)
                } else {
                    getString(R.string.record_request_for_patient_param, medcardInfo.second.size)
                }
            )

            recordRequest.setOnClickListener {
                session.runIfOpened { userModel ->
                    if (userModel is PatientModel) {
                        RequestedRecordsForPatientActivity.IntentBuilder(this)
                            .doctor(presenter.doctor)
                            .patient(userModel)
                            .start()
                    }
                }
            }

            labelMedcard.show()
            medicalAccess.showParent()
            recordRequest.showParent()
            medcardDelimiter.show()
        }
    }

    override fun showEvent(event: Event) = nothing()

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun showDoctorInfo(doctor: DoctorModel) {
        PicassoProvider.get(this)
            .load(doctor.relativeImageUrl)
            .fit()
            .centerCrop()
            .placeholder(R.color.lightLightGrey)
            .into(imageView)

        name.text = doctor.fullName ?: getString(R.string.name_not_set)

        val acceptColor = ContextCompat.getColor(this, R.color.accept_call)
        val declineColor = ContextCompat.getColor(this, R.color.decline_call)

        if (doctor.isReadyForConsultation) {
            readyForConsultation.text = getString(R.string.ready_for_consultation_now)
            readyForConsultation.setTextColor(acceptColor)
        } else {
            readyForConsultation.text = getString(R.string.not_ready_for_consultation_now)
            readyForConsultation.setTextColor(declineColor)
        }

        when (doctor.isReadyForAudio) {
            1 -> {
                readyForAudio.text = getString(R.string.ready_for_audio)
                readyForAudio.setTextColor(acceptColor)
            }
            2 -> {
                readyForAudio.text = getString(R.string.ready_for_audio_and_video)
                readyForAudio.setTextColor(acceptColor)
            }
            else -> {
                readyForAudio.text = getString(R.string.not_ready_for_audio_or_video)
                readyForAudio.setTextColor(declineColor)
            }
        }

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
            careerDelimiter.hide()
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
        session.runIfOpened { userInfo ->
            ChatActivity.IntentBuilder(this)
                .recipientUser(doctor)
                .currentUser(userInfo)
                .start()
        } ?: run {
            onSessionException()
        }
    }

    private fun AppCompatEditText.hideParent() {
        (parent.parent as? View)?.hide()
    }

    private fun AppCompatEditText.showParent() {
        (parent.parent as? View)?.show()
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var doctor: DoctorModel? = null
        private var patient: PatientModel? = null

        fun doctor(doctor: DoctorModel) = apply { this.doctor = doctor }
        fun patient(patient: PatientModel?) = apply { this.patient = patient }

        override fun areParamsValid() = doctor != null && patient != null

        override fun get(): Intent =
            Intent(context, DoctorActivity::class.java)
                .putExtra(DoctorActivity.DOCTOR_PARAM, doctor)
                .putExtra(DoctorActivity.PATIENT_PARAM, patient)

    }

}