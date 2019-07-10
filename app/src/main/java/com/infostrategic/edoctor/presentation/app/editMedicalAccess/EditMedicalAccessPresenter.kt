package com.infostrategic.edoctor.presentation.app.editMedicalAccess

import com.infostrategic.edoctor.data.entity.presentation.MedicalAccessForPatient
import com.infostrategic.edoctor.data.entity.presentation.MedicalAccessesForPatient
import com.infostrategic.edoctor.data.entity.presentation.MedicalRecordType
import com.infostrategic.edoctor.data.injection.ApplicationModule
import com.infostrategic.edoctor.data.repository.MedicalAccessesRepository
import com.infostrategic.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessPresenter.Event
import com.infostrategic.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessPresenter.Event.*
import com.infostrategic.edoctor.presentation.app.editMedicalAccess.EditMedicalAccessPresenter.ViewState
import com.infostrategic.edoctor.presentation.architecture.presenter.BasePresenter
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter
import com.infostrategic.edoctor.utils.SessionExceptionHelper.isSessionException
import com.infostrategic.edoctor.utils.disposableDelegate
import com.infostrategic.edoctor.utils.isNoNetworkError
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