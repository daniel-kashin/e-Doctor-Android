package com.edoctor.presentation.app.presenter.welcome

import com.bookmate.app.base.BasePresenter
import com.edoctor.data.entity.remote.LoginData
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.AuthRepository
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.isNoNetworkError
import com.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

class WelcomePresenter @Inject constructor(
    val authRepository: AuthRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    val observeScheduler: Scheduler?,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    val subscribeScheduler: Scheduler?
) : BasePresenter<WelcomePresenter.ViewState, WelcomePresenter.Event>("WelcomePresenter") {

    fun init() {
        setViewState(ViewState())
    }

    fun changeAuthType() {
        setViewState { copy(isLogin = !isLogin) }
    }

    fun auth(email: String, password: String) {
        if (viewStateSnapshot().isLogin) {
            login(email, password)
        } else {
            register(email, password)
        }
    }

    private fun login(email: String, password: String) {
        disposables += authRepository.login(LoginData(email, password))
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                sendEvent(Event.AuthSuccessEvent)
            }, {
                if (it.isNoNetworkError()) {
                    sendEvent(Event.NoInternetExceptionEvent)
                } else if (it is HttpException && it.code() == 400) {
                    sendEvent(Event.UserNotFound)
                } else if (it is HttpException && it.code() == 409) {
                    sendEvent(Event.PasswordIsWrong)
                } else {
                    sendEvent(Event.UnknownExceptionEvent)
                }
            })
    }

    private fun register(email: String, password: String) {
        disposables += authRepository.register(LoginData(email, password))
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                sendEvent(Event.AuthSuccessEvent)
            }, {
                if (it.isNoNetworkError()) {
                    sendEvent(Event.NoInternetExceptionEvent)
                } else if (it is HttpException && it.code() == 409) {
                    sendEvent(Event.UserAlreadyExists)
                } else {
                    sendEvent(Event.UnknownExceptionEvent)
                }
            })
    }

    data class ViewState(val isLogin: Boolean = true) : Presenter.ViewState

    sealed class Event : Presenter.Event {
        object AuthSuccessEvent : Event()
        object UserNotFound : Event()
        object PasswordIsWrong : Event()
        object UserAlreadyExists : Event()
        object NoInternetExceptionEvent : Event()
        object UnknownExceptionEvent : Event()
    }

}