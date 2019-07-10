package com.infostrategic.edoctor.presentation.app.welcome

import com.infostrategic.edoctor.data.entity.remote.request.LoginDataRequest
import com.infostrategic.edoctor.data.injection.ApplicationModule
import com.infostrategic.edoctor.data.repository.AuthRepository
import com.infostrategic.edoctor.presentation.architecture.presenter.BasePresenter
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter
import com.infostrategic.edoctor.utils.isNoNetworkError
import com.infostrategic.edoctor.utils.plusAssign
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

    fun changeUserType() {
        setViewState { copy(isPatient = !isPatient) }
    }

    fun auth(email: String, password: String) {
        viewStateSnapshot().run {
            if (isLogin) {
                login(email, password, isPatient)
            } else {
                register(email, password, isPatient)
            }
        }
    }

    private fun login(email: String, password: String, isPatient: Boolean) {
        disposables += authRepository
            .login(
                LoginDataRequest(
                    email,
                    password,
                    isPatient
                )
            )
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                sendEvent(Event.AuthSuccessEvent)
            }, {
                if (it.isNoNetworkError()) {
                    sendEvent(Event.NoInternetException)
                } else if (it is HttpException && it.code() == 400) {
                    sendEvent(Event.UserNotFound)
                } else if (it is HttpException && it.code() == 409) {
                    sendEvent(Event.PasswordIsWrong)
                } else {
                    sendEvent(Event.UnknownException)
                }
            })
    }

    private fun register(email: String, password: String, isPatient: Boolean) {
        disposables += authRepository
            .register(
                LoginDataRequest(
                    email,
                    password,
                    isPatient
                )
            )
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                sendEvent(Event.AuthSuccessEvent)
            }, {
                if (it.isNoNetworkError()) {
                    sendEvent(Event.NoInternetException)
                } else if (it is HttpException && it.code() == 409) {
                    sendEvent(Event.UserAlreadyExists)
                } else {
                    sendEvent(Event.UnknownException)
                }
            })
    }

    data class ViewState(val isLogin: Boolean = true, val isPatient: Boolean = true) : Presenter.ViewState

    sealed class Event : Presenter.Event {
        object AuthSuccessEvent : Event()
        object UserNotFound : Event()
        object PasswordIsWrong : Event()
        object UserAlreadyExists : Event()
        object NoInternetException : Event()
        object UnknownException : Event()
        object CloseActivityIntent : Event()
    }

}