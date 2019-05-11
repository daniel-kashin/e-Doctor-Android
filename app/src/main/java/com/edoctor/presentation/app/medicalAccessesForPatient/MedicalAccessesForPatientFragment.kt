package com.edoctor.presentation.app.medicalAccessesForPatient

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.presentation.MedicalAccessesForPatient
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.doctor.DoctorActivity
import com.edoctor.presentation.app.medicalAccessesForPatient.MedicalAccessesForPatientPresenter.Event
import com.edoctor.presentation.app.medicalAccessesForPatient.MedicalAccessesForPatientPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.edoctor.utils.SimpleDividerItemDecoration
import com.edoctor.utils.invisible
import com.edoctor.utils.session
import com.edoctor.utils.toast
import javax.inject.Inject

class MedicalAccessesForPatientFragment :
    BaseFragment<MedicalAccessesForPatientPresenter, ViewState, Event>("MedicalAccessesForPatientFragment") {

    @Inject
    override lateinit var presenter: MedicalAccessesForPatientPresenter

    override val layoutRes: Int = R.layout.fragment_medical_accesses_for_patient

    private lateinit var tryAgain: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: MedicalAccessesForPatientAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalAccessComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run {
            tryAgain = findViewById(R.id.try_again)
            progressBar = findViewById(R.id.progress_bar)
            errorMessage = findViewById(R.id.error_message)
            recyclerView = findViewById(R.id.recycler_view)

            tryAgain.invisible()
            progressBar.invisible()
            errorMessage.invisible()
            recyclerView.invisible()

            initializeRecyclerView(context)
        }
    }

    private fun initializeRecyclerView(context: Context) {
        adapter = MedicalAccessesForPatientAdapter().apply {
            onDeletePatientMedicalAccessClickListener = {
                presenter.deleteMedicalAccess(it.doctor)
            }
            onPatientMedicalAccessClickListener = { medicalAccess ->
                context.session.runIfOpened { userModel ->
                    DoctorActivity.IntentBuilder(context)
                        .doctor(medicalAccess.doctor)
                        .patient(userModel as? PatientModel)
                        .start()
                }
            }
        }
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(context))
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    override fun render(viewState: ViewState) {
        when (viewState) {
            is ViewState.LoadingViewState -> showLoading()
            is ViewState.UnknownExceptionViewState -> showUnhandledException()
            is ViewState.NetworkExceptionViewState -> showNetworkUnavailable()
            is ViewState.MedicalAccessesViewState -> viewState.medicalAccessesForPatient.let {
                if (it.medicalAccesses.isEmpty()) {
                    showEmptyMedicalAccesses()
                } else {
                    showMedicalAccesses(it)
                }
            }
        }
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowSessionException -> activity?.onSessionException()
            is Event.ShowUnknownException -> activity?.toast(getString(R.string.unhandled_error_message))
            is Event.ShowNoNetworkException -> activity?.toast(getString(R.string.network_error_message))
        }
    }

    private fun showEmptyMedicalAccesses() {
        errorMessage.text = getString(R.string.empty_medical_accesses_for_patient)
        tryAgain.setOnClickListener {
            presenter.loadMedicalAccesses()
        }

        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.INVISIBLE
        errorMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun showMedicalAccesses(medicalAccesses: MedicalAccessesForPatient) {
        adapter.medicalAccessesForPatient = medicalAccesses

        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.INVISIBLE
        errorMessage.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        tryAgain.visibility = View.INVISIBLE
        errorMessage.visibility = View.INVISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun showUnhandledException() {
        errorMessage.text = getString(R.string.unhandled_error_message)
        tryAgain.setOnClickListener {
            presenter.loadMedicalAccesses()
        }

        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.VISIBLE
        errorMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun showNetworkUnavailable() {
        errorMessage.text = getString(R.string.network_error_message)
        tryAgain.setOnClickListener {
            presenter.loadMedicalAccesses()
        }
        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.VISIBLE
        errorMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

}