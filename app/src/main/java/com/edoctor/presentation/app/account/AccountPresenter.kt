package com.edoctor.presentation.app.account

import com.edoctor.data.entity.remote.result.UserResult
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

        disposables += accountRepository.getCurrentAccount()
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(account = it, isLoading = false) }
            }, { throwable ->
                setViewState { copy(isLoading = false) }
                if (throwable.isSessionException()) {
                    sendEvent(Event.ShowSessionException)
                } else if (throwable.isNoNetworkError()) {
                    // TODO
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

    data class ViewState(val account: UserResult?, val isLoading: Boolean) : Presenter.ViewState {
        companion object {
            val EMPTY = ViewState(null, true)
        }
    }

    sealed class Event : Presenter.Event {
        object ShowSessionException : Event()
    }

}