package com.edoctor.presentation.app.events

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.edoctor.R
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.presentation.MedicalEventType.*
import com.edoctor.data.entity.presentation.MedicalEventsInfo
import com.edoctor.data.entity.remote.model.record.MedicalEventModel
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.addEvent.AddOrEditEventActivity
import com.edoctor.presentation.app.events.EventsPresenter.Event
import com.edoctor.presentation.app.events.EventsPresenter.ViewState
import com.edoctor.presentation.app.parameters.ParametersPresenter
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class EventsFragment : BaseFragment<EventsPresenter, ViewState, Event>("EventsFragment") {

    companion object {
        const val EVENT_PARAM = "event"
        const val IS_REMOVED_PARAM = "is_removed"
        const val REQUEST_ADD_OR_EDIT_PARAMETER = 12301

        private const val PATIENT_PARAM = "patient"
        private const val DOCTOR_PARAM = "doctor"
        private const val CURRENT_USER_IS_PATIENT = "current_user_is_patient"
        private const val IS_REQUESTED_RECORDS = "is_requested_records"

        fun newInstance(
            patient: PatientModel,
            doctor: DoctorModel?,
            currentUserIsPatient: Boolean,
            isRequestedRecords: Boolean
        ) = EventsFragment().apply {
            arguments = Bundle().apply {
                putSerializable(PATIENT_PARAM, patient)
                putSerializable(DOCTOR_PARAM, doctor)
                putBoolean(CURRENT_USER_IS_PATIENT, currentUserIsPatient)
                putBoolean(IS_REQUESTED_RECORDS, isRequestedRecords)
            }
        }
    }

    @Inject
    override lateinit var presenter: EventsPresenter

    override val layoutRes: Int = R.layout.fragment_parameters

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var errorMessage: TextView
    private lateinit var fab: FloatingActionButton

    private lateinit var adapter: EventsAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalRecordsComponent.inject(this)
        val patient = arguments!!.getSerializable(PATIENT_PARAM) as PatientModel
        val doctor = arguments!!.getSerializable(DOCTOR_PARAM) as? DoctorModel
        val currentUserIsPatient = arguments!!.getBoolean(CURRENT_USER_IS_PATIENT)
        val isRequestedParams = arguments!!.getBoolean(IS_REQUESTED_RECORDS)
        presenter.init(patient, doctor, currentUserIsPatient, isRequestedParams)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        fab = view.findViewById(R.id.fab)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        errorMessage = view.findViewById(R.id.error_message)

        swipeRefreshLayout.setOnRefreshListener {
            presenter.updateAllEvents()
        }

        fab.hide()

        adapter = EventsAdapter(presenter.isRequestedRecords)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(view.context))
    }

    override fun onStart() {
        super.onStart()
        presenter.updateAllEvents()
    }

    override fun render(viewState: ViewState) {
        swipeRefreshLayout.isRefreshing = viewState.isLoading

        val info = viewState.medicalEventsInfo

        adapter.events = info.medicalEvents
        adapter.onEventClickListener = { event ->
            context?.let {
                startActivityForResult(
                    AddOrEditEventActivity.IntentBuilder(it)
                        .event(event)
                        .readOnly(!presenter.canBeEdited)
                        .get(),
                    REQUEST_ADD_OR_EDIT_PARAMETER
                )
            }
        }

        val currentUserIsPatient = presenter.currentUserIsPatient
        val isRequestedRecords = presenter.isRequestedRecords

        if (info.medicalEvents.isEmpty() && viewState.wasLoaded) {
            errorMessage.show()
            errorMessage.text = when {
                currentUserIsPatient && isRequestedRecords -> {
                    getString(R.string.empty_requested_events_for_patient)
                }
                !currentUserIsPatient && isRequestedRecords -> {
                    getString(R.string.empty_requested_events_for_doctor)
                }
                currentUserIsPatient && !isRequestedRecords -> {
                    getString(R.string.empty_events_for_patient)
                }
                else -> {
                    getString(R.string.empty_events_for_doctor)
                }
            }
        } else {
            errorMessage.hide()
        }

        if (info.availableMedicalEventTypes.isNotEmpty() && presenter.canBeAdded) {
            fab.show()
            fab.setOnClickListener {
                PopupMenu(fab.context, fab).apply {
                    val namesToTypes: List<Pair<String, MedicalEventType>> =
                        info.availableMedicalEventTypes.map { type ->
                            val name = when (type) {
                                is Analysis -> getString(R.string.analysis)
                                is Allergy -> getString(R.string.allergy)
                                is Note -> getString(R.string.note)
                                is Vaccination -> getString(R.string.vaccination)
                                is Procedure -> getString(R.string.procedure)
                                is DoctorVisit -> getString(R.string.doctor_visit)
                                is Sickness -> getString(R.string.sickness)
                            }
                            name to type
                        }

                    namesToTypes.forEach {
                        menu.add(it.first)
                    }

                    setOnMenuItemClickListener { item ->
                        val type = namesToTypes.first { it.first == item.title }.second
                        context?.let {
                            startActivityForResult(
                                AddOrEditEventActivity.IntentBuilder(it)
                                    .eventType(type)
                                    .readOnly(false)
                                    .get(),
                                REQUEST_ADD_OR_EDIT_PARAMETER
                            )
                        }
                        true
                    }
                    show()
                }
            }
        } else {
            fab.hide()
        }
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowNotSynchronizedEvent -> context?.toast(getString(R.string.records_synchronization_error))
            is Event.ShowUnhandledErrorEvent -> context?.toast(getString(R.string.unhandled_error_message))
            is Event.ShowNoNetworkException -> context?.toast(getString(R.string.network_error_message))
            is Event.ShowSessionException -> activity?.onSessionException()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD_OR_EDIT_PARAMETER) {
                val event = data?.getSerializableExtra(EVENT_PARAM) as? MedicalEventModel
                if (event != null) {
                    val isRemoved = data.getBooleanExtra(IS_REMOVED_PARAM, false)
                    if (isRemoved) {
                        presenter.deleteEvent(event)
                    } else {
                        presenter.addOrEditEvent(event)
                    }
                }
            }
        }
    }

}