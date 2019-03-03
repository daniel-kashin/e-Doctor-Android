package com.edoctor.data.entity.presentation

import com.edoctor.utils.unixTimeToJavaTime
import com.stfalcon.chatkit.commons.models.IMessage
import java.util.*

abstract class Message : IMessage {
    abstract val uuid: String
    abstract val recipientEmail: String
    abstract val sendingTimestamp: Long
}

abstract class SystemMessage : Message()

abstract class UserMessage : Message() {
    abstract val senderEmail: String

    override fun getId() = uuid
    override fun getCreatedAt() = Date(sendingTimestamp.unixTimeToJavaTime())
    override fun getUser() = senderEmail.toUser()
}


data class CallStatusMessage(
    override val uuid: String,
    override val senderEmail: String,
    override val recipientEmail: String,
    override val sendingTimestamp: Long,
    private val callStatus: CallStatus,
    private val _text: String
) : UserMessage() {

    override fun getText() = _text

    enum class CallStatus {
        INITIATED,
        STARTED,
        CANCELLED;
    }
}

data class TextMessage(
    override val uuid: String,
    override val senderEmail: String,
    override val recipientEmail: String,
    override val sendingTimestamp: Long,
    private val text: String
) : UserMessage() {

    override fun getText() = text

}