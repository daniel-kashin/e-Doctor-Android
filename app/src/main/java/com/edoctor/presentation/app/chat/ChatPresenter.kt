package com.edoctor.presentation.app.chat

import com.edoctor.data.entity.presentation.CallAction
import com.edoctor.data.entity.presentation.CallStatusMessage
import com.edoctor.data.entity.presentation.Message
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

    private var connectivityDisposable by disposableDelegate
    private var currentMessagesDisposable by disposableDelegate
    private var getMessagesDisposable by disposableDelegate

    private var lastMessageUpdateTimestamp: Long = -1

    fun init(currentUserEmail: String, recipientEmail: String) {
        this.currentUserEmail = currentUserEmail
        this.recipientEmail = recipientEmail

        setViewState(ViewState())
    }

    fun openConnection() {
        setWaitingForConnectionState()

        connectivityDisposable = ConnectivityNotifier.asObservable.toV2()
            .subscribe { isConnected ->
                currentMessagesDisposable = if (isConnected) {
                    chatRepository
                        .observeEvents()
                        .subscribeOn(subscribeScheduler)
                        .observeOn(observeScheduler)
                        .doOnSubscribe { setWaitingForConnectionState() }
                        .doOnCancel { setWaitingForConnectionState() }
                        .doOnError { setWaitingForConnectionState() }
                        .subscribe(this::onEventReceived, this::handleException)
                } else {
                    null
                }
            }
    }

    fun closeConnection() {
        connectivityDisposable = null
        currentMessagesDisposable = null
        getMessagesDisposable = null
    }

    fun sendMessage(message: String): Boolean {
        return if (viewStateSnapshot().messagesStatus != MessagesStatus.WAITING_FOR_CONNECTION) {
            chatRepository.sendMessage(message)
            true
        } else {
            sendEvent(Event.ShowNetworkException)
            false
        }
    }

    fun initiateCall() {
        if (viewStateSnapshot().messagesStatus != MessagesStatus.WAITING_FOR_CONNECTION) {
            chatRepository.sendCallStatusRequest(CallAction.ENTER)
        } else {
            sendEvent(Event.ShowNetworkException)
        }
    }

    fun acceptCall() {
        if (viewStateSnapshot().messagesStatus != MessagesStatus.WAITING_FOR_CONNECTION) {
            chatRepository.sendCallStatusRequest(CallAction.ENTER)
        } else {
            sendEvent(Event.ShowNetworkException)
        }
    }

    fun leaveCall() {
        if (viewStateSnapshot().messagesStatus != MessagesStatus.WAITING_FOR_CONNECTION) {
            chatRepository.sendCallStatusRequest(CallAction.LEAVE)
        } else {
            sendEvent(Event.ShowNetworkException)
        }
    }


    override fun destroy() {
        super.destroy()
        closeConnection()
    }

    private fun updateMessages() {
        val startMessageUpdateTimestamp = currentUnixTime()

        getMessagesDisposable = chatRepository.getMessages(lastMessageUpdateTimestamp)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .doOnSubscribe { setViewState { copy(messagesStatus = MessagesStatus.UPDATING) } }
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
    }

    private fun setWaitingForConnectionState() {
        getMessagesDisposable = null
        setViewState { copy(messagesStatus = MessagesStatus.WAITING_FOR_CONNECTION) }
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
            is ChatRepository.ChatEvent.OnConnectionOpened -> {
                updateMessages()
            }
            is ChatRepository.ChatEvent.OnConnectionClosing -> {
                setWaitingForConnectionState()
            }
            is ChatRepository.ChatEvent.OnConnectionFailed -> {
                setWaitingForConnectionState()
            }
            is ChatRepository.ChatEvent.OnMessageReceived -> {
                setViewState {
                    copy(messages = messages.addWithSorting(listOf((event.message))))
                }
            }
        }
    }

    private fun List<Message>.addWithSorting(newMessages: List<Message>): List<Message> {
        return asSequence()
            .plus(newMessages)
            .asSequence()
            .distinctBy { it.uuid }
            .sortedByDescending { it.sendingTimestamp }
            .toList()
    }

    data class ViewState(
        val messagesStatus: MessagesStatus = MessagesStatus.WAITING_FOR_CONNECTION,
        val messages: List<Message> = listOf(),
        val callStatusMessage: CallStatusMessage? = null
    ) : Presenter.ViewState

    enum class MessagesStatus {
        WAITING_FOR_CONNECTION,
        UPDATING,
        UP_TO_DATE
    }

    sealed class Event : Presenter.Event {
        class ShowException(val throwable: Throwable) : Event()
        object ShowNetworkException : Event()
        object ShowSessionException : Event()
    }

}