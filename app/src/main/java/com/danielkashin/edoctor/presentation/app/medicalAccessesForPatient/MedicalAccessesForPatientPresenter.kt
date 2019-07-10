package com.danielkashin.edoctor.presentation.app.medicalAccessesForPatient

import com.danielkashin.edoctor.data.entity.presentation.MedicalAccessForPatient
import com.danielkashin.edoctor.data.entity.presentation.MedicalAccessesForPatient
import com.danielkashin.edoctor.data.entity.remote.model.user.DoctorModel
import com.danielkashin.edoctor.data.injection.ApplicationModule
import com.danielkashin.edoctor.data.repository.MedicalAccessesRepository
import com.danielkashin.edoctor.presentation.app.medicalAccessesForPatient.MedicalAccessesForPatientPresenter.Event
import com.danielkashin.edoctor.presentation.app.medicalAccessesForPatient.MedicalAccessesForPatientPresenter.ViewState
import com.danielkashin.edoctor.utils.SessionExceptionHelper.isSessionException
import com.danielkashin.edoctor.presentation.architecture.presenter.BasePresenter
import com.danielkashin.edoctor.presentation.architecture.presenter.Presenter
import com.danielkashin.edoctor.utils.disposableDelegate
import com.danielkashin.edoctor.utils.isNoNetworkError
import com.danielkashin.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class MedicalAccessesForPatientPresenter @Inject constructor(
    private val medicalAccessesRepository: MedicalAccessesRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>("MedicalAccessesForPatientPresenter") {

    private var loadMedicalAccessesDisposable by disposableDelegate

    init {
        setViewState(ViewState.LoadingViewState)
        loadMedicalAccesses()
    }

    fun loadMedicalAccesses() {
        loadMedicalAccessesDisposable = medicalAccessesRepository.getMedicalAccessesForPatient()
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

    fun deleteMedicalAccess(doctor: DoctorModel) {
        val emptyDoctorAccesses = MedicalAccessesForPatient(listOf(MedicalAccessForPatient(doctor, emptyList())), emptyList())

        disposables += medicalAccessesRepository.postMedicalAccessesForPatient(emptyDoctorAccesses)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                (viewStateSnapshot() as? ViewState.MedicalAccessesViewState)?.let { viewState ->
                    val medicalAccessesWithoutDoctor = viewState.medicalAccessesForPatient.copy(
                        medicalAccesses = viewState.medicalAccessesForPatient.medicalAccesses.filter {
                            it.doctor.uuid != doctor.uuid
                        }
                    )
                    setViewState(ViewState.MedicalAccessesViewState(medicalAccessesWithoutDoctor))
                }
            }, {
                when {
                    it.isSessionException() -> sendEvent(Event.ShowSessionException)
                    it.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                    else -> sendEvent(Event.ShowUnknownException(it))
                }
            })
    }

    override fun destroy() {
        loadMedicalAccessesDisposable = null
        super.destroy()
    }

    sealed class ViewState : Presenter.ViewState {
        data class MedicalAccessesViewState(
            val medicalAccessesForPatient: MedicalAccessesForPatient
        ) : ViewState()

        object LoadingViewState : ViewState()

        object UnknownExceptionViewState : ViewState()

        object NetworkExceptionViewState : ViewState()
    }

    sealed class Event : Presenter.Event {
        object ShowSessionException : Event()
        class ShowUnknownException(val throwable: Throwable) : Event()
        object ShowNoNetworkException : Event()
    }

}