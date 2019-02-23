package com.edoctor.data.entity.remote

data class ConversationsResult(val emails: List<Conversation>)

data class Conversation(val otherUserEmail: String, val lastMessageTimestamp: Long, val lastMessageIsFromMe: Boolean)