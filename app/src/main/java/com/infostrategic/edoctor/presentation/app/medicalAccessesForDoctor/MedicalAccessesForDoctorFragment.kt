package com.infostrategic.edoctor.presentation.app.medicalAccessesForDoctor

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.infostrategic.edoctor.R
import com.infostrategic.edoctor.data.entity.presentation.MedicalAccessesForDoctor
import com.infostrategic.edoctor.data.injection.ApplicationComponent
import com.infostrategic.edoctor.presentation.app.medicalAccessesForDoctor.MedicalAccessesForDoctorPresenter.Event
import com.infostrategic.edoctor.presentation.app.medicalAccessesForDoctor.MedicalAccessesForDoctorPresenter.ViewState
import com.infostrategic.edoctor.presentation.app.patient.PatientActivity
import com.infostrategic.edoctor.presentation.architecture.fragment.BaseFragment
import com.infostrategic.edoctor.utils.SessionExceptionHelper.onSessionException
import com.infostrategic.edoctor.utils.SimpleDividerItemDecoration
import com.infostrategic.edoctor.utils.invisible
import javax.inject.Inject

class MedicalAccessesForDoctorFragment :
    BaseFragment<MedicalAccessesForDoctorPresenter, ViewState, Event>("MedicalAccessesForDoctorFragment") {

    @Inject
    override lateinit var presenter: MedicalAccessesForDoctorPresenter

    override val layoutRes: Int = R.layout.fragment_medical_accesses_for_doctor

    private lateinit var tryAgain: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: MedicalAccessesForDoctorAdapter

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
        adapter = MedicalAccessesForDoctorAdapter().apply {
            onMedicalAccessForDoctorClickListener = {
                PatientActivity.IntentBuilder(context)
                    .patient(it.patient)
                    .start()
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
            is ViewState.MedicalAccessesViewState -> viewState.medicalAccessesForDoctor.let {
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
        }
    }

    private fun showEmptyMedicalAccesses() {
        errorMessage.text = getString(R.string.empty_medical_accesses_for_doctor)
        tryAgain.setOnClickListener {
            presenter.loadMedicalAccesses()
        }

        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.INVISIBLE
        errorMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun showMedicalAccesses(medicalAccesses: MedicalAccessesForDoctor) {
        adapter.medicalAccessesForDoctor = medicalAccesses

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