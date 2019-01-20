package com.edoctor.data.repository

import com.edoctor.data.account.SessionManager
import com.edoctor.data.entity.remote.Message
import com.edoctor.data.entity.remote.TextMessage
import com.edoctor.data.remote.api.ChatService
import com.edoctor.utils.javaTimeToUnixTime
import com.edoctor.utils.rx.RxExtensions.justOrEmptyFlowable
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tinder.scarlet.Message.Text
import com.tinder.scarlet.ShutdownReason
import com.tinder.scarlet.WebSocket
import io.reactivex.Flowable
import java.util.*

class ChatRepository(
    val recipientEmail: String,
    val chatService: ChatService,
    val sessionManager: SessionManager
) {

    fun observeEvents(): Flowable<ChatEvent> {
        return chatService.observeEvents()
            .flatMap {
                justOrEmptyFlowable(it.toChatEvent())
            }
    }

    fun sendMessage(message: String) {
        sessionManager.runIfOpened { sessionInfo ->
            chatService.sendMessage(
                TextMessage(
                    UUID.randomUUID().toString(),
                    sessionInfo.profile.email,
                    recipientEmail,
                    System.currentTimeMillis().javaTimeToUnixTime(),
                    message
                )
            )
        }
    }

    fun WebSocket.Event.toChatEvent(): ChatEvent? {
        return when (this) {
            is WebSocket.Event.OnMessageReceived -> {
                sessionManager.runIfOpened { sessionInfo ->
                    val messageString = (this.message as? Text)?.value ?: return null
                    val textMessage = fromJsonSafely(messageString, TextMessage::class.java) ?: return null
                    if (textMessage.senderEmail == sessionInfo.profile.email || textMessage.senderEmail == recipientEmail) {
                        ChatEvent.OnMessageReceived(textMessage)
                    } else {
                        null
                    }
                }
            }
            is WebSocket.Event.OnConnectionClosed -> ChatEvent.OnConnectionClosed(this.shutdownReason)
            is WebSocket.Event.OnConnectionClosing -> ChatEvent.OnConnectionClosing(this.shutdownReason)
            is WebSocket.Event.OnConnectionOpened<*> -> ChatEvent.OnConnectionOpened()
            is WebSocket.Event.OnConnectionFailed -> ChatEvent.OnConnectionFailed(this.throwable)
        }
    }

    sealed class ChatEvent {
        class OnConnectionOpened() : ChatEvent()
        data class OnMessageReceived(val message: Message) : ChatEvent()
        data class OnConnectionClosing(val shutdownReason: ShutdownReason) : ChatEvent()
        data class OnConnectionClosed(val shutdownReason: ShutdownReason) : ChatEvent()
        data class OnConnectionFailed(val throwable: Throwable) : ChatEvent()
    }

    fun <T> fromJsonSafely(json: String, classOfT: Class<T>): T? {
        try {
            return Gson().fromJson(json, classOfT)
        } catch (e: JsonSyntaxException) {
            return null
        }
    }

}