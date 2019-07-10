package com.infostrategic.edoctor.presentation.app.patient

import com.infostrategic.edoctor.data.entity.presentation.MedicalAccessForDoctor
import com.infostrategic.edoctor.data.entity.remote.model.record.MedicalEventModel
import com.infostrategic.edoctor.data.entity.remote.model.user.PatientModel
import com.infostrategic.edoctor.data.injection.ApplicationModule
import com.infostrategic.edoctor.data.repository.MedicalAccessesRepository
import com.infostrategic.edoctor.data.repository.MedicalRecordsRepository
import com.infostrategic.edoctor.presentation.app.patient.PatientPresenter.Event
import com.infostrategic.edoctor.presentation.app.patient.PatientPresenter.ViewState
import com.infostrategic.edoctor.presentation.architecture.presenter.BasePresenter
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter
import com.infostrategic.edoctor.utils.disposableDelegate
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.zipWith
import javax.inject.Inject
import javax.inject.Named

class PatientPresenter @Inject constructor(
    private val medicalRecordsRepository: MedicalRecordsRepository,
    private val medicalAccessesRepository: MedicalAccessesRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>("DoctorPresenter") {

    private var updateDisposable by disposableDelegate

    lateinit var patient: PatientModel

    init {
        setViewState(ViewState(null))
    }

    fun init(patient: PatientModel) {
        this.patient = patient
    }

    fun updatePatientInfo() {
        updateDisposable = medicalAccessesRepository.getMedicalAccessForDoctor(patient.uuid)
            .zipWith(medicalRecordsRepository.getRequestedMedicalEventsForDoctor(patient.uuid))
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(medcardInfo = it.first to it.second.medicalEvents) }
            }, {
                sendEvent(Event.ShowUnhandledEventException)
            })
    }

    data class ViewState(
        val medcardInfo: Pair<MedicalAccessForDoctor, List<MedicalEventModel>>?
    ) : Presenter.ViewState

    sealed class Event : Presenter.Event {
        object ShowUnhandledEventException : Event()
    }

}