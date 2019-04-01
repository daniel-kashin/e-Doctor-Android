package com.edoctor.presentation.app.account

import com.edoctor.data.entity.remote.response.DoctorResponse
import com.edoctor.data.entity.remote.response.PatientResponse
import com.edoctor.data.entity.remote.response.UserResponse
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.AccountRepository
import com.edoctor.data.repository.AuthRepository
import com.edoctor.presentation.app.account.AccountPresenter.Event
import com.edoctor.presentation.app.account.AccountPresenter.ViewState
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.SessionExceptionHelper.isSessionException
import com.edoctor.utils.isNoNetworkError
import com.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class AccountPresenter @Inject constructor(
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>() {

    init {
        setViewState(ViewState.EMPTY)

        refreshAccount()
    }

    fun refreshAccount() {
        disposables += accountRepository.getCurrentAccount(refresh = false)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .doOnSubscribe { setViewState { copy(isLoading = true) } }
            .doOnSuccess { setViewState { copy(account = it) } }

            .flatMap {
                accountRepository.getCurrentAccount(refresh = true)
                    .subscribeOn(subscribeScheduler)
                    .observeOn(observeScheduler)
            }
            .subscribe({
                setViewState { copy(account = it, isLoading = false) }
            }, { throwable ->
                setViewState { copy(isLoading = false) }
                when {
                    throwable.isSessionException() -> sendEvent(Event.ShowSessionException)
                    throwable.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                }
            })
    }

    fun updateAccount(fullName: String, city: String) {
        val viewState = viewStateSnapshot()
        val oldAccount = viewState.account

        val newAccount = when (oldAccount) {
            is PatientResponse -> oldAccount.copy(fullName = fullName, city = city)
            is DoctorResponse -> oldAccount.copy(fullName = fullName, city = city)
            else -> return
        }

        disposables += accountRepository.updateAccount(newAccount)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .doOnSubscribe { setViewState { copy(account = newAccount, isLoading = true) } }
            .subscribe({
                setViewState { copy(account = it, isLoading = false) }
            }, { throwable ->
                setViewState { copy(account = oldAccount, isLoading = false) }
                when {
                    throwable.isSessionException() -> sendEvent(Event.ShowSessionException)
                    throwable.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                }
            })
    }

    fun logOut() {
        disposables += authRepository.logOut()
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe {
                sendEvent(Event.ShowSessionException)
            }
    }

    data class ViewState(val account: UserResponse?, val isLoading: Boolean) : Presenter.ViewState {
        companion object {
            val EMPTY = ViewState(null, true)
        }
    }

    sealed class Event : Presenter.Event {
        object ShowSessionException : Event()
        object ShowNoNetworkException : Event()
    }

}