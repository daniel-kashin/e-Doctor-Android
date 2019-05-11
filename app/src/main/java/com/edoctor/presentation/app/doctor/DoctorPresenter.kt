package com.edoctor.presentation.app.doctor

import com.edoctor.data.entity.presentation.MedicalAccessInfo
import com.edoctor.data.entity.remote.model.record.MedicalEventModel
import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.MedicalAccessesRepository
import com.edoctor.data.repository.MedicalRecordsRepository
import com.edoctor.presentation.app.doctor.DoctorPresenter.Event
import com.edoctor.presentation.app.doctor.DoctorPresenter.ViewState
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.disposableDelegate
import com.edoctor.utils.nothing
import com.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.zipWith
import javax.inject.Inject
import javax.inject.Named

class DoctorPresenter @Inject constructor(
    private val medicalAccessesRepository: MedicalAccessesRepository,
    private val medicalRecordsRepository: MedicalRecordsRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>("DoctorPresenter") {

    private var updateDisposable by disposableDelegate

    lateinit var doctor: DoctorModel
    lateinit var patient: PatientModel

    init {
        setViewState(ViewState(null))
    }

    fun init(doctor: DoctorModel, patient: PatientModel) {
        this.doctor = doctor
        this.patient = patient
    }

    fun updateDoctorInfo() {
        updateDisposable = medicalAccessesRepository.getMedicalAccessForPatient(doctor.uuid)
            .zipWith(medicalRecordsRepository.getRequestedMedicalEventsForPatient(doctor.uuid, patient.uuid))
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(medcardInfo = it.first to it.second.medicalEvents) }
            }, {
                nothing()
            })
    }

    override fun destroy() {
        updateDisposable = null
        super.destroy()
    }

    data class ViewState(
        val medcardInfo: Pair<MedicalAccessInfo, List<MedicalEventModel>>?
    ) : Presenter.ViewState

    sealed class Event : Presenter.Event {
        object ShowUnhandledErrorEvent : Event()
    }

}