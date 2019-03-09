package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.CallActionRequest
import com.edoctor.data.entity.presentation.Message
import com.edoctor.data.entity.remote.request.MessageRequestWrapper
import com.edoctor.data.entity.remote.request.TextMessageRequest
import com.edoctor.data.entity.remote.response.MessageResponseWrapper
import com.edoctor.data.mapper.MessageMapper.toNetwork
import com.edoctor.data.mapper.MessageMapper.toPresentation
import com.edoctor.data.remote.api.ChatApi
import com.edoctor.data.remote.api.ChatService
import com.edoctor.utils.rx.RxExtensions.justOrEmptyFlowable
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tinder.scarlet.Message.Text
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.WebSocketEvent
import io.reactivex.Flowable
import io.reactivex.Single

class ChatRepository(
    private val currentUserEmail: String,
    private val recipientEmail: String,
    private val chatApi: ChatApi,
    private val chatService: ChatService
) {

    var onStartConnectionListener: (() -> Unit)? = null
    var onCloseConnectionListener: (() -> Unit)? = null

    fun observeEvents(): Flowable<ChatEvent> {
        return chatService.observeEvents()
            .doOnSubscribe { onStartConnectionListener?.invoke() }
            .doOnCancel { onCloseConnectionListener?.invoke() }
            .flatMap { justOrEmptyFlowable(it.toChatEvent()) }
    }

    fun getMessages(fromTimestamp: Long): Single<List<Message>> {
        return chatApi.getMessages(fromTimestamp, recipientEmail)
            .map { toPresentation(it) }
    }

    fun sendMessage(message: String) {
        chatService.sendMessage(
            MessageRequestWrapper(
                textMessageRequest = TextMessageRequest(message)
            )
        )
    }

    fun sendCallStatusRequest(callActionRequest: CallActionRequest) {
        chatService.sendMessage(
            MessageRequestWrapper(
                callActionMessageRequest = toNetwork(callActionRequest)
            )
        )
    }

    private fun WebSocketEvent.toChatEvent(): ChatEvent? {
        return when (this) {
            is WebSocketEvent.OnMessageReceived -> {
                val messageString = (this.message as? Text)?.value ?: return null
                val textMessage = fromJsonSafely(messageString, MessageResponseWrapper::class.java)
                    ?.let { toPresentation(it) }
                    ?: return null

                ChatEvent.OnMessageReceived(textMessage)
            }
            is WebSocketEvent.OnConnectionClosed -> ChatEvent.OnConnectionClosed(this.shutdownReason)
            is WebSocketEvent.OnConnectionClosing -> ChatEvent.OnConnectionClosing(this.shutdownReason)
            is WebSocketEvent.OnConnectionOpened -> ChatEvent.OnConnectionOpened
            is WebSocketEvent.OnConnectionFailed -> ChatEvent.OnConnectionFailed(this.throwable)
        }
    }


    sealed class ChatEvent {
        object OnConnectionOpened : ChatEvent()
        data class OnMessageReceived(val message: Message) : ChatEvent()
        data class OnConnectionClosing(val shutdownReason: ShutdownReason) : ChatEvent()
        data class OnConnectionClosed(val shutdownReason: ShutdownReason) : ChatEvent()
        data class OnConnectionFailed(val throwable: Throwable) : ChatEvent()
    }

    private fun <T> fromJsonSafely(json: String, classOfT: Class<T>): T? {
        return try {
            Gson().fromJson(json, classOfT)
        } catch (e: JsonSyntaxException) {
            null
        }
    }

}