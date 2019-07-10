package com.infostrategic.edoctor.data.entity.presentation

import android.text.Html
import com.infostrategic.edoctor.data.entity.remote.model.user.UserModel
import com.infostrategic.edoctor.utils.unixTimeToJavaTime
import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.MessageContentType
import java.util.*

abstract class Message : IMessage {
    abstract val uuid: String
    abstract val recipientUser: UserModel
    abstract val sendingTimestamp: Long
}

sealed class UserMessage : Message(), MessageContentType {
    abstract val senderUser: UserModel
    abstract fun withRemovedHtml(): UserMessage

    override fun getId() = uuid
    override fun getCreatedAt() = Date(sendingTimestamp.unixTimeToJavaTime())
    override fun getUser() = senderUser.toPresentationFromNetwork()
}


data class CallStatusMessage(
    override val uuid: String,
    override val senderUser: UserModel,
    override val recipientUser: UserModel,
    override val sendingTimestamp: Long,
    val callStatus: CallStatus,
    val callUuid: String,
    private val text: String
) : UserMessage() {

    enum class CallStatus {
        INITIATED,
        STARTED,
        CANCELLED;
    }

    override fun getText() = text

    override fun withRemovedHtml() = this

}

data class TextMessage(
    override val uuid: String,
    override val senderUser: UserModel,
    override val recipientUser: UserModel,
    override val sendingTimestamp: Long,
    private val text: String
) : UserMessage() {

    override fun getText() = text

    override fun withRemovedHtml() = this

}

data class MedicalAccessesMessage(
    override val uuid: String,
    override val senderUser: UserModel,
    override val recipientUser: UserModel,
    override val sendingTimestamp: Long,
    private val text: String
) : UserMessage() {

    override fun getText() = text

    override fun withRemovedHtml() = this.copy(text = Html.fromHtml(text).toString())

}

data class MedicalRecordRequestMessage(
    override val uuid: String,
    override val senderUser: UserModel,
    override val recipientUser: UserModel,
    override val sendingTimestamp: Long,
    private val text: String
) : UserMessage() {

    override fun getText() = text

    override fun withRemovedHtml() = this.copy(text = Html.fromHtml(text).toString())

}

data class ImageMessage(
    override val uuid: String,
    override val senderUser: UserModel,
    override val recipientUser: UserModel,
    override val sendingTimestamp: Long,
    private val absoluteImageUrl: String,
    private val text: String
) : UserMessage(), MessageContentType.Image {

    override fun getText() = text

    override fun getImageUrl() = absoluteImageUrl

    override fun withRemovedHtml() = this

}