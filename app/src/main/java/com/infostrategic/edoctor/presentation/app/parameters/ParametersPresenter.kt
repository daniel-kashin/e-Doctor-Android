package com.infostrategic.edoctor.presentation.app.parameters

import com.infostrategic.edoctor.data.entity.presentation.BodyParameterType
import com.infostrategic.edoctor.data.entity.presentation.LatestBodyParametersInfo
import com.infostrategic.edoctor.data.entity.remote.model.record.BodyParameterModel
import com.infostrategic.edoctor.data.entity.remote.model.user.PatientModel
import com.infostrategic.edoctor.data.injection.ApplicationModule
import com.infostrategic.edoctor.data.repository.MedicalRecordsRepository
import com.infostrategic.edoctor.presentation.app.parameters.ParametersPresenter.Event
import com.infostrategic.edoctor.presentation.app.parameters.ParametersPresenter.ViewState
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter
import com.infostrategic.edoctor.utils.SessionExceptionHelper.isSessionException
import com.infostrategic.edoctor.utils.disposableDelegate
import com.infostrategic.edoctor.utils.isNoNetworkError
import com.infostrategic.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ParametersPresenter @Inject constructor(
    private val medicalRecordsRepository: MedicalRecordsRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : Presenter<ViewState, Event>() {

    private var updateDisposable by disposableDelegate

    var currentUserIsPatient: Boolean = false
    lateinit var patient: PatientModel

    fun init(patient: PatientModel, currentUserIsPatient: Boolean) {
        this.patient = patient
        this.currentUserIsPatient = currentUserIsPatient

        if (currentUserIsPatient) {
            setViewState(
                ViewState(
                    LatestBodyParametersInfo(
                        emptyList(),
                        BodyParameterType.NON_CUSTOM_BODY_PARAMETER_TYPES,
                        false
                    ),
                    true,
                    false
                )
            )
        } else {
            setViewState(ViewState(LatestBodyParametersInfo(emptyList(), emptyList(), false), true, false))
        }
    }

    fun updateAllParameters() {
        val getParametersSingle = if (currentUserIsPatient) {
            medicalRecordsRepository.getLatestBodyParametersInfoForPatient(patient.uuid)
        } else {
            medicalRecordsRepository.getLatestBodyParametersInfoForDoctor(patient.uuid)
        }

        updateDisposable = getParametersSingle
            .doOnSubscribe { setViewState { copy(isLoading = true) } }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                if (currentUserIsPatient && !it.isSynchronized) {
                    sendEvent(Event.ShowNotSynchronizedEvent)
                }
                setViewState { copy(latestBodyParametersInfo = it, wasLoaded = true, isLoading = false) }
            }, {
                when {
                    it.isSessionException() -> sendEvent(Event.ShowSessionException)
                    it.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                    else -> sendEvent(Event.ShowUnhandledErrorEvent)
                }
            })
    }

    fun addOrEditParameter(parameter: BodyParameterModel) {
        if (currentUserIsPatient) {
            disposables += medicalRecordsRepository.addOrEditParameterForPatient(parameter, patient.uuid)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe({
                    updateAllParameters()
                }, {
                    sendEvent(Event.ShowUnhandledErrorEvent)
                })
        }
    }

    fun removeParameter(parameter: BodyParameterModel) {
        if (currentUserIsPatient) {
            disposables += medicalRecordsRepository.deleteParameterForPatient(parameter)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe({
                    updateAllParameters()
                }, {
                    sendEvent(Event.ShowUnhandledErrorEvent)
                })
        }
    }

    override fun destroy() {
        updateDisposable = null
        super.destroy()
    }

    data class ViewState(
        val latestBodyParametersInfo: LatestBodyParametersInfo,
        val isLoading: Boolean,
        val wasLoaded: Boolean
    ) : Presenter.ViewState

    sealed class Event : Presenter.Event {
        object ShowNotSynchronizedEvent : Event()
        object ShowUnhandledErrorEvent : Event()
        object ShowNoNetworkException : Event()
        object ShowSessionException : Event()
    }

}