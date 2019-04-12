package com.edoctor.presentation.app.editMedicalAccess

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.presentation.MedicalAccessForPatient
import com.edoctor.data.entity.presentation.MedicalRecordType
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessPresenter.Event
import com.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessPresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import java.io.Serializable
import javax.inject.Inject

class EditMedicalAccessActivity :
    BaseActivity<EditMedicalAccessPresenter, ViewState, Event>("EditMedicalAccessActivity") {

    companion object {
        const val ALL_MEDICAL_RECORD_TYPES_PARAM = "all_medical_record_types"
        const val MEDICAL_ACCESS_FOR_PATIENT_PARAM = "medical_access_for_patient"
    }

    @Inject
    override lateinit var presenter: EditMedicalAccessPresenter

    private val toolbar by lazyFind<Toolbar>(R.id.toolbar)

    override val layoutRes: Int? = R.layout.activity_edit_medical_access

    private val recyclerView by lazyFind<RecyclerView>(R.id.recycler_view)
    private val toolbarPrimaryText by lazyFind<TextView>(R.id.toolbar_primary_text)
    private val iconConfirm by lazyFind<ImageView>(R.id.icon_confirm)
    private lateinit var adapter: EditMedicalAccessAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalAccessComponent.inject(this)
        val medicalAccessForPatient =
            intent?.getSerializableExtra(MEDICAL_ACCESS_FOR_PATIENT_PARAM) as MedicalAccessForPatient
        val allTypes = intent?.getSerializableExtra(ALL_MEDICAL_RECORD_TYPES_PARAM) as List<MedicalRecordType>
        presenter.init(medicalAccessForPatient, allTypes)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }

        toolbarPrimaryText.text = presenter.medicalAccessForPatient.doctor.fullName ?: getString(R.string.doctor_access)

        adapter = EditMedicalAccessAdapter()
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(this))
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        adapter.medicalRecordTypes = presenter.allTypes
            .map { type ->
                val foundInAccess = presenter.medicalAccessForPatient
                    .availableTypes
                    .firstOrNull { type.javaClass == it.javaClass }
                type to (foundInAccess != null)
            }
            .toMutableList()

        iconConfirm.setOnClickListener {
            presenter.onMedicalRecordTypesPicked(
                adapter.medicalRecordTypes
                    .filter { it.second }
                    .map { it.first }
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun render(viewState: ViewState) = nothing()

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowUnknownException -> toast(getString(R.string.unhandled_error_message))
            is Event.ShowNoNetworkException -> toast(getString(R.string.network_error_message))
            is Event.MedicalRecordTypesChanged -> toast(getString(R.string.medcard_access_changed))
            is Event.ShowSessionException -> onSessionException()
        }
    }

    class IntentBuilder(context: Context) : CheckedIntentBuilder(context) {

        private var medicalAccessForPatient: MedicalAccessForPatient? = null
        private var allMedicalRecordTypes: List<MedicalRecordType>? = null

        fun medicalAccessesForPatient(medicalAccessForPatient: MedicalAccessForPatient) = apply {
            this.medicalAccessForPatient = medicalAccessForPatient
        }

        fun allMedicalRecordTypes(allMedicalRecordTypes: List<MedicalRecordType>) = apply {
            this.allMedicalRecordTypes = allMedicalRecordTypes
        }

        override fun areParamsValid() = medicalAccessForPatient != null && allMedicalRecordTypes != null

        override fun get(): Intent =
            Intent(context, EditMedicalAccessActivity::class.java)
                .putExtra(ALL_MEDICAL_RECORD_TYPES_PARAM, allMedicalRecordTypes as Serializable)
                .putExtra(MEDICAL_ACCESS_FOR_PATIENT_PARAM, medicalAccessForPatient)

    }

}