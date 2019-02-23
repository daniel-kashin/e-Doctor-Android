package com.edoctor.presentation.app.chat

import com.edoctor.data.entity.remote.TextMessage
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.ChatRepository
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.SessionExceptionHelper.isSessionException
import com.edoctor.utils.plusAssign
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ChatPresenter @Inject constructor(
    private val chatRepository: ChatRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ChatPresenter.ViewState, ChatPresenter.Event>() {

    lateinit var currentUserEmail: String

    fun init(currentUserEmail: String) {
        this.currentUserEmail = currentUserEmail

        setViewState(ViewState())

        disposables += chatRepository
            .observeEvents()
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe(
                {
                    onEventReceived(it)
                },
                {
                    if (it.isSessionException()) {
                        sendEvent(Event.ShowSessionException)
                    } else {
                        sendEvent(Event.ShowException(it))
                    }
                }
            )
    }

    fun sendMessage(message: String) {
        chatRepository.sendMessage(message)
    }

    override fun destroy() {
        chatRepository.dispose()
        super.destroy()
    }

    private fun onEventReceived(event: ChatRepository.ChatEvent) {
        if (event is ChatRepository.ChatEvent.OnMessageReceived) {
            (event.message as? TextMessage)?.let {
                setViewState { copy(messages = (messages + it).sortedByDescending { it.sendingTimestamp }) }
            }
        }
    }

    data class ViewState(
        val messages: List<TextMessage> = listOf()
    ) : Presenter.ViewState

    sealed class Event : Presenter.Event {
        class ShowException(val throwable: Throwable) : Event()
        object ShowSessionException : Event()
    }

}