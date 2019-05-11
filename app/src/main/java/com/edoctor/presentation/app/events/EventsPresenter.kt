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
import com.edoctor.utils.SessionExceptionHelper.isSessionException
import com.edoctor.utils.disposableDelegate
import com.edoctor.utils.isNoNetworkError
import com.edoctor.utils.plusAssign
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named

class EventsPresenter @Inject constructor(
    private val medicalRecordsRepository: MedicalRecordsRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>() {

    private var updateDisposable by disposableDelegate

    private var addOrEditEventAction: ((MedicalEventModel) -> Single<MedicalEventModel>)? = null
    private var deleteEventAction: ((MedicalEventModel) -> Completable)? = null
    private lateinit var getEventsSingleAction: (() -> Single<MedicalEventsInfo>)

    var isRequestedRecords: Boolean = false
    var currentUserIsPatient: Boolean = false
    private lateinit var patient: PatientModel
    var doctor: DoctorModel? = null

    val canBeAdded: Boolean get() = addOrEditEventAction != null
    var canBeEdited: Boolean = false

    fun init(patient: PatientModel, doctor: DoctorModel?, currentUserIsPatient: Boolean, isRequestedRecords: Boolean) {
        this.patient = patient
        this.doctor = doctor
        this.currentUserIsPatient = currentUserIsPatient
        this.isRequestedRecords = isRequestedRecords

        val (initialViewState, getEventsSingle) = when {
            currentUserIsPatient && isRequestedRecords -> {
                canBeEdited = true
                addOrEditEventAction = { event ->
                    medicalRecordsRepository.addOrEditEventForPatient(event.getAddedFromDoctorCopy(), patient.uuid)
                }
                deleteEventAction = { event -> medicalRecordsRepository.deleteEventForPatient(event) }
                val viewState = ViewState(MedicalEventsInfo(emptyList(), emptyList(), false), true, false)
                val single = medicalRecordsRepository.getRequestedMedicalEventsForPatient(doctor!!.uuid, patient.uuid)
                viewState to single
            }
            !currentUserIsPatient && isRequestedRecords -> {
                addOrEditEventAction = { event ->
                    medicalRecordsRepository.addMedicalEventForDoctor(event, patient.uuid)
                }
                val viewState = ViewState(MedicalEventsInfo(emptyList(), MedicalEventType.ALL_MEDICAL_EVENT_TYPES, false), true, false)
                val single = medicalRecordsRepository.getRequestedMedicalEventsForDoctor(patient.uuid)
                viewState to single
            }
            currentUserIsPatient && !isRequestedRecords -> {
                canBeEdited = true
                addOrEditEventAction =
                        { event -> medicalRecordsRepository.addOrEditEventForPatient(event, patient.uuid) }
                deleteEventAction = { event -> medicalRecordsRepository.deleteEventForPatient(event) }
                val viewState = ViewState(MedicalEventsInfo(emptyList(), MedicalEventType.ALL_MEDICAL_EVENT_TYPES, false), true, false)
                val single = medicalRecordsRepository.getMedicalEventsForPatient(patient.uuid)
                viewState to single
            }
            else -> {
                val viewState = ViewState(MedicalEventsInfo(emptyList(), emptyList(), false), true, false)
                val single = medicalRecordsRepository.getMedicalEventsForDoctor(patient.uuid)
                viewState to single
            }
        }

        setViewState(initialViewState)

        getEventsSingleAction = { getEventsSingle }
    }

    fun updateAllEvents() {
        updateDisposable = getEventsSingleAction()
            .doOnSubscribe { setViewState { copy(isLoading = true) } }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                if (currentUserIsPatient && !it.isSynchronized) {
                    sendEvent(Event.ShowNotSynchronizedEvent)
                }
                setViewState { copy(medicalEventsInfo = it, wasLoaded = true, isLoading = false) }
            }, {
                setViewState { copy(isLoading = false) }
                when {
                    it.isSessionException() -> sendEvent(Event.ShowSessionException)
                    it.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                    else -> sendEvent(Event.ShowUnhandledErrorEvent)
                }
            })
    }

    fun addOrEditEvent(event: MedicalEventModel) {
        addOrEditEventAction?.let {
            disposables += it.invoke(event)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe({
                    updateAllEvents()
                }, {
                    sendEvent(Event.ShowUnhandledErrorEvent)
                })
        }
    }

    fun deleteEvent(event: MedicalEventModel) {
        deleteEventAction?.let {
            disposables += it.invoke(event)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .subscribe({
                    updateAllEvents()
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
        val medicalEventsInfo: MedicalEventsInfo,
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