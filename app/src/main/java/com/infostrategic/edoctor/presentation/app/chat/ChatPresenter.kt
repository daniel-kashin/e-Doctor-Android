package com.infostrategic.edoctor.presentation.app.chat

import android.graphics.Bitmap
import com.infostrategic.edoctor.data.entity.presentation.CallActionRequest
import com.infostrategic.edoctor.data.entity.presentation.CallActionRequest.CallAction.*
import com.infostrategic.edoctor.data.entity.presentation.CallStatusMessage
import com.infostrategic.edoctor.data.entity.presentation.Message
import com.infostrategic.edoctor.data.entity.remote.model.user.UserModel
import com.infostrategic.edoctor.data.injection.ApplicationModule
import com.infostrategic.edoctor.data.repository.ChatRepository
import com.infostrategic.edoctor.presentation.architecture.presenter.BasePresenter
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter
import com.infostrategic.edoctor.utils.*
import com.infostrategic.edoctor.utils.SessionExceptionHelper.isSessionException
import com.infostrategic.edoctor.utils.rx.toV2
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class ChatPresenter @Inject constructor(
    private val chatRepository: ChatRepository,
    @Named(ApplicationModule.MAIN_THREAD_SCHEDULER)
    private val observeScheduler: Scheduler,
    @Named(ApplicationModule.IO_THREAD_SCHEDULER)
    private val subscribeScheduler: Scheduler
) : BasePresenter<ChatPresenter.ViewState, ChatPresenter.Event>() {

    companion object {
        private fun randomTempFileName() = "${UUID.randomUUID()}.tmp"
    }

    lateinit var currentUser: UserModel
    lateinit var recipientUser: UserModel

    private var connectivityDisposable by disposableDelegate
    private var currentMessagesDisposable by disposableDelegate
    private var getMessagesDisposable by disposableDelegate

    private var lastMessageUpdateTimestamp: Long = -1

    fun init(currentUser: UserModel, recipientUser: UserModel) {
        this.currentUser = currentUser
        this.recipientUser = recipientUser

        setViewState(ViewState())
    }

    fun onImageSelected(bitmap: Bitmap, cacheDirectory: File) {
        if (viewStateSnapshot().messagesStatus != MessagesStatus.WAITING_FOR_CONNECTION) {
            disposables += Single
                .fromCallable {
                    bitmap.writeToFile(File(cacheDirectory, randomTempFileName()))
                }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { sendEvent(Event.ShowImageUploadStart) }
                .flatMapCompletable { chatRepository.sendImage(it) }
                .observeOn(observeScheduler)
                .subscribe(
                    { nothing() },
                    { sendEvent(Event.ShowImageUploadException) }
                )
        } else {
            sendEvent(Event.ShowNetworkException)
        }
    }

    fun openConnection() {
        setWaitingForConnectionState()

        connectivityDisposable = ConnectivityNotifier.asObservable.toV2()
            .subscribe { isConnected ->
                if (isConnected) {
                    currentMessagesDisposable = chatRepository
                        .observeEvents()
                        .subscribeOn(subscribeScheduler)
                        .observeOn(observeScheduler)
                        .doOnSubscribe { setWaitingForConnectionState() }
                        .doOnCancel { setWaitingForConnectionState() }
                        .doOnError { setWaitingForConnectionState() }
                        .subscribe(this::onEventReceived, this::handleException)
                } else {
                    currentMessagesDisposable = null

                    if (viewStateSnapshot().messages.isEmpty()) {
                        updateMessages(false)
                    }
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

    fun initiateCall(): Unit = viewStateSnapshot().run {
        if (messagesStatus != MessagesStatus.WAITING_FOR_CONNECTION) {
            chatRepository.sendCallStatusRequest(CallActionRequest(INITIATE, ""))
        } else {
            sendEvent(Event.ShowNetworkException)
        }
    }

    fun acceptCall(): Unit = viewStateSnapshot().run {
        if (messagesStatus != MessagesStatus.WAITING_FOR_CONNECTION && callStatusMessage != null) {
            chatRepository.sendCallStatusRequest(CallActionRequest(ENTER, callStatusMessage.callUuid))
        } else {
            sendEvent(Event.ShowNetworkException)
        }
    }

    fun leaveCall(): Unit = viewStateSnapshot().run {
        if (messagesStatus != MessagesStatus.WAITING_FOR_CONNECTION && callStatusMessage != null) {
            chatRepository.sendCallStatusRequest(CallActionRequest(LEAVE, callStatusMessage.callUuid))
        }
    }

    private fun updateMessages(isConnected: Boolean) {
        val startMessageUpdateTimestamp = currentUnixTime()

        getMessagesDisposable = chatRepository.getMessages(lastMessageUpdateTimestamp, !isConnected)
            .subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
            .doOnSubscribe {
                if (isConnected) {
                    setViewState { copy(messagesStatus = MessagesStatus.UPDATING) }
                }
            }
            .subscribe(
                { (newMessages, fromRemote) ->
                    lastMessageUpdateTimestamp = startMessageUpdateTimestamp
                    setViewState {
                        copy(
                            messages = messages.addWithSorting(newMessages),
                            messagesStatus = if (fromRemote) MessagesStatus.UP_TO_DATE else MessagesStatus.WAITING_FOR_CONNECTION
                        )
                    }
                },
                { handleException(it) }
            )
    }

    private fun setWaitingForConnectionState() {
        getMessagesDisposable = null
        setViewState { copy(messagesStatus = MessagesStatus.WAITING_FOR_CONNECTION, callStatusMessage = null) }
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
                updateMessages(true)
            }
            is ChatRepository.ChatEvent.OnConnectionClosing -> {
                setWaitingForConnectionState()
            }
            is ChatRepository.ChatEvent.OnConnectionFailed -> {
                setWaitingForConnectionState()
            }
            is ChatRepository.ChatEvent.OnMessageReceived -> {
                event.message.let { message ->
                    setViewState {
                        copy(
                            messages = if (message.shouldBeShown()) messages.addWithSorting(listOf((message))) else messages,
                            callStatusMessage = if (message is CallStatusMessage) message else callStatusMessage
                        )
                    }
                }
            }
        }
    }

    private fun List<Message>.addWithSorting(newMessages: List<Message>): List<Message> {
        return asSequence()
            .plus(newMessages)
            .asSequence()
            .distinctBy { it.uuid }
            .filter { it.shouldBeShown() }
            .sortedByDescending { it.sendingTimestamp }
            .toList()
    }

    private fun Message.shouldBeShown(): Boolean {
        return !(this is CallStatusMessage && this.callStatus == CallStatusMessage.CallStatus.STARTED)
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
        object ShowImageUploadException : Event()
        object ShowImageUploadSuccess : Event()
        object ShowImageUploadStart : Event()
    }

}