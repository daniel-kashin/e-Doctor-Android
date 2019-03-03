package com.edoctor.data.entity.presentation

import com.stfalcon.chatkit.commons.models.IDialog

class Conversation(private val currentUserEmail: String, private var _lastMessage: UserMessage) : IDialog<UserMessage> {

    override fun getDialogPhoto() = null

    override fun getUnreadCount() = 0

    override fun getId() = _lastMessage.senderEmail + _lastMessage.recipientEmail

    override fun getUsers() =  mutableListOf(_lastMessage.recipientEmail.toUser(), _lastMessage.senderEmail.toUser())

    override fun getLastMessage() = _lastMessage

    override fun getDialogName() = _lastMessage.run { recipientEmail.takeIf { it != currentUserEmail } ?: senderEmail }

    override fun setLastMessage(message: UserMessage) {
        _lastMessage = message
    }

}