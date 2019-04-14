package com.edoctor.presentation.views

import com.edoctor.data.entity.presentation.MedicalAccessesMessage
import com.edoctor.data.entity.presentation.MedicalRecordRequestMessage
import com.edoctor.data.entity.presentation.Message
import com.stfalcon.chatkit.messages.MessageHolders

class HyperlinkTextMessageContentChecker : MessageHolders.ContentChecker<Message> {

    companion object {
        const val CONTENT_TYPE_HYPERLINK_TEXT: Byte = 23
    }

    override fun hasContentFor(message: Message?, type: Byte): Boolean {
        return if (type == CONTENT_TYPE_HYPERLINK_TEXT) {
            message is MedicalRecordRequestMessage || message is MedicalAccessesMessage
        } else {
            false
        }
    }

}