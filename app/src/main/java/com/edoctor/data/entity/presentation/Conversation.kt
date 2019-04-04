package com.edoctor.data.entity.presentation

import com.edoctor.data.entity.remote.model.user.UserModel
import com.stfalcon.chatkit.commons.models.IDialog

class Conversation(val currentUser: UserModel, private var _lastMessage: UserMessage) : IDialog<UserMessage> {

    val recipientUser = _lastMessage.run { recipientUser.takeIf { it != currentUser } ?: senderUser }

    override fun getDialogPhoto() = recipientUser.relativeImageUrl

    override fun getUnreadCount() = 0

    override fun getId() = _lastMessage.senderUser.email + _lastMessage.recipientUser.email

    override fun getUsers() = mutableListOf(_lastMessage.recipientUser.toPresentation(), _lastMessage.senderUser.toPresentation())

    override fun getLastMessage() = _lastMessage

    override fun getDialogName() = recipientUser.fullName ?: recipientUser.email

    override fun setLastMessage(message: UserMessage) {
        _lastMessage = message
    }

}