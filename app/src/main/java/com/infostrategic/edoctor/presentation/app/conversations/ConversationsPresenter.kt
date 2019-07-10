package com.infostrategic.edoctor.presentation.app.conversations

import com.infostrategic.edoctor.data.entity.presentation.Conversation
import com.infostrategic.edoctor.data.injection.ApplicationModule
import com.infostrategic.edoctor.data.repository.ConversationsRepository
import com.infostrategic.edoctor.presentation.app.conversations.ConversationsPresenter.Event
import com.infostrategic.edoctor.presentation.app.conversations.ConversationsPresenter.ViewState
import com.infostrategic.edoctor.presentation.architecture.presenter.BasePresenter
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter
import com.infostrategic.edoctor.utils.SessionExceptionHelper.isSessionException
import com.infostrategic.edoctor.utils.isNoNetworkError
import com.infostrategic.edoctor.utils.nothing
import com.infostrategic.edoctor.utils.plusAssign
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
            .subscribe(
                { (conversations, throwable) ->
                    when {
                        throwable == null -> nothing()
                        throwable.isSessionException() -> sendEvent(Event.ShowSessionException)
                        throwable.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                        else -> sendEvent(Event.ShowUnknownException(throwable))
                    }
                    setViewState { copy(conversations = conversations, isLoading = false) }
                },
                {
                    setViewState { copy(isLoading = false) }
                    when {
                        it.isSessionException() -> sendEvent(Event.ShowSessionException)
                        it.isNoNetworkError() -> sendEvent(Event.ShowNoNetworkException)
                        else -> sendEvent(Event.ShowUnknownException(it))
                    }
                }
            )
    }

    data class ViewState(
        val conversations: List<Conversation>?,
        val isLoading: Boolean
    ) : Presenter.ViewState {
        companion object {
            val EMPTY = ViewState(null, true)
        }
    }

    sealed class Event : Presenter.Event {
        class ShowUnknownException(val throwable: Throwable) : Event()
        object ShowSessionException : Event()
        object ShowNoNetworkException : Event()
    }

}