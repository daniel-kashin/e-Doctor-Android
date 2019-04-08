package com.edoctor.presentation.app.events

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.presentation.MedicalEventType.*
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.addEvent.AddOrEditEventActivity
import com.edoctor.presentation.app.events.EventsPresenter.Event
import com.edoctor.presentation.app.events.EventsPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.SimpleDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class EventsFragment : BaseFragment<EventsPresenter, ViewState, Event>("EventsFragment") {

    companion object {
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
                    info.availableMedicalEventTypes.map {
                        val name = when (it) {
                            Analysis -> getString(R.string.analysis)
                            Allergy -> getString(R.string.allergy)
                            Note -> getString(R.string.note)
                            Vaccination -> getString(R.string.vaccination)
                            Procedure -> getString(R.string.procedure)
                            DoctorVisit -> getString(R.string.doctor_visit)
                            Sickness -> getString(R.string.sickness)
                        }
                        name to it
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

}