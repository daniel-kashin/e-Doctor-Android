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

    lateinit var parameterType: BodyParameterType
    lateinit var patient: PatientModel
    var currentUserIsPatient: Boolean = false

    fun init(bodyParameterType: BodyParameterType, patient: PatientModel, currentUserIsPatient: Boolean) {
        this.parameterType = bodyParameterType
        this.patient = patient
        this.currentUserIsPatient = currentUserIsPatient

        setViewState(ViewState(emptyList()))

        val getAllParametersSingle = if (currentUserIsPatient) {
            medicalRecordsRepository.getAllParametersOfTypeForPatient(bodyParameterType, patient.uuid)
        } else {
            medicalRecordsRepository.getAllParametersOfTypeForDoctor(bodyParameterType, patient.uuid)
        }

        disposables += getAllParametersSingle
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(parameters = it.sortedBy { it.timestamp }) }
            }, { throwable ->
                nothing()
            })
    }

    fun addOrEditParameter(parameter: BodyParameterModel) {
        if (currentUserIsPatient) {
            disposables += medicalRecordsRepository.addOrEditParameterForPatient(parameter, patient.uuid)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe({
                    nothing()
                }, {
                    nothing()
                })
        }
    }

    fun deleteParameter(parameter: BodyParameterModel) {
        if (currentUserIsPatient) {
            disposables += medicalRecordsRepository.deleteParameterForPatient(parameter)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe({
                    nothing()
                }, {
                    nothing()
                })
        }
    }

    data class ViewState(
        val parameters: List<BodyParameterModel>
    ) : Presenter.ViewState

    class Event : Presenter.Event

}