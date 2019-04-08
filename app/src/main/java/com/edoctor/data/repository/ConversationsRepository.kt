package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.Conversation
import com.edoctor.data.entity.remote.model.user.UserModel
import com.edoctor.data.mapper.MessageMapper
import com.edoctor.data.remote.rest.ConversationsRestApi
import io.reactivex.Single

class ConversationsRepository(
    private val currentUser: UserModel,
    private val api: ConversationsRestApi,
    private val messageMapper: MessageMapper
) {

    fun getConversations(): Single<List<Conversation>> {
        return api.getConversations().map { result ->
            result.lastMessages
                .mapNotNull { messageMapper.toPresentation(it, currentUser) }
                .map { Conversation(currentUser, it) }
        }
    }

}