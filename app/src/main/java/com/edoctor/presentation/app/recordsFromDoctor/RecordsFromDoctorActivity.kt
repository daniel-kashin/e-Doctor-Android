package com.edoctor.presentation.app.recordsFromDoctor

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.edoctor.R
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.presentation.app.events.EventsFragment
import com.edoctor.utils.CheckedIntentBuilder
import com.edoctor.utils.lazyFind

class RecordsFromDoctorActivity : AppCompatActivity() {

    companion object {
        const val DOCTOR_PARAM = "doctor"
    }

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_from_doctor)

        val doctor = intent?.getSerializableExtra(DOCTOR_PARAM) as DoctorModel

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
                .replace(R.id.fragment_container, EventsFragment.newInstance(null, doctor, true))
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var doctor: DoctorModel? = null

        fun doctor(doctor: DoctorModel) = apply { this.doctor = doctor }

        override fun areParamsValid() = doctor != null

        override fun get(): Intent =
            Intent(context, RecordsFromDoctorActivity::class.java)
                .putExtra(DOCTOR_PARAM, doctor)

    }

}