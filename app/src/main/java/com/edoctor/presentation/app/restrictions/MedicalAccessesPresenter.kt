package com.edoctor.presentation.app.restrictions

import com.edoctor.data.entity.remote.model.medicalAccess.MedicalAccessesForPatientModel
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.MedicalAccessesRepository
import com.edoctor.presentation.app.findDoctor.FindDoctorPresenter
import com.edoctor.presentation.app.restrictions.MedicalAccessesPresenter.Event
import com.edoctor.presentation.app.restrictions.MedicalAccessesPresenter.ViewState
import com.edoctor.utils.SessionExceptionHelper.isSessionException
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.disposableDelegate
import com.edoctor.utils.isNoNetworkError
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class MedicalAccessesPresenter @Inject constructor(
    private val medicalAccessesRepository: MedicalAccessesRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>("MedicalAccessesPresenter") {

    private var loadRestrictionsDisposable by disposableDelegate

    init {
        setViewState(ViewState.LoadingViewState)
        loadMedicalAccesses()
    }

    fun loadMedicalAccesses() {
        loadRestrictionsDisposable = medicalAccessesRepository.getMedicalAccessesForPatient()
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .doOnSubscribe { setViewState(ViewState.LoadingViewState) }
            .subscribe(
                {
                    setViewState(ViewState.MedicalAccessesViewState(it))
                },
                {
                    when {
                        it.isSessionException() -> sendEvent(Event.ShowSessionException)
                        it.isNoNetworkError() -> setViewState(ViewState.NetworkExceptionViewState)
                        else -> setViewState(ViewState.UnknownExceptionViewState)
                    }
                })
    }

    override fun destroy() {
        loadRestrictionsDisposable = null
        super.destroy()
    }

    sealed class ViewState : Presenter.ViewState {
        data class MedicalAccessesViewState(
            val medicalAccessesForPatientModel: MedicalAccessesForPatientModel
        ) : ViewState()

        object LoadingViewState : ViewState()

        object UnknownExceptionViewState : ViewState()

        object NetworkExceptionViewState : ViewState()
    }

    sealed class Event : Presenter.Event {
        object ShowSessionException : Event()
    }

}