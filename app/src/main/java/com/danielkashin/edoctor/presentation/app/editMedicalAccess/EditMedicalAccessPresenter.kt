package com.danielkashin.edoctor.presentation.app.editMedicalAccess

import com.danielkashin.edoctor.data.entity.presentation.MedicalAccessForPatient
import com.danielkashin.edoctor.data.entity.presentation.MedicalAccessesForPatient
import com.danielkashin.edoctor.data.entity.presentation.MedicalRecordType
import com.danielkashin.edoctor.data.injection.ApplicationModule
import com.danielkashin.edoctor.data.repository.MedicalAccessesRepository
import com.danielkashin.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessPresenter.Event
import com.danielkashin.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessPresenter.Event.*
import com.danielkashin.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessPresenter.ViewState
import com.danielkashin.edoctor.presentation.architecture.presenter.BasePresenter
import com.danielkashin.edoctor.presentation.architecture.presenter.Presenter
import com.danielkashin.edoctor.utils.SessionExceptionHelper.isSessionException
import com.danielkashin.edoctor.utils.disposableDelegate
import com.danielkashin.edoctor.utils.isNoNetworkError
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class EditMedicalAccessPresenter @Inject constructor(
    private val medicalAccessesRepository: MedicalAccessesRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>() {

    lateinit var medicalAccessForPatient: MedicalAccessForPatient
    lateinit var allTypes: List<MedicalRecordType>

    private var changeMedicalRecordTypesDisposable by disposableDelegate

    fun init(medicalAccessForPatient: MedicalAccessForPatient, allTypes: List<MedicalRecordType>) {
        this.medicalAccessForPatient = medicalAccessForPatient
        this.allTypes = allTypes
    }

    fun onMedicalRecordTypesPicked(medicalRecordTypes: List<MedicalRecordType>) {
        val medicalAccessForPatient = medicalAccessForPatient.copy(availableTypes = medicalRecordTypes)
        val medicalAccessesForPatient = MedicalAccessesForPatient(listOf(medicalAccessForPatient), allTypes)

        changeMedicalRecordTypesDisposable = medicalAccessesRepository
            .postMedicalAccessesForPatient(medicalAccessesForPatient)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                this.medicalAccessForPatient = medicalAccessForPatient
                sendEvent(MedicalRecordTypesChanged)
            }, {
                when {
                    it.isSessionException() -> sendEvent(ShowSessionException)
                    it.isNoNetworkError() -> sendEvent(ShowNoNetworkException)
                    else -> sendEvent(ShowUnknownException(it))
                }
            })
    }

    override fun destroy() {
        changeMedicalRecordTypesDisposable = null
        super.destroy()
    }

    class ViewState : Presenter.ViewState

    sealed class Event : Presenter.Event {
        class ShowUnknownException(val throwable: Throwable) : Event()
        object ShowSessionException : Event()
        object ShowNoNetworkException : Event()
        object MedicalRecordTypesChanged : Event()
    }

}