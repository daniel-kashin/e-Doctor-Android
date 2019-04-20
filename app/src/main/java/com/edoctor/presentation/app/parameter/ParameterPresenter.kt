package com.edoctor.presentation.app.parameter

import com.edoctor.data.entity.remote.model.record.BodyParameterModel
import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.MedicalRecordsRepository
import com.edoctor.presentation.app.parameter.ParameterPresenter.Event
import com.edoctor.presentation.app.parameter.ParameterPresenter.ViewState
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.disposableDelegate
import com.edoctor.utils.nothing
import com.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ParameterPresenter @Inject constructor(
    private val medicalRecordsRepository: MedicalRecordsRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>() {

    private var updateDisposable by disposableDelegate

    lateinit var parameterType: BodyParameterType
    lateinit var patient: PatientModel
    var currentUserIsPatient: Boolean = false

    fun init(bodyParameterType: BodyParameterType, patient: PatientModel, currentUserIsPatient: Boolean) {
        this.parameterType = bodyParameterType
        this.patient = patient
        this.currentUserIsPatient = currentUserIsPatient

        setViewState(ViewState(emptyList()))

        updateAllParameters()
    }

    private fun updateAllParameters() {
        val getAllParametersSingle = if (currentUserIsPatient) {
            medicalRecordsRepository.getAllParametersOfTypeForPatient(parameterType, patient.uuid)
        } else {
            medicalRecordsRepository.getAllParametersOfTypeForDoctor(parameterType, patient.uuid)
        }

        updateDisposable = getAllParametersSingle
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({ parameters ->
                setViewState { copy(parameters = parameters) }
            }, {
                sendEvent(Event.ShowUnhandledErrorEvent)
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

    fun deleteParameter(parameter: BodyParameterModel) {
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
        val parameters: List<BodyParameterModel>
    ) : Presenter.ViewState

    sealed class Event : Presenter.Event {
        object ShowUnhandledErrorEvent : Event()
    }

}