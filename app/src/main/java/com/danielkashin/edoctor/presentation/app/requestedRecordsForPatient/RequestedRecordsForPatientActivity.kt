package com.danielkashin.edoctor.presentation.app.requestedRecordsForPatient

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.danielkashin.edoctor.R
import com.danielkashin.edoctor.data.entity.remote.model.user.DoctorModel
import com.danielkashin.edoctor.data.entity.remote.model.user.PatientModel
import com.danielkashin.edoctor.presentation.app.events.EventsFragment
import com.danielkashin.edoctor.utils.CheckedIntentBuilder
import com.danielkashin.edoctor.utils.lazyFind

class RequestedRecordsForPatientActivity : AppCompatActivity() {

    companion object {
        const val DOCTOR_PARAM = "doctor"
        const val PATIENT_PARAM = "patient"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_from_doctor)

        val doctor = intent?.getSerializableExtra(DOCTOR_PARAM) as DoctorModel
        val patient = intent?.getSerializableExtra(PATIENT_PARAM) as PatientModel

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
            title = getString(R.string.records_from_doctor)
        }

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, EventsFragment.newInstance(patient, doctor, true, true))
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var doctor: DoctorModel? = null
        private var patient: PatientModel? = null

        fun doctor(doctor: DoctorModel) = apply { this.doctor = doctor }
        fun patient(patient: PatientModel) = apply { this.patient = patient }

        override fun areParamsValid() = doctor != null && patient != null

        override fun get(): Intent =
            Intent(context, RequestedRecordsForPatientActivity::class.java)
                .putExtra(DOCTOR_PARAM, doctor)
                .putExtra(PATIENT_PARAM, patient)

    }

}