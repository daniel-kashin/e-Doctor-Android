package com.edoctor.presentation.app.restrictions

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.edoctor.R
import com.edoctor.data.entity.presentation.MedicalAccessForPatient
import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessForPatientModel
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.doctor.DoctorActivity
import com.edoctor.presentation.app.findDoctor.FindDoctorAdapter
import com.edoctor.presentation.app.restrictions.MedicalAccessesPresenter.Event
import com.edoctor.presentation.app.restrictions.MedicalAccessesPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.edoctor.utils.SimpleDividerItemDecoration
import com.edoctor.utils.invisible
import javax.inject.Inject

class MedicalAccessesFragment : BaseFragment<MedicalAccessesPresenter, ViewState, Event>("MedicalAccessesFragment") {

    @Inject
    override lateinit var presenter: MedicalAccessesPresenter

    override val layoutRes: Int = R.layout.fragment_restrictions

    private lateinit var tryAgain: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var doctorsAdapter: FindDoctorAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.medicalAccessesComponent.inject(this)
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
        doctorsAdapter = FindDoctorAdapter().apply {
            onDoctorClickListener = { doctor ->
                DoctorActivity.IntentBuilder(context)
                    .doctor(doctor)
                    .start()
            }
        }
        recyclerView.adapter = doctorsAdapter
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(context))
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    override fun render(viewState: ViewState) {
        when (viewState) {
            is ViewState.LoadingViewState -> showLoading()
            is ViewState.UnknownExceptionViewState -> showUnhandledException()
            is ViewState.NetworkExceptionViewState -> showNetworkUnavailable()
            is ViewState.MedicalAccessesViewState -> viewState.medicalAccessesForPatient.let {
                if (it.medicalAccesses.isEmpty()) showEmptyMedicalAccesses() else showMedicalAccesses(it.medicalAccesses)
            }
        }
    }

    override fun showEvent(event: Event) {
        if (event is Event.ShowSessionException) {
            activity?.onSessionException()
        }
    }

    private fun showEmptyMedicalAccesses() {
        errorMessage.text = getString(R.string.empty_medical_accesses)
        tryAgain.setOnClickListener {
            presenter.loadMedicalAccesses()
        }

        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.INVISIBLE
        errorMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun showMedicalAccesses(medicalAccesses: List<MedicalAccessForPatient>) {
//        doctorsAdapter.setDoctors(doctors)

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