package com.edoctor.presentation.app.medcardForDoctor

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.presentation.app.medcard.MedcardFragment
import com.edoctor.presentation.app.patient.PatientActivity
import com.edoctor.utils.CheckedIntentBuilder
import com.edoctor.utils.lazyFind

class MedcardForDoctorActivity : AppCompatActivity() {

    companion object {
        const val PATIENT_PARAM = "patient"
        const val DOCTOR_PARAM = "doctor"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medcard_for_doctor)

        val patient = intent?.getSerializableExtra(PATIENT_PARAM) as PatientModel
        val doctor = intent?.getSerializableExtra(DOCTOR_PARAM) as DoctorModel

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
            title = patient.fullName ?: getString(R.string.patient).capitalize()
        }

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MedcardFragment.newInstance(patient, doctor, false))
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var patient: PatientModel? = null
        private var doctor: DoctorModel? = null

        fun patient(patient: PatientModel) = apply { this.patient = patient }
        fun doctor(doctor: DoctorModel) = apply { this.doctor = doctor }

        override fun areParamsValid() = patient != null && doctor != null

        override fun get(): Intent =
            Intent(context, MedcardForDoctorActivity::class.java)
                .putExtra(PATIENT_PARAM, patient)
                .putExtra(DOCTOR_PARAM, doctor)

    }

}