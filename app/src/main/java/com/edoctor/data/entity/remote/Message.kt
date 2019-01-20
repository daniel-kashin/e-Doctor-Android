package com.edoctor.data.entity.remote

import com.edoctor.utils.unixTimeToJavaTime
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
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

    override fun getUser(): IUser {
        return object : IUser {
            override fun getAvatar() = null
            override fun getName() = senderEmail
            override fun getId(): String = senderEmail
        }
    }

    override fun getText() = text
}