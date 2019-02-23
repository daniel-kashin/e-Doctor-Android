package com.edoctor.presentation.app.conversations

import com.edoctor.data.entity.presentation.Conversation
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.ConversationsRepository
import com.edoctor.presentation.app.conversations.ConversationsPresenter.Event
import com.edoctor.presentation.app.conversations.ConversationsPresenter.ViewState
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.SessionExceptionHelper.isSessionException
import com.edoctor.utils.isNoNetworkError
import com.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ConversationsPresenter @Inject constructor(
    private val conversationsRepository: ConversationsRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ViewState, Event>() {

    init {
        setViewState(ViewState.EMPTY)

        onReloadConversations()
    }

    fun onReloadConversations() {
        disposables += conversationsRepository.getConversations()
            .doOnSubscribe { setViewState { copy(isLoading = true) } }
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe({
                setViewState { copy(conversations = it, isLoading = false) }
            }, {
                setViewState { copy(isLoading = false) }
                when {
                    it.isSessionException() -> sendEvent(Event.ShowSessionException)
                    it.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                    else -> sendEvent(Event.ShowUnknownException(it))
                }
            })
    }

    data class ViewState(val conversations: List<Conversation>, val isLoading: Boolean) : Presenter.ViewState {
        companion object {
            val EMPTY = ViewState(emptyList(), true)
        }
    }

    sealed class Event : Presenter.Event {
        class ShowUnknownException(val throwable: Throwable) : Event()
        object ShowSessionException : Event()
        object ShowNoNetworkException : Event()
    }

}