package com.danielkashin.edoctor.data.repository

import com.danielkashin.edoctor.data.entity.presentation.Conversation
import com.danielkashin.edoctor.data.entity.remote.model.user.UserModel
import com.danielkashin.edoctor.data.local.message.MessagesLocalStore
import com.danielkashin.edoctor.data.mapper.MessageMapper
import com.danielkashin.edoctor.data.remote.rest.ConversationsRestApi
import io.reactivex.Single

class ConversationsRepository(
    private val currentUser: UserModel,
    private val api: ConversationsRestApi,
    private val messageMapper: MessageMapper,
    private val messagesLocalStore: MessagesLocalStore
) {

    fun getConversations(): Single<Pair<List<Conversation>, Throwable?>> =
        api
            .getConversations()
            .map<Pair<List<Conversation>, Throwable?>> { conversationsResult ->
                val conversations = conversationsResult.lastMessages.mapNotNull {
                    messageMapper.toConversationFromNetwork(it, currentUser)
                }
                conversations to null
            }
            .onErrorResumeNext { throwable ->
                messagesLocalStore.getConversations(currentUser.uuid).map {
                    val conversations = it.mapNotNull {
                        messageMapper.toConversationFromLocal(it, currentUser)
                    }
                    conversations to throwable
                }
            }

}