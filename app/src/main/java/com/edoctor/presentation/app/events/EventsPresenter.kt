package com.edoctor.presentation.app.events

import com.edoctor.data.entity.presentation.MedicalEventType
import com.edoctor.data.entity.presentation.MedicalEventsInfo
import com.edoctor.data.entity.remote.model.record.MedicalEventModel
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.MedicalRecordsRepository
import com.edoctor.presentation.app.events.EventsPresenter.Event
import com.edoctor.presentation.app.events.EventsPresenter.ViewState
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.nothing
import com.edoctor.utils.plusAssign
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import java.lang.IllegalStateException
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
    var doctor: DoctorModel? = null
    var isRequestedRecords: Boolean = false
    var addOrEditEventAction: ((MedicalEventModel) -> Single<MedicalEventModel>)? = null
    var deleteEventAction: ((MedicalEventModel) -> Completable)? = null

    val canBeModified: Boolean
        get() = addOrEditEventAction != null && deleteEventAction != null

    fun init(patient: PatientModel?, doctor: DoctorModel?, isRequestedRecords: Boolean) {
        this.patient = patient
        this.doctor = doctor
        this.isRequestedRecords = isRequestedRecords

        val (initialViewState, getEventsSingle) = if (isRequestedRecords) {
            when {
                doctor != null -> {
                    val viewState = ViewState(MedicalEventsInfo(emptyList(), emptyList()))
                    val single = medicalRecordsRepository.getRequestedMedicalEventsForPatient(doctor.uuid)
                    viewState to single
                }
                patient != null -> TODO()
                else -> throw IllegalStateException("")
            }
        } else {
            when {
                patient != null -> {
                    val viewState = ViewState(MedicalEventsInfo(emptyList(), emptyList()))
                    val single = medicalRecordsRepository.getMedicalEventsForDoctor(patient.uuid)
                    viewState to single
                }
                else -> {
                    addOrEditEventAction = { event -> medicalRecordsRepository.addOrEditEventForPatient(event) }
                    deleteEventAction = { event -> medicalRecordsRepository.deleteEventForPatient(event) }
                    val viewState = ViewState(MedicalEventsInfo(emptyList(), MedicalEventType.ALL_MEDICAL_EVENT_TYPES))
                    val single = medicalRecordsRepository.getMedicalEventsForPatient()
                    viewState to single
                }
            }
        }

        setViewState(initialViewState)

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
        addOrEditEventAction?.let {
            disposables += it.invoke(event)
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

    fun deleteEvent(event: MedicalEventModel) {
        deleteEventAction?.let {
            disposables += it.invoke(event)
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