package com.edoctor.data.repository

import com.edoctor.data.entity.presentation.Conversation
import com.edoctor.data.mapper.MessageMapper.toPresentation
import com.edoctor.data.remote.api.ConversationsRestApi
import io.reactivex.Single

class ConversationsRepository(
    private val currentUserEmail: String,
    private val api: ConversationsRestApi
) {

    fun getConversations(): Single<List<Conversation>> {
        return api.getConversations().map { result ->
            result.lastMessages
                .mapNotNull { toPresentation(it) }
                .map { Conversation(currentUserEmail, it) }
        }
    }

}