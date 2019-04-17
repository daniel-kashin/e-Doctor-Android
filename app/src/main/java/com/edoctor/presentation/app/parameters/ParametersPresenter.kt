package com.edoctor.presentation.app.parameters

import com.edoctor.data.entity.presentation.BodyParameterType
import com.edoctor.data.entity.presentation.LatestBodyParametersInfo
import com.edoctor.data.entity.remote.model.record.BodyParameterModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.MedicalRecordsRepository
import com.edoctor.presentation.app.parameters.ParametersPresenter.Event
import com.edoctor.presentation.app.parameters.ParametersPresenter.ViewState
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.nothing
import com.edoctor.utils.plusAssign
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

    var currentUserIsPatient: Boolean = false
    lateinit var patient: PatientModel

    fun init(patient: PatientModel, currentUserIsPatient: Boolean) {
        this.patient = patient
        this.currentUserIsPatient = currentUserIsPatient

        if (currentUserIsPatient) {
            setViewState(ViewState(LatestBodyParametersInfo(emptyList(), BodyParameterType.NON_CUSTOM_BODY_PARAMETER_TYPES)))
        } else {
            setViewState(ViewState(LatestBodyParametersInfo(emptyList(), emptyList())))
        }

        val getParametersSingle = if (currentUserIsPatient) {
            medicalRecordsRepository.getLatestBodyParametersInfoForPatient(patient.uuid)
        } else {
            medicalRecordsRepository.getLatestBodyParametersInfoForDoctor(patient.uuid)
        }

        disposables += getParametersSingle
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(latestBodyParametersInfo = it) }
            }, { throwable ->
                nothing()
            })
    }

    fun addOrEditParameter(parameter: BodyParameterModel) {
        if (currentUserIsPatient) {
            disposables += medicalRecordsRepository.addOrEditParameterPatient(parameter, patient.uuid)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe({
                    nothing()
                }, {
                    nothing()
                })
        }
    }

    fun removeParameter(parameter: BodyParameterModel) {
        if (currentUserIsPatient) {
            disposables += medicalRecordsRepository.removeParameterForPatient(parameter)
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
        val latestBodyParametersInfo: LatestBodyParametersInfo
    ) : Presenter.ViewState

    class Event : Presenter.Event

}