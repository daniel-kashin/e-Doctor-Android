package com.edoctor.presentation.app.presenter.chat

import com.bookmate.app.base.BasePresenter
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.ChatRepository
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.plusAssign
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named

class ChatPresenter @Inject constructor(
    val chatRepository: ChatRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    val subscribeScheduler: Scheduler
) : BasePresenter<ChatPresenter.ViewState, ChatPresenter.Event>() {


    fun init() {
        setViewState(ViewState(listOf()))

        disposables += chatRepository
            .observeEvents()
            .subscribeOn(observeScheduler)
            .observeOn(subscribeScheduler)
            .subscribe(
                { onEventReceived(it) },
                { sendEvent(Event.ShowChatError(it)) }
            )
    }

    fun sendMessage(message: String) {
        chatRepository.sendMessage(message)
    }

    private fun onEventReceived(event: WebSocket.Event) {
        if (event is WebSocket.Event.OnMessageReceived) {
            (event.message as? Message.Text)?.let {
                setViewState { copy(messages + it.value) }
            }
        }
    }

    data class ViewState(
        val messages: List<String>
    ) : Presenter.ViewState

    sealed class Event : Presenter.Event {
        class ShowChatError(val throwable: Throwable) : Event()
    }

}