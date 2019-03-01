package com.edoctor.presentation.app.chat

import com.edoctor.data.entity.remote.TextMessage
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.repository.ChatRepository
import com.edoctor.presentation.architecture.presenter.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.isSessionException
import com.edoctor.utils.rx.toV2
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
    lateinit var recipientEmail: String

    private var currentMessagesDisposable by disposableDelegate
    private var getMessagesDisposable by disposableDelegate

    private var lastMessageUpdateTimestamp: Long = -1

    fun init(currentUserEmail: String, recipientEmail: String) {
        this.currentUserEmail = currentUserEmail
        this.recipientEmail = recipientEmail

        setViewState(ViewState(MessagesStatus.WAITING_FOR_CONNECTION))

        disposables += ConnectivityNotifier.asObservable.toV2()
            .subscribe { isConnected ->
                if (!isConnected) {
                    setViewState { copy(messagesStatus = MessagesStatus.WAITING_FOR_CONNECTION) }
                }

                currentMessagesDisposable = if (isConnected) {
                    chatRepository
                        .observeEvents()
                        .subscribeOn(subscribeScheduler)
                        .observeOn(observeScheduler)
                        .subscribe(this::onEventReceived, this::handleException)
                } else {
                    null
                }

                getMessagesDisposable = if (isConnected) {
                    val startMessageUpdateTimestamp = currentUnixTime()

                    chatRepository
                        .getMessages(lastMessageUpdateTimestamp - 10)
                        .doOnSubscribe { setViewState { copy(messagesStatus = MessagesStatus.UPDATING) } }
                        .subscribeOn(subscribeScheduler)
                        .observeOn(observeScheduler)
                        .subscribe({ newMessages ->
                            lastMessageUpdateTimestamp = startMessageUpdateTimestamp
                            setViewState {
                                copy(
                                    messages = messages.addWithSorting(newMessages),
                                    messagesStatus = MessagesStatus.UP_TO_DATE
                                )
                            }
                        }, {
                            handleException(it)
                        })
                } else {
                    null
                }
            }
    }

    fun sendMessage(message: String): Boolean {
        return if (viewStateSnapshot().messagesStatus != MessagesStatus.WAITING_FOR_CONNECTION) {
            chatRepository.sendMessage(message)
            true
        } else {
            false
        }
    }

    override fun destroy() {
        super.destroy()
        getMessagesDisposable = null
        currentMessagesDisposable = null
    }

    private fun handleException(throwable: Throwable) {
        when {
            throwable.isSessionException() -> sendEvent(Event.ShowSessionException)
            throwable.isNoNetworkError() -> nothing()
            else -> sendEvent(Event.ShowException(throwable))
        }
    }

    private fun onEventReceived(event: ChatRepository.ChatEvent) {
        when (event) {
            is ChatRepository.ChatEvent.OnMessageReceived -> (event.message as? TextMessage)?.let { message ->
                setViewState {
                    copy(messages = messages.addWithSorting(listOf(message)))
                }
            }
        }
    }

    private fun List<TextMessage>.addWithSorting(newMessages: List<TextMessage>): List<TextMessage> {
        return asSequence()
            .plus(newMessages)
            .asSequence()
            .distinctBy { it.uuid }
            .sortedByDescending { it.sendingTimestamp }
            .toList()
    }


    data class ViewState(
        val messagesStatus: MessagesStatus,
        val messages: List<TextMessage> = listOf()
    ) : Presenter.ViewState

    enum class MessagesStatus {
        WAITING_FOR_CONNECTION,
        UPDATING,
        UP_TO_DATE
    }

    sealed class Event : Presenter.Event {
        class ShowException(val throwable: Throwable) : Event()
        object ShowSessionException : Event()
    }

}