package com.danielkashin.edoctor.presentation.app.parameters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.danielkashin.edoctor.R
import com.danielkashin.edoctor.data.entity.presentation.BodyParameterType
import com.danielkashin.edoctor.data.entity.remote.model.record.*
import com.danielkashin.edoctor.data.entity.presentation.BodyParameterType.*
import com.danielkashin.edoctor.data.entity.presentation.BodyParameterType.Custom.Companion.NEW
import com.danielkashin.edoctor.data.entity.remote.model.user.PatientModel
import com.danielkashin.edoctor.data.injection.ApplicationComponent
import com.danielkashin.edoctor.data.mapper.BodyParameterMapper.toType
import com.danielkashin.edoctor.presentation.app.addParameter.AddOrEditParameterActivity
import com.danielkashin.edoctor.presentation.app.parameter.ParameterActivity
import com.danielkashin.edoctor.presentation.app.parameters.ParametersPresenter.Event
import com.danielkashin.edoctor.presentation.app.parameters.ParametersPresenter.ViewState
import com.danielkashin.edoctor.presentation.architecture.fragment.BaseFragment
import com.danielkashin.edoctor.utils.SessionExceptionHelper.onSessionException
import com.danielkashin.edoctor.utils.SimpleDividerItemDecoration
import com.danielkashin.edoctor.utils.hide
import com.danielkashin.edoctor.utils.show
import com.danielkashin.edoctor.utils.toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class ParametersFragment : BaseFragment<ParametersPresenter, ViewState, Event>("ParametersFragment") {

    companion object {
        const val PATIENT_PARAM = "patient"
        const val CURRENT_USER_IS_PATIENT = "current_user_is_patient"

        const val PARAMETER_PARAM = "parameter"
        const val IS_REMOVED_PARAM = "is_removed"
        const val REQUEST_ADD_OR_EDIT_PARAMETER = 12300


        fun newInstance(patient: PatientModel, currentUserIsPatient: Boolean) = ParametersFragment().apply {
            arguments = Bundle().apply {
                putSerializable(PATIENT_PARAM, patient)
                putBoolean(CURRENT_USER_IS_PATIENT, currentUserIsPatient)
            }
        }
    }

    @Inject
    override lateinit var presenter: ParametersPresenter

    override val layoutRes: Int = R.layout.fragment_parameters

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var errorMessage: TextView
    private lateinit var fab: FloatingActionButton

    private lateinit var adapter: ParametersAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalRecordsComponent.inject(this)
        val patient = arguments!!.getSerializable(PATIENT_PARAM) as PatientModel
        val currentUserIsPatient = arguments!!.getBoolean(CURRENT_USER_IS_PATIENT)
        presenter.init(patient, currentUserIsPatient)
    }

    override fun onStart() {
        super.onStart()
        presenter.updateAllParameters()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        fab = view.findViewById(R.id.fab)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        errorMessage = view.findViewById(R.id.error_message)

        swipeRefreshLayout.setOnRefreshListener {
            presenter.updateAllParameters()
        }

        fab.hide()

        adapter = ParametersAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(view.context, RecyclerView.VERTICAL, false).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(view.context))
    }

    @SuppressLint("RestrictedApi")
    override fun render(viewState: ViewState) {
        swipeRefreshLayout.isRefreshing = viewState.isLoading

        val info = viewState.latestBodyParametersInfo

        adapter.parameters = info.latestBodyParametersOfEachType
        adapter.onParameterClickListener = { parameter ->
            ParameterActivity.IntentBuilder(this)
                .parameterType(toType(parameter))
                .patient(presenter.patient)
                .currentUserIsPatient(presenter.currentUserIsPatient)
                .start()
        }

        val currentUserIsPatient = presenter.currentUserIsPatient

        if (info.latestBodyParametersOfEachType.isEmpty() && viewState.wasLoaded) {
            errorMessage.show()
            errorMessage.text = if (currentUserIsPatient) {
                getString(R.string.empty_parameters_for_patient)
            } else {
                getString(R.string.empty_parameters_for_doctor)
            }
        } else {
            errorMessage.hide()
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
                                    .readOnly(!presenter.currentUserIsPatient)
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