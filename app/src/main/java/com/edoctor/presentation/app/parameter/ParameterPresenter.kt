package com.edoctor.presentation.app.parameter

import com.edoctor.data.entity.remote.model.record.BodyParameterModel
import com.edoctor.data.entity.presentation.BodyParameterType
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

    fun init(bodyParameterType: BodyParameterType) {
        this.parameterType = bodyParameterType

        setViewState(ViewState(emptyList()))

        disposables += medicalRecordsRepository.getAllParametersOfType(bodyParameterType)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(parameters = it) }
            }, { throwable ->
                // TODO
                nothing()
            })
    }

    fun addOrEditParameter(parameter: BodyParameterModel) {
        disposables += medicalRecordsRepository.addOrEditParameter(parameter)
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

    fun removeParameter(parameter: BodyParameterModel) {
        disposables += medicalRecordsRepository.removeParameter(parameter)
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

    data class ViewState(
        val parameters: List<BodyParameterModel>
    ) : Presenter.ViewState

    class Event : Presenter.Event

}