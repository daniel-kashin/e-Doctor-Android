package com.edoctor.presentation.app.findDoctor

import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.FindDoctorRepository
import com.edoctor.presentation.app.findDoctor.FindDoctorPresenter.ViewState.*
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.SessionExceptionHelper.isSessionException
import com.edoctor.utils.isNoNetworkError
import com.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class FindDoctorPresenter @Inject constructor(
    val findDoctorRepository: FindDoctorRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<FindDoctorPresenter.ViewState, FindDoctorPresenter.Event>() {

    init {
        setViewState(EmptySearchViewState)
    }

    fun onSearchTyped(textToSearch: String) {
        if (textToSearch.isEmpty()) {
            setViewState(EmptySearchViewState)
        } else {
            disposables += findDoctorRepository.findDoctors(textToSearch)
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
                .doOnSubscribe { setViewState(LoadingViewState) }
                .subscribe(
                    { setViewState(DoctorsViewState(it)) },
                    {
                        when {
                            it.isSessionException() -> sendEvent(Event.ShowSessionException)
                            it.isNoNetworkError() -> setViewState(NetworkExceptionViewState)
                            else -> setViewState(UnknownExceptionViewState)
                        }
                    })
        }
    }

    sealed class ViewState : Presenter.ViewState {
        object EmptySearchViewState : ViewState()
        data class DoctorsViewState(val doctors: List<DoctorModel>) : ViewState()
        object LoadingViewState : ViewState()

        object UnknownExceptionViewState : ViewState()
        object NetworkExceptionViewState : ViewState()
    }

    sealed class Event : Presenter.Event {
        object ShowSessionException : Event()
    }

}