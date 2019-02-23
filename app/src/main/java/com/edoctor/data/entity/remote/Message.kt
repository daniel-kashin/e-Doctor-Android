package com.edoctor.data.entity.remote

import com.edoctor.data.entity.presentation.toUser
import com.edoctor.utils.unixTimeToJavaTime
import com.stfalcon.chatkit.commons.models.IMessage
import java.util.*

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
    private val text: String
) : UserMessage(), IMessage {

    override fun getId() = uuid

    override fun getCreatedAt() = Date(sendingTimestamp.unixTimeToJavaTime())

    override fun getUser() = senderEmail.toUser()

    override fun getText() = text

}