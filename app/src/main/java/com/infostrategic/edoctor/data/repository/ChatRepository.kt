package com.infostrategic.edoctor.data.repository

import com.infostrategic.edoctor.data.entity.presentation.CallActionRequest
import com.infostrategic.edoctor.data.entity.presentation.Message
import com.infostrategic.edoctor.data.entity.remote.model.user.UserModel
import com.infostrategic.edoctor.data.entity.remote.request.MessageRequestWrapper
import com.infostrategic.edoctor.data.entity.remote.request.TextMessageRequest
import com.infostrategic.edoctor.data.entity.remote.response.MessageResponseWrapper
import com.infostrategic.edoctor.data.local.message.MessagesLocalStore
import com.infostrategic.edoctor.data.mapper.MessageMapper
import com.infostrategic.edoctor.data.remote.rest.ChatRestApi
import com.infostrategic.edoctor.data.remote.socket.ChatSocketApi
import com.infostrategic.edoctor.utils.asImageBodyPart
import com.infostrategic.edoctor.utils.rx.RxExtensions.justOrEmptyFlowable
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tinder.scarlet.Message.Text
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.WebSocketEvent
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.io.File

class ChatRepository(
    private val currentUser: UserModel,
    private val recipientUser: UserModel,
    private val chatRestApi: ChatRestApi,
    private val chatSocketApi: ChatSocketApi,
    private val messagesLocalStore: MessagesLocalStore,
    private val messageMapper: MessageMapper
) {

    var onStartConnectionListener: (() -> Unit)? = null
    var onCloseConnectionListener: (() -> Unit)? = null

    fun observeEvents(): Flowable<ChatEvent> {
        return chatSocketApi.observeEvents()
            .doOnSubscribe { onStartConnectionListener?.invoke() }
            .doOnCancel { onCloseConnectionListener?.invoke() }
            .flatMap { event ->
                val chatEvent = event.toChatEvent()
                (chatEvent as? ChatEvent.OnMessageReceived)
                    ?.let { messageMapper.toLocalFromPresentation(it.message) }
                    ?.let { messagesLocalStore.saveBlocking(it) }
                justOrEmptyFlowable(chatEvent)
            }
    }

    fun getMessages(
        fromTimestamp: Long,
        onlyFromLocal: Boolean
    ): Single<Pair<List<Message>, Boolean>> {
        return messagesLocalStore.getConversationMessages(fromTimestamp, currentUser.uuid, recipientUser.uuid)
            .map { localMessages ->
                localMessages
                    .asSequence()
                    .mapNotNull { messageMapper.toPresentationFromLocal(it, currentUser) }
                    .sortedBy { it.sendingTimestamp }
                    .toList()
            }
            .flatMap { presentationLocalMessages ->
                if (onlyFromLocal) {
                    Single.just(presentationLocalMessages to false)
                } else {
                    chatRestApi
                        .getMessages(
                            presentationLocalMessages.lastOrNull()?.sendingTimestamp ?: fromTimestamp,
                            recipientUser.uuid
                        )
                        .flatMap { remoteMessages ->
                            val presentationRemoteMessages = messageMapper
                                .toPresentationFromNetwork(remoteMessages, currentUser)
                                .sortedBy { it.sendingTimestamp }

                            val remoteMessagesToSave = presentationRemoteMessages.mapNotNull {
                                messageMapper.toLocalFromPresentation(it)
                            }

                            messagesLocalStore.save(remoteMessagesToSave)
                                .map { (presentationLocalMessages + presentationRemoteMessages) to true }
                        }
                        .onErrorReturnItem(presentationLocalMessages to false)
                }
            }
    }

    fun sendMessage(message: String) {
        chatSocketApi.sendMessage(
            MessageRequestWrapper(
                textMessageRequest = TextMessageRequest(message)
            )
        )
    }

    fun sendImage(imageFile: File?): Completable {
        return chatRestApi.sendImage(recipientUser.uuid, imageFile?.asImageBodyPart("image"))
    }

    fun sendCallStatusRequest(callActionRequest: CallActionRequest) {
        chatSocketApi.sendMessage(
            MessageRequestWrapper(
                callActionMessageRequest = messageMapper.toNetworkFromPresentation(callActionRequest)
            )
        )
    }

    private fun WebSocketEvent.toChatEvent(): ChatEvent? {
        return when (this) {
            is WebSocketEvent.OnMessageReceived -> {
                val messageString = (this.message as? Text)?.value ?: return null
                val textMessage = fromJsonSafely(messageString, MessageResponseWrapper::class.java)
                    ?.let { messageMapper.toPresentationFromNetwork(it, currentUser) }
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