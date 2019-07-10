package com.danielkashin.edoctor.presentation.app.medicalAccessesForDoctor

import com.danielkashin.edoctor.data.entity.presentation.MedicalAccessesForDoctor
import com.danielkashin.edoctor.data.injection.ApplicationModule
import com.danielkashin.edoctor.data.repository.MedicalAccessesRepository
import com.danielkashin.edoctor.presentation.app.medicalAccessesForDoctor.MedicalAccessesForDoctorPresenter.Event
import com.danielkashin.edoctor.presentation.app.medicalAccessesForDoctor.MedicalAccessesForDoctorPresenter.ViewState
import com.danielkashin.edoctor.presentation.architecture.presenter.BasePresenter
import com.danielkashin.edoctor.presentation.architecture.presenter.Presenter
import com.danielkashin.edoctor.utils.SessionExceptionHelper.isSessionException
import com.danielkashin.edoctor.utils.disposableDelegate
import com.danielkashin.edoctor.utils.isNoNetworkError
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class MedicalAccessesForDoctorPresenter @Inject constructor(
    private val medicalAccessesRepository: MedicalAccessesRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>("MedicalAccessesForDoctorPresenter") {

    private var loadMedicalAccessesDisposable by disposableDelegate

    init {
        setViewState(ViewState.LoadingViewState)
        loadMedicalAccesses()
    }

    fun loadMedicalAccesses() {
        loadMedicalAccessesDisposable = medicalAccessesRepository.getMedicalAccessesForDoctor()
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
        loadMedicalAccessesDisposable = null
        super.destroy()
    }

    sealed class ViewState : Presenter.ViewState {
        data class MedicalAccessesViewState(
            val medicalAccessesForDoctor: MedicalAccessesForDoctor
        ) : ViewState()

        object LoadingViewState : ViewState()

        object UnknownExceptionViewState : ViewState()

        object NetworkExceptionViewState : ViewState()
    }

    sealed class Event : Presenter.Event {
        object ShowSessionException : Event()
    }

}