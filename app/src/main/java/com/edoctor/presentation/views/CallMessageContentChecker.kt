package com.edoctor.presentation.views

import com.edoctor.data.entity.presentation.CallStatusMessage
import com.edoctor.data.entity.presentation.Message
import com.stfalcon.chatkit.messages.MessageHolders

class CallMessageContentChecker : MessageHolders.ContentChecker<Message> {

    companion object {
        const val CONTENT_TYPE_CALL: Byte = 22
    }

    override fun hasContentFor(message: Message?, type: Byte): Boolean {
        return if (type == CONTENT_TYPE_CALL) {
            message is CallStatusMessage
        } else {
            false
        }
    }

}