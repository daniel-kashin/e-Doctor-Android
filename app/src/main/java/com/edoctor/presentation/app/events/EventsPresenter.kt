package com.edoctor.presentation.app.events

import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.presentation.MedicalEventsInfo
import com.edoctor.data.entity.remote.model.record.MedicalEventModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.MedicalRecordsRepository
import com.edoctor.presentation.app.events.EventsPresenter.Event
import com.edoctor.presentation.app.events.EventsPresenter.ViewState
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.nothing
import com.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class EventsPresenter @Inject constructor(
    private val medicalRecordsRepository: MedicalRecordsRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>() {

    var patient: PatientModel? = null

    fun init(patient: PatientModel?) {
        this.patient = patient

        if (patient == null) {
            setViewState(ViewState(MedicalEventsInfo(emptyList(), MedicalEventType.ALL_MEDICAL_EVENT_TYPES)))
        } else {
            setViewState(ViewState(MedicalEventsInfo(emptyList(), emptyList())))
        }

        val getEventsSingle = if (patient == null) {
            medicalRecordsRepository.getMedicalEventsForPatient()
        } else {
            medicalRecordsRepository.getMedicalEventsForDoctor(patient.uuid)
        }

        disposables += getEventsSingle
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(medicalEventsInfo = it) }
            }, {
                // TODO
                nothing()
            })
    }

    fun addOrEditEvent(event: MedicalEventModel) {
        if (patient == null) {
            disposables += medicalRecordsRepository.addOrEditEventForPatient(event)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe({
                    // TODO
                    nothing()
                }, {
                    // TODO
                    nothing()
                })
        }
    }

    fun removeEvent(event: MedicalEventModel) {
        if (patient == null) {
            disposables += medicalRecordsRepository.deleteEventForPatient(event)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe({
                    // TODO
                    nothing()
                }, {
                    // TODO
                    nothing()
                })
        }
    }

    data class ViewState(
        val medicalEventsInfo: MedicalEventsInfo
    ) : Presenter.ViewState

    class Event : Presenter.Event

}