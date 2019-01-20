package com.edoctor.presentation.app.presenter.chat

import com.bookmate.app.base.BasePresenter
import com.edoctor.data.entity.remote.TextMessage
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.ChatRepository
import com.edoctor.presentation.architecture.presenter.Presenter
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

    lateinit var senderEmail: String

    fun init(senderEmail: String) {
        this.senderEmail = senderEmail

        setViewState(ViewState())

        disposables += chatRepository
            .observeEvents()
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .subscribe(
                { onEventReceived(it) },
                { sendEvent(Event.ShowChatError(it)) }
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
                setViewState { copy(messages = (messages + it).sortedByDescending { it.sendingTimestamp } ) }
            }
        }
    }

    data class ViewState(
        val messages: List<TextMessage> = listOf()
    ) : Presenter.ViewState

    sealed class Event : Presenter.Event {
        class ShowChatError(val throwable: Throwable) : Event()
    }

}