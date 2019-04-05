package com.edoctor.presentation.app.parameters

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.remote.model.record.BodyParameterType.*
import com.edoctor.data.entity.remote.model.record.BodyParameterType.Custom.Companion.NEW
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.addParameter.AddParameterActivity
import com.edoctor.presentation.app.parameters.ParametersPresenter.Event
import com.edoctor.presentation.app.parameters.ParametersPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.SimpleDividerItemDecoration
import com.edoctor.utils.toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class ParametersFragment : BaseFragment<ParametersPresenter, ViewState, Event>("ParametersFragment") {

    @Inject
    override lateinit var presenter: ParametersPresenter

    override val layoutRes: Int = R.layout.fragment_parameters

    lateinit var recyclerView: RecyclerView
    lateinit var fab: FloatingActionButton

    lateinit var adapter: ParametersAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalRecordsComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        fab = view.findViewById(R.id.fab)

        fab.hide()

        adapter = ParametersAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false)
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(view.context))
    }

    @SuppressLint("RestrictedApi")
    override fun render(viewState: ViewState) {
        val info = viewState.latestBodyParametersInfo
        if (info != null) {
            adapter.parameters = info.latestBodyParametersOfEachType
            fab.show()
            fab.setOnClickListener {
                PopupMenu(fab.context, fab).apply {
                    val namesToTypes: List<Pair<String, BodyParameterType>> =
                        info.availableBodyParametesTypes.map {
                            val name = when (it) {
                                is Height -> getString(R.string.height)
                                is Weight -> getString(R.string.weight)
                                is BloodPressure -> getString(R.string.blood_pressure)
                                is BloodSugar -> getString(R.string.blood_sugar)
                                is Temperature -> getString(R.string.temperature)
                                is BloodOxygen -> getString(R.string.blood_oxygen)
                                is Custom -> if (it == NEW) getString(R.string.new_parameter) else "${it.name} (${it.unit})"
                            }
                            name to it
                        }

                    namesToTypes.forEach {
                        menu.add(it.first)
                    }

                    setOnMenuItemClickListener { item ->
                        val type = namesToTypes.first { it.first == item.title }.second
                        AddParameterActivity.IntentBuilder(this@ParametersFragment)
                            .parameterType(type)
                            .start()
                        true
                    }
                    show()
                }
            }
        } else {
            adapter.parameters = emptyList()
            fab.hide()
        }
    }

    override fun showEvent(event: Event) {

    }

}