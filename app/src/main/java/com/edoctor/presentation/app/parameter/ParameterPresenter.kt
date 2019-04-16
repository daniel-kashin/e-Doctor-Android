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
    val medicalRecordsRepository: MedicalRecordsRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>() {

    lateinit var parameterType: BodyParameterType
    var patient: PatientModel? = null

    fun init(bodyParameterType: BodyParameterType, patient: PatientModel?) {
        this.parameterType = bodyParameterType
        this.patient = patient

        setViewState(ViewState(emptyList()))

        val getAllParametersSingle = if (patient == null) {
            medicalRecordsRepository.getAllParametersOfTypeForPatient(bodyParameterType)
        } else {
            medicalRecordsRepository.getAllParametersOfTypeForDoctor(bodyParameterType, patient.uuid)
        }

        disposables += getAllParametersSingle
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(parameters = it) }
            }, { throwable ->
                nothing()
            })
    }

    fun addOrEditParameter(parameter: BodyParameterModel) {
        if (patient == null) {
            disposables += medicalRecordsRepository.addOrEditParameterPatient(parameter)
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
        if (patient == null) {
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
        val parameters: List<BodyParameterModel>
    ) : Presenter.ViewState

    class Event : Presenter.Event

}