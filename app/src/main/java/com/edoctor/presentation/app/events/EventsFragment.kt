package com.edoctor.presentation.app.events

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.presentation.MedicalEventType.*
import com.edoctor.data.entity.remote.model.record.BodyParameterModel
import com.edoctor.data.entity.remote.model.record.MedicalEventModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.addEvent.AddOrEditEventActivity
import com.edoctor.presentation.app.events.EventsPresenter.Event
import com.edoctor.presentation.app.events.EventsPresenter.ViewState
import com.edoctor.presentation.app.parameter.ParameterActivity
import com.edoctor.presentation.app.parameters.ParametersFragment
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.SimpleDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class EventsFragment : BaseFragment<EventsPresenter, ViewState, Event>("EventsFragment") {

    companion object {
        const val EVENT_PARAM = "event"
        const val IS_REMOVED_PARAM = "is_removed"
        const val REQUEST_ADD_OR_EDIT_PARAMETER = 12301
    }

    @Inject
    override lateinit var presenter: EventsPresenter

    override val layoutRes: Int = R.layout.fragment_parameters

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton

    private lateinit var adapter: EventsAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalRecordsComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        fab = view.findViewById(R.id.fab)

        fab.hide()

        adapter = EventsAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(view.context))
    }

    override fun render(viewState: ViewState) {
        val info = viewState.medicalEventsInfo

        adapter.events = info.medicalEvents
        adapter.onEventClickListener = { event ->
            context?.let {
                startActivityForResult(
                    AddOrEditEventActivity.IntentBuilder(it)
                        .event(event)
                        .get(),
                    REQUEST_ADD_OR_EDIT_PARAMETER
                )
            }
        }

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
                                .get(),
                            REQUEST_ADD_OR_EDIT_PARAMETER
                        )
                    }
                    true
                }
                show()
            }
        }
    }

    override fun showEvent(event: Event) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ADD_OR_EDIT_PARAMETER) {
                val event = data?.getSerializableExtra(EVENT_PARAM) as? MedicalEventModel
                if (event != null) {
                    val isRemoved = data.getBooleanExtra(IS_REMOVED_PARAM, false)
                    if (isRemoved) {
                        presenter.removeEvent(event)
                    } else {
                        presenter.addOrEditEvent(event)
                    }
                }
            }
        }
    }

}