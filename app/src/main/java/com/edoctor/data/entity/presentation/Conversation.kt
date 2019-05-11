package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.remote.model.user.DoctorModel
import com.edoctor.data.entity.remote.model.user.PatientModel
import com.edoctor.data.entity.remote.model.user.UserModel
import com.stfalcon.chatkit.commons.models.IDialog

class Conversation(
    val currentUser: UserModel,
    private val doctorString: String,
    private val patientString: String,
    private var _lastMessage: UserMessage
) : IDialog<UserMessage> {

    val recipientUser = _lastMessage.run { recipientUser.takeIf { it.uuid != currentUser.uuid } ?: senderUser }

    override fun getDialogPhoto() = recipientUser.relativeImageUrl

    override fun getUnreadCount() = 0

    override fun getId() = _lastMessage.senderUser.uuid + _lastMessage.recipientUser.uuid

    override fun getUsers() = mutableListOf(_lastMessage.recipientUser.toPresentationFromNetwork(), _lastMessage.senderUser.toPresentationFromNetwork())

    override fun getLastMessage() = _lastMessage.withRemovedHtml()

    override fun getDialogName() = when {
        recipientUser.fullName != null -> recipientUser.fullName
        recipientUser is DoctorModel -> doctorString
        recipientUser is PatientModel -> patientString
        else -> null
    }

    override fun setLastMessage(message: UserMessage) {
        _lastMessage = message
    }

}