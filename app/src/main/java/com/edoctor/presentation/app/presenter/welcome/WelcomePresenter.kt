package com.edoctor.presentation.app.presenter.welcome

import com.bookmate.app.base.BasePresenter
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.remote.entity.LoginData
import com.edoctor.data.repository.AuthRepository
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.isNoNetworkError
import com.edoctor.utils.plusAssign
import com.tinder.scarlet.ShutdownReason.Companion.GRACEFUL
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class WelcomePresenter @Inject constructor(
    val authRepository: AuthRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    val observeScheduler: Scheduler?,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    val subscribeScheduler: Scheduler?
) : BasePresenter<WelcomePresenter.ViewState, WelcomePresenter.Event>("WelcomePresenter") {

    fun login(email: String, password: String) {

    }

    fun register(email: String, password: String) {
        disposables += authRepository.register(LoginData(email, password))
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                sendEvent(Event.AuthSuccessEvent)
            }, {
                if (it.isNoNetworkError()) {
                    sendEvent(Event.NoInternetExceptionEvent)
                } else {
                    sendEvent(Event.UnknownExceptionEvent)
                }
            })

        GRACEFUL
    }

    class ViewState : Presenter.ViewState {

    }

    sealed class Event : Presenter.Event {
        object AuthSuccessEvent : Event()
        object NoInternetExceptionEvent : Event()
        object UnknownExceptionEvent : Event()
    }

}