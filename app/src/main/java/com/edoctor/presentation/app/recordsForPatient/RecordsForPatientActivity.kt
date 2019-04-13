package com.edoctor.presentation.app.recordsForPatient

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.presentation.app.events.EventsFragment
import com.edoctor.utils.CheckedIntentBuilder
import com.edoctor.utils.lazyFind

class RecordsForPatientActivity : AppCompatActivity() {

    companion object {
        const val PATIENT_PARAM = "patient"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_for_patient)

        val patient = intent?.getSerializableExtra(PATIENT_PARAM) as PatientModel

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
            title = getString(R.string.records_for_patient)
        }

        if (supportFragmentManager.findFragmentById(R.id.fragment_container) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, EventsFragment.newInstance(patient, null, true))
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var patient: PatientModel? = null

        fun patient(patient: PatientModel) = apply { this.patient = patient }

        override fun areParamsValid() = patient != null

        override fun get(): Intent =
            Intent(context, RecordsForPatientActivity::class.java)
                .putExtra(PATIENT_PARAM, patient)

    }

}