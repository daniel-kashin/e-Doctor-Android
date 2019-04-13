package com.edoctor.presentation.app.parameters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.remote.model.record.*
import com.edoctor.data.entity.presentation.BodyParameterType.*
import com.edoctor.data.entity.presentation.BodyParameterType.Custom.Companion.NEW
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.mapper.BodyParameterMapper.toType
import com.edoctor.presentation.app.addParameter.AddOrEditParameterActivity
import com.edoctor.presentation.app.events.EventsFragment
import com.edoctor.presentation.app.parameter.ParameterActivity
import com.edoctor.presentation.app.parameters.ParametersPresenter.Event
import com.edoctor.presentation.app.parameters.ParametersPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.SimpleDividerItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class ParametersFragment : BaseFragment<ParametersPresenter, ViewState, Event>("ParametersFragment") {

    companion object {
        const val PARAMETER_PARAM = "parameter"
        const val IS_REMOVED_PARAM = "is_removed"
        const val REQUEST_ADD_OR_EDIT_PARAMETER = 12300

        private const val PATIENT_PARAM = "patient"

        fun newInstance(patient: PatientModel?) = ParametersFragment().apply {
            arguments = Bundle().apply {
                putSerializable(PATIENT_PARAM, patient)
            }
        }
    }

    @Inject
    override lateinit var presenter: ParametersPresenter

    override val layoutRes: Int = R.layout.fragment_parameters

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton

    private lateinit var adapter: ParametersAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalRecordsComponent.inject(this)
        val patient = arguments?.getSerializable(PATIENT_PARAM) as? PatientModel
        presenter.init(patient)
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

        adapter.parameters = info.latestBodyParametersOfEachType
        adapter.onParameterClickListener = { parameter ->
            ParameterActivity.IntentBuilder(this)
                .parameterType(toType(parameter))
                .start()
        }

        if (info.availableBodyParametesTypes.isNotEmpty()) {
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
                        context?.let {
                            startActivityForResult(
                                AddOrEditParameterActivity.IntentBuilder(it)
                                    .parameterType(type)
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
    }

    override fun showEvent(event: Event) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ParameterActivity.REQUEST_ADD_OR_EDIT_PARAMETER) {
                val parameter = data?.getSerializableExtra(PARAMETER_PARAM) as? BodyParameterModel
                val isRemoved = data?.getBooleanExtra(IS_REMOVED_PARAM, false) ?: false
                if (parameter != null) {
                    if (isRemoved) {
                        presenter.removeParameter(parameter)
                    } else {
                        presenter.addOrEditParameter(parameter)
                    }
                }
            }
        }
    }

}