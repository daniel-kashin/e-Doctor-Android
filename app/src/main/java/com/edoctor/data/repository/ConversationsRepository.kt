package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.Conversation
import com.edoctor.data.mapper.MessageMapper
import com.edoctor.data.remote.api.ConversationsRestApi
import io.reactivex.Single

class ConversationsRepository(
    private val currentUserEmail: String,
    private val api: ConversationsRestApi,
    private val messageMapper: MessageMapper
) {

    fun getConversations(): Single<List<Conversation>> {
        return api.getConversations().map { result ->
            result.lastMessages
                .mapNotNull { messageMapper.toPresentation(it, currentUserEmail) }
                .map { Conversation(currentUserEmail, it) }
        }
    }

}