package com.danielkashin.edoctor.presentation.app.findDoctor

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.danielkashin.edoctor.R
import com.danielkashin.edoctor.data.entity.remote.model.user.DoctorModel
import com.danielkashin.edoctor.data.entity.remote.model.user.PatientModel
import com.danielkashin.edoctor.data.injection.ApplicationComponent
import com.danielkashin.edoctor.data.injection.FindDoctorModule
import com.danielkashin.edoctor.presentation.app.doctor.DoctorActivity
import com.danielkashin.edoctor.presentation.app.findDoctor.FindDoctorPresenter.Event
import com.danielkashin.edoctor.presentation.app.findDoctor.FindDoctorPresenter.Event.ShowSessionException
import com.danielkashin.edoctor.presentation.app.findDoctor.FindDoctorPresenter.ViewState
import com.danielkashin.edoctor.presentation.architecture.fragment.BaseFragment
import com.danielkashin.edoctor.utils.SessionExceptionHelper.onSessionException
import com.danielkashin.edoctor.utils.SimpleDividerItemDecoration
import com.danielkashin.edoctor.utils.invisible
import com.danielkashin.edoctor.utils.session
import com.jakewharton.rxbinding3.widget.afterTextChangeEvents
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@SuppressLint("CheckResult")
class FindDoctorFragment : BaseFragment<FindDoctorPresenter, ViewState, Event>("FindDoctorFragment") {

    @Inject
    override lateinit var presenter: FindDoctorPresenter

    override val layoutRes: Int = R.layout.fragment_find_doctor

    private lateinit var toolbarSearch: EditText
    private lateinit var iconClear: ImageView
    private lateinit var tryAgain: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorMessage: TextView
    private lateinit var recyclerView: RecyclerView

    private lateinit var doctorsAdapter: FindDoctorAdapter

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.plus(FindDoctorModule()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.run {
            toolbarSearch = findViewById(R.id.toolbar_search)
            iconClear = findViewById(R.id.icon_clear)
            tryAgain = findViewById(R.id.try_again)
            progressBar = findViewById(R.id.progress_bar)
            errorMessage = findViewById(R.id.error_message)
            recyclerView = findViewById(R.id.recycler_view)

            tryAgain.invisible()
            progressBar.invisible()
            errorMessage.invisible()
            recyclerView.invisible()

            initializeRecyclerView(context)

            initializeToolbar()
        }
    }

    override fun render(viewState: ViewState) {
        when (viewState) {
            is ViewState.LoadingViewState -> showLoading()
            is ViewState.UnknownExceptionViewState -> showUnhandledException()
            is ViewState.NetworkExceptionViewState -> showNetworkUnavailable()
            is ViewState.DoctorsViewState -> viewState.doctors.let {
                if (it.isEmpty()) showEmptyDoctors() else showDoctors(viewState.doctors)
            }
            is ViewState.EmptySearchViewState -> showEmptySearch()
        }
    }

    override fun showEvent(event: Event) {
        if (event is ShowSessionException) activity?.onSessionException()
    }

    private fun initializeRecyclerView(context: Context) {
        doctorsAdapter = FindDoctorAdapter().apply {
            onDoctorClickListener = { doctor ->
                context.session.runIfOpened { userInfo ->
                    DoctorActivity.IntentBuilder(context)
                        .doctor(doctor)
                        .patient(userInfo as? PatientModel)
                        .start()
                }
            }
        }
        recyclerView.adapter = doctorsAdapter
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(context))
        recyclerView.layoutManager = LinearLayoutManager(context, VERTICAL, false)
    }

    private fun initializeToolbar() {
        toolbarSearch.afterTextChangeEvents()
            .map { event -> event.editable.toString().trim() }
            .doOnNext { string ->
                if (string.isEmpty()) {
                    presenter.onSearchTyped(string)
                }
            }
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { string ->
                if (!string.isEmpty()) {
                    presenter.onSearchTyped(string)
                }
            }

        iconClear.setOnClickListener {
            toolbarSearch.text = null
        }
    }

    private fun showEmptyDoctors() {
        errorMessage.text = getString(R.string.nothing_was_found)
        tryAgain.setOnClickListener {
            presenter.onSearchTyped(toolbarSearch.text.toString())
        }

        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.INVISIBLE
        errorMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun showEmptySearch() {
        errorMessage.text = getString(R.string.type_search_excursions)

        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.INVISIBLE
        errorMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun showDoctors(doctors: List<DoctorModel>) {
        doctorsAdapter.setDoctors(doctors)

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
            presenter.onSearchTyped(toolbarSearch.text.toString())
        }

        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.VISIBLE
        errorMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

    private fun showNetworkUnavailable() {
        errorMessage.text = getString(R.string.network_error_message)
        tryAgain.setOnClickListener {
            presenter.onSearchTyped(toolbarSearch.text.toString())
        }
        progressBar.visibility = View.INVISIBLE
        tryAgain.visibility = View.VISIBLE
        errorMessage.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
    }

}