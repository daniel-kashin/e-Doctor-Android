package com.edoctor.presentation.app.patient

import com.edoctor.data.entity.presentation.MedicalAccessForDoctor
import com.edoctor.data.entity.presentation.MedicalAccessForPatient
import com.edoctor.data.entity.presentation.MedicalRecordType
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.MedicalAccessesRepository
import com.edoctor.presentation.app.patient.PatientPresenter.Event
import com.edoctor.presentation.app.patient.PatientPresenter.ViewState
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.nothing
import com.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class PatientPresenter @Inject constructor(
    private val medicalAccessesRepository: MedicalAccessesRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>("DoctorPresenter") {

    lateinit var patient: PatientModel

    init {
        setViewState(ViewState(null))
    }

    fun init(patient: PatientModel) {
        this.patient = patient

        disposables += medicalAccessesRepository.getMedicalAccessForDoctor(patient.uuid)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(medicalAccessForDoctor = it) }
            }, {
                nothing()
            })
    }

    data class ViewState(
        val medicalAccessForDoctor: MedicalAccessForDoctor?
    ) : Presenter.ViewState

    class Event : Presenter.Event

}