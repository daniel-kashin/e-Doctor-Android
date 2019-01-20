package com.edoctor.data.entity.remote

abstract class Message {
    abstract val uuid: String
    abstract val recipientEmail: String
    abstract val sendingTimestamp: Long
}

abstract class SystemMessage : Message()

abstract class UserMessage : Message() {
    abstract val senderEmail: String
}


data class TextMessage(
    override val uuid: String,
    override val senderEmail: String,
    override val recipientEmail: String,
    override val sendingTimestamp: Long,
    val text: String
) : UserMessage()