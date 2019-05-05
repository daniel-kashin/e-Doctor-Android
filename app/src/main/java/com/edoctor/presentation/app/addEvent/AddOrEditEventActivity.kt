package com.edoctor.presentation.app.addEvent

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.edoctor.R
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.mapper.MedicalEventMapper.toType
import com.edoctor.utils.*
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID.randomUUID

class AddOrEditEventActivity : AppCompatActivity() {

    companion object {
        const val EVENT_TYPE_PARAM = "event_type"
        const val EVENT_PARAM = "event"
        const val READ_ONLY_PARAM = "read_only"
        const val IS_REMOVED_PARAM = "is_removed"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)

    private val dateEditText by lazyFind<AppCompatEditText>(R.id.date)
    private val timeEditText by lazyFind<AppCompatEditText>(R.id.time)
    private val endDateEditText by lazyFind<AppCompatEditText>(R.id.end_date)
    private val endTimeEditText by lazyFind<AppCompatEditText>(R.id.end_time)
    private val nameEditText by lazyFind<AppCompatEditText>(R.id.name)
    private val clinicEditText by lazyFind<AppCompatEditText>(R.id.clinic_name)
    private val doctorNameEditText by lazyFind<AppCompatEditText>(R.id.doctor_name)
    private val doctorSpecializationEditText by lazyFind<AppCompatEditText>(R.id.doctor_specialization)
    private val symptomsEditText by lazyFind<AppCompatEditText>(R.id.symptoms)
    private val diagnosisEditText by lazyFind<AppCompatEditText>(R.id.diagnosis)
    private val recipeEditText by lazyFind<AppCompatEditText>(R.id.recipe)
    private val commentEditText by lazyFind<AppCompatEditText>(R.id.comment)

    private val saveButton by lazyFind<Button>(R.id.save_button)
    private val deleteButton by lazyFind<Button>(R.id.delete_button)

    private var calendar: Calendar = Calendar.getInstance()
    private val timestamp: Long
        get() = calendar.time.let { calendar.time }.time.javaTimeToUnixTime()

    private val endCalendar: Calendar = Calendar.getInstance()
    private val endTimestamp: Long?
        get() = if (endDateEditText.notEmptyText() != null && endTimeEditText.notEmptyText() != null) {
            endCalendar.time.let { endCalendar.time }.time.javaTimeToUnixTime()
        } else {
            null
        }

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        val event = intent.getSerializableExtra(EVENT_PARAM) as? MedicalEventModel
        val eventType = (intent.getSerializableExtra(EVENT_TYPE_PARAM) as? MedicalEventType) ?: toType(event!!)
        val readOnly = intent.getBooleanExtra(READ_ONLY_PARAM, true)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
            title = when (eventType) {
                is MedicalEventType.Analysis -> getString(R.string.analysis)
                is MedicalEventType.Allergy -> getString(R.string.allergy)
                is MedicalEventType.Note -> getString(R.string.note)
                is MedicalEventType.Vaccination -> getString(R.string.vaccination)
                is MedicalEventType.Procedure -> getString(R.string.procedure)
                is MedicalEventType.DoctorVisit -> getString(R.string.doctor_visit)
                is MedicalEventType.Sickness -> getString(R.string.sickness)
            }
        }

        dateEditText.isFocusable = false
        dateEditText.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(
                        year,
                        month,
                        dayOfMonth,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        0
                    )
                    dateEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(calendar.time.let { calendar.time }))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                show()
            }
        }

        timeEditText.isFocusable = false
        timeEditText.setOnClickListener {
            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    calendar.set(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute,
                        0
                    )
                    timeEditText.setText(SimpleDateFormat("HH:mm").format(calendar.time.let { calendar.time }))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        endDateEditText.isFocusable = false
        endDateEditText.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    endCalendar.set(
                        year,
                        month,
                        dayOfMonth,
                        endCalendar.get(Calendar.HOUR_OF_DAY),
                        endCalendar.get(Calendar.MINUTE),
                        0
                    )
                    endDateEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(endCalendar.time.let { endCalendar.time }))
                },
                endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                show()
            }
        }

        endTimeEditText.isFocusable = false
        endTimeEditText.setOnClickListener {
            TimePickerDialog(
                this,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    endCalendar.set(
                        endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH),
                        hourOfDay,
                        minute,
                        0
                    )
                    endTimeEditText.setText(SimpleDateFormat("HH:mm").format(endCalendar.time.let { endCalendar.time }))
                },
                endCalendar.get(Calendar.HOUR_OF_DAY),
                endCalendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        if (readOnly) {
            saveButton.hide()
            deleteButton.hide()
            dateEditText.isEnabled = false
            timeEditText.isEnabled = false
            endDateEditText.isEnabled = false
            endTimeEditText.isEnabled = false
            nameEditText.isEnabled = false
            clinicEditText.isEnabled = false
            doctorNameEditText.isEnabled = false
            doctorSpecializationEditText.isEnabled = false
            symptomsEditText.isEnabled = false
            diagnosisEditText.isEnabled = false
            recipeEditText.isEnabled = false
            commentEditText.isEnabled = false
        }

        if (event == null) {
            dateEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(calendar.time.let { calendar.time }))
            timeEditText.setText(SimpleDateFormat("HH:mm").format(calendar.time.let { calendar.time }))

            deleteButton.hide()
        } else {
            event.timestamp.unixTimeToJavaTime().let {
                dateEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(it))
                timeEditText.setText(SimpleDateFormat("HH:mm").format(it))
                calendar.time = Date(it)
            }

            if (event is EndDateSpecific) {
                event.endTimestamp?.unixTimeToJavaTime()?.let {
                    endDateEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(it))
                    endTimeEditText.setText(SimpleDateFormat("HH:mm").format(it))
                    endCalendar.time = Date(it)
                }
            }
            if (event is ClinicSpecific) {
                clinicEditText.setText(event.clinic)
            }
            if (event is DoctorSpecific) {
                doctorNameEditText.setText(event.doctorName)
                doctorSpecializationEditText.setText(event.doctorSpecialization)
            }
            commentEditText.setText(event.comment)

            deleteButton.setOnClickListener {
                finishWithRemoveMedicalEvent(event)
            }
        }

        when (eventType) {
            is MedicalEventType.Analysis -> {
                endDateEditText.hideParentAndNextView()
                endTimeEditText.hideParentAndNextView()
                doctorNameEditText.hideParentAndNextView()
                doctorSpecializationEditText.hideParentAndNextView()
                symptomsEditText.hideParentAndNextView()
                recipeEditText.hideParentAndNextView()

                diagnosisEditText.getParentView()?.let { it.hint = getString(R.string.conclusion) }

                (event as Analysis?)?.let {
                    diagnosisEditText.setText(it.result)
                    nameEditText.setText(it.name)
                }

                saveButton.setOnClickListener {
                    val name = nameEditText.notEmptyText()
                    if (name == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithMedicalEvent(
                            event?.copy(
                                timestamp = timestamp,
                                comment = commentEditText.notEmptyText(),
                                clinic = clinicEditText.notEmptyText(),
                                name = name,
                                result = diagnosisEditText.notEmptyText()
                            ) ?: Analysis(
                                uuid = randomUUID().toString(),
                                isDeleted = false,
                                timestamp = timestamp,
                                comment = commentEditText.notEmptyText(),
                                clinic = clinicEditText.notEmptyText(),
                                name = name,
                                result = diagnosisEditText.notEmptyText()
                            )
                        )
                    }
                }
            }
            is MedicalEventType.Allergy -> {
                clinicEditText.hideParentAndNextView()
                doctorNameEditText.hideParentAndNextView()
                doctorSpecializationEditText.hideParentAndNextView()
                diagnosisEditText.hideParentAndNextView()
                recipeEditText.hideParentAndNextView()

                nameEditText.getParentView()?.let { it.hint = getString(R.string.allergen) }

                (event as Allergy?)?.let {
                    nameEditText.setText(it.allergenName)
                    symptomsEditText.setText(it.symptoms)
                }

                saveButton.setOnClickListener {
                    val name = nameEditText.notEmptyText()
                    if (name == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithMedicalEvent(
                            event?.copy(
                                timestamp = timestamp,
                                comment = commentEditText.notEmptyText(),
                                endTimestamp = endTimestamp,
                                allergenName = name,
                                symptoms = symptomsEditText.notEmptyText()
                            ) ?: Allergy(
                                uuid = randomUUID().toString(),
                                isDeleted = false,
                                timestamp = timestamp,
                                comment = commentEditText.notEmptyText(),
                                endTimestamp = endTimestamp,
                                allergenName = name,
                                symptoms = symptomsEditText.notEmptyText()
                            )
                        )
                    }
                }
            }
            is MedicalEventType.Note -> {
                endDateEditText.hideParentAndNextView()
                endTimeEditText.hideParentAndNextView()
                nameEditText.hideParentAndNextView()
                clinicEditText.hideParentAndNextView()
                doctorNameEditText.hideParentAndNextView()
                doctorSpecializationEditText.hideParentAndNextView()
                symptomsEditText.hideParentAndNextView()
                diagnosisEditText.hideParentAndNextView()
                recipeEditText.hideParentAndNextView()

                saveButton.setOnClickListener {
                    val comment = commentEditText.notEmptyText()
                    if (comment == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithMedicalEvent(
                            (event as Note?)?.copy(
                                timestamp = timestamp,
                                comment = comment
                            ) ?: Note(
                                uuid = randomUUID().toString(),
                                isDeleted = false,
                                timestamp = timestamp,
                                comment = comment
                            )
                        )
                    }
                }
            }
            is MedicalEventType.Sickness -> {
                nameEditText.hideParentAndNextView()
                clinicEditText.hideParentAndNextView()
                doctorNameEditText.hideParentAndNextView()
                doctorSpecializationEditText.hideParentAndNextView()
                recipeEditText.hideParentAndNextView()

                (event as Sickness?)?.let {
                    symptomsEditText.setText(it.symptoms)
                    diagnosisEditText.setText(it.diagnosis)
                }

                saveButton.setOnClickListener {
                    val diagnosis = diagnosisEditText.notEmptyText()
                    if (diagnosis == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithMedicalEvent(
                            event?.copy(
                                timestamp = timestamp,
                                comment = commentEditText.notEmptyText(),
                                endTimestamp = endTimestamp,
                                symptoms = symptomsEditText.notEmptyText(),
                                diagnosis = diagnosis
                            ) ?: Sickness(
                                randomUUID().toString(),
                                isDeleted = false,
                                timestamp = timestamp,
                                comment = commentEditText.notEmptyText(),
                                endTimestamp = endTimestamp,
                                symptoms = symptomsEditText.notEmptyText(),
                                diagnosis = diagnosis
                            )
                        )
                    }
                }
            }
            is MedicalEventType.Procedure -> {
                endDateEditText.hideParentAndNextView()
                endTimeEditText.hideParentAndNextView()
                symptomsEditText.hideParentAndNextView()
                diagnosisEditText.hideParentAndNextView()
                recipeEditText.hideParentAndNextView()

                (event as Procedure?)?.let {
                    nameEditText.setText(it.name)
                }

                saveButton.setOnClickListener {
                    val name = nameEditText.notEmptyText()
                    if (name == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithMedicalEvent(
                            event?.copy(
                                timestamp = timestamp,
                                comment = commentEditText.notEmptyText(),
                                clinic = clinicEditText.notEmptyText(),
                                doctorName = doctorNameEditText.notEmptyText(),
                                doctorSpecialization = doctorSpecializationEditText.notEmptyText(),
                                name = name
                            ) ?: Procedure(
                                randomUUID().toString(),
                                timestamp = timestamp,
                                isDeleted = false,
                                comment = commentEditText.notEmptyText(),
                                clinic = clinicEditText.notEmptyText(),
                                doctorName = doctorNameEditText.notEmptyText(),
                                doctorSpecialization = doctorSpecializationEditText.notEmptyText(),
                                name = name
                            )
                        )
                    }
                }
            }
            is MedicalEventType.Vaccination -> {
                endDateEditText.hideParentAndNextView()
                endTimeEditText.hideParentAndNextView()
                symptomsEditText.hideParentAndNextView()
                diagnosisEditText.hideParentAndNextView()
                recipeEditText.hideParentAndNextView()

                (event as Vaccination?)?.let {
                    nameEditText.setText(it.name)
                }

                saveButton.setOnClickListener {
                    val name = nameEditText.notEmptyText()
                    if (name == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithMedicalEvent(
                            event?.copy(
                                timestamp = timestamp,
                                comment = commentEditText.notEmptyText(),
                                clinic = clinicEditText.notEmptyText(),
                                doctorName = doctorNameEditText.notEmptyText(),
                                doctorSpecialization = doctorSpecializationEditText.notEmptyText(),
                                name = name
                            ) ?: Vaccination(
                                randomUUID().toString(),
                                timestamp = timestamp,
                                isDeleted = false,
                                comment = commentEditText.notEmptyText(),
                                clinic = clinicEditText.notEmptyText(),
                                doctorName = doctorNameEditText.notEmptyText(),
                                doctorSpecialization = doctorSpecializationEditText.notEmptyText(),
                                name = name
                            )
                        )
                    }
                }
            }
            is MedicalEventType.DoctorVisit -> {
                endDateEditText.hideParentAndNextView()
                endTimeEditText.hideParentAndNextView()
                nameEditText.hideParentAndNextView()

                diagnosisEditText.getParentView()?.let { it.hint = getString(R.string.diagnosis_and_recommendations) }
                symptomsEditText.getParentView()?.let { it.hint = getString(R.string.complaints) }

                saveButton.setOnClickListener {
                    val symptoms = symptomsEditText.notEmptyText()
                    val diagnosis = diagnosisEditText.notEmptyText()
                    if (symptoms == null || diagnosis == null) {
                        toast(R.string.fields_wrong_content)
                    } else {
                        finishWithMedicalEvent(
                            (event as DoctorVisit?)?.copy(
                                timestamp = timestamp,
                                comment = commentEditText.notEmptyText(),
                                clinic = clinicEditText.notEmptyText(),
                                doctorName = doctorNameEditText.notEmptyText(),
                                doctorSpecialization = doctorSpecializationEditText.notEmptyText(),
                                complaints = symptoms,
                                diagnosisAndRecommendations = diagnosis,
                                recipe = recipeEditText.notEmptyText()
                            ) ?: DoctorVisit(
                                randomUUID().toString(),
                                timestamp = timestamp,
                                isDeleted = false,
                                comment = commentEditText.notEmptyText(),
                                clinic = clinicEditText.notEmptyText(),
                                doctorName = doctorNameEditText.notEmptyText(),
                                doctorSpecialization = doctorSpecializationEditText.notEmptyText(),
                                complaints = symptoms,
                                diagnosisAndRecommendations = diagnosis,
                                recipe = recipeEditText.notEmptyText()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun EditText.notEmptyText(): String? {
        return text?.toString()?.takeIfNotEmpty()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun finishWithMedicalEvent(medicalEventModel: MedicalEventModel) {
        setResult(Activity.RESULT_OK, Intent().putExtra(EVENT_PARAM, medicalEventModel))
        finish()
    }

    private fun finishWithRemoveMedicalEvent(medicalEventModel: MedicalEventModel) {
        setResult(
            Activity.RESULT_OK,
            Intent()
                .putExtra(EVENT_PARAM, medicalEventModel)
                .putExtra(IS_REMOVED_PARAM, true)
        )
        finish()
    }

    private fun AppCompatEditText.getParentView(): TextInputLayout? = parent.parent as? TextInputLayout

    private fun AppCompatEditText.hideParentAndNextView() {
        (parent.parent as? View)?.let { parent ->
            parent.hide()
            (parent.parent as? ViewGroup)?.let { viewGroup ->
                viewGroup.getChildAt(viewGroup.indexOfChild(parent) + 1)?.hide()
            }
        }
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var eventType: MedicalEventType? = null
        private var event: MedicalEventModel? = null
        private var readOnly: Boolean? = null

        fun eventType(eventType: MedicalEventType) = apply { this.eventType = eventType }
        fun event(event: MedicalEventModel) = apply { this.event = event }
        fun readOnly(readOnly: Boolean) = apply { this.readOnly = readOnly }

        override fun areParamsValid() = (eventType != null || event != null) && readOnly != null

        override fun get(): Intent =
            Intent(context, AddOrEditEventActivity::class.java)
                .putExtra(EVENT_TYPE_PARAM, eventType)
                .putExtra(EVENT_PARAM, event)
                .putExtra(READ_ONLY_PARAM, readOnly)

    }

}