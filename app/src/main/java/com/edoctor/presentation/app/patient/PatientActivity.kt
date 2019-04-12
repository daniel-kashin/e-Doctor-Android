package com.edoctor.presentation.app.patient

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.chat.ChatActivity
import com.edoctor.presentation.app.patient.PatientPresenter.Event
import com.edoctor.presentation.app.patient.PatientPresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.google.android.material.textfield.TextInputEditText
import javax.inject.Inject

class PatientActivity : BaseActivity<PatientPresenter, ViewState, Event>("PatientActivity") {

    companion object {
        const val PATIENT_PARAM = "patient"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)
    private val imageView by lazyFind<ImageView>(R.id.image_view)
    private val name by lazyFind<TextView>(R.id.name)
    private val cityAndYears by lazyFind<TextView>(R.id.city_and_years)
    private val bloodGroup by lazyFind<TextView>(R.id.blood_group)
    private val openChat by lazyFind<Button>(R.id.open_chat)

    private val labelMedcard by lazyFind<TextView>(R.id.label_medcard)
    private val medicalAccess by lazyFind<TextInputEditText>(R.id.medcard_access)
    private val medcardDelimiter by lazyFind<View>(R.id.medcard_delimiter)

    @Inject
    override lateinit var presenter: PatientPresenter

    override val layoutRes: Int = R.layout.activity_patient

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalAccessComponent.inject(this)
        val patient = intent?.getSerializableExtra(PATIENT_PARAM) as PatientModel
        presenter.init(patient)
    }

    override fun createScreenConfig() = ScreenConfig(isPortraitOrientationRequired = true, isOpenedSessionRequired = true)

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
            title = getString(R.string.patient).capitalize()
        }

        medicalAccess.isFocusable = false

        showPatientInfo(presenter.patient)
    }

    override fun render(viewState: ViewState) {
        val access = viewState.medicalAccessForDoctor
        if (access == null) {
            labelMedcard.hide()
            medicalAccess.hideParent()
            medcardDelimiter.hide()
        } else {
            medicalAccess.setText(
                if (access.availableTypes.isEmpty()) {
                    getString(R.string.you_have_no_access_to_medcard)
                } else {
                    getString(
                        R.string.read_access_types_count_param,
                        access.availableTypes.size,
                        access.allTypes.size
                    )
                }
            )

            medicalAccess.setOnClickListener {
                // TODO
            }

            labelMedcard.show()
            medicalAccess.showParent()
            medcardDelimiter.show()
        }
    }

    override fun showEvent(event: Event) = nothing()

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

    private fun AppCompatEditText.hideParent() {
        (parent.parent as? View)?.hide()
    }

    private fun AppCompatEditText.showParent() {
        (parent.parent as? View)?.show()
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