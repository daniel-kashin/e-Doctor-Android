package com.infostrategic.edoctor.presentation.views

import com.infostrategic.edoctor.data.entity.presentation.CallStatusMessage
import com.infostrategic.edoctor.data.entity.presentation.MedicalAccessesMessage
import com.infostrategic.edoctor.data.entity.presentation.MedicalRecordRequestMessage
import com.infostrategic.edoctor.data.entity.presentation.Message
import com.stfalcon.chatkit.messages.MessageHolders

class MessageContentChecker : MessageHolders.ContentChecker<Message> {

    companion object {
        const val CONTENT_TYPE_CALL: Byte = 22
        const val CONTENT_TYPE_HYPERLINK_TEXT: Byte = 23
    }

    override fun hasContentFor(message: Message?, type: Byte): Boolean {
        return when (type) {
            CONTENT_TYPE_CALL -> message is CallStatusMessage
            CONTENT_TYPE_HYPERLINK_TEXT -> message is MedicalRecordRequestMessage || message is MedicalAccessesMessage
            else -> false
        }
    }

}