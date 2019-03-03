package com.edoctor.data.mapper

import com.edoctor.data.entity.presentation.CallStatusMessage
import com.edoctor.data.entity.presentation.CallStatusMessage.CallStatus.*
import com.edoctor.data.entity.presentation.Message
import com.edoctor.data.entity.presentation.TextMessage
import com.edoctor.data.entity.remote.CallStatusMessageResult
import com.edoctor.data.entity.remote.CallStatusMessageResult.Companion.CALL_STATUS_CANCELLED
import com.edoctor.data.entity.remote.CallStatusMessageResult.Companion.CALL_STATUS_INITIATED
import com.edoctor.data.entity.remote.CallStatusMessageResult.Companion.CALL_STATUS_STARTED
import com.edoctor.data.entity.remote.MessageWrapperResult
import com.edoctor.data.entity.remote.MessagesResult
import com.edoctor.data.entity.remote.TextMessageResult

object MessageMapper {

    fun toPresentation(messagesResult: MessagesResult): List<Message> = messagesResult.run {
        messages.mapNotNull { toPresentation(it) }
    }

    fun toPresentation(messageWrapperResult: MessageWrapperResult): Message? = messageWrapperResult.run {
        when {
            textMessage != null -> toPresentation(textMessage)
            callStatusMessage != null -> toPresentation(callStatusMessage)
            else -> null
        }
    }

    private fun toPresentation(textMessageResult: TextMessageResult): TextMessage =
        textMessageResult.run {
            TextMessage(uuid, senderEmail, recipientEmail, sendingTimestamp, text)
        }

    private fun toPresentation(callStatusMessage: CallStatusMessageResult): CallStatusMessage =
        callStatusMessage.run {
            val callStatus = getCallStatus(callStatus)
            val text = callStatus.toText()
            CallStatusMessage(uuid, senderEmail, recipientEmail, sendingTimestamp, callStatus, text)
        }


    private fun getCallStatus(value: Int): CallStatusMessage.CallStatus {
        return when (value) {
            CALL_STATUS_INITIATED -> CallStatusMessage.CallStatus.INITIATED
            CALL_STATUS_CANCELLED -> CallStatusMessage.CallStatus.CANCELLED
            CALL_STATUS_STARTED -> CallStatusMessage.CallStatus.STARTED
            else -> throw IllegalArgumentException()
        }
    }

    // TODO
    private fun CallStatusMessage.CallStatus.toText(): String =
        when (this) {
            INITIATED -> "Разговор инициирован"
            STARTED -> "Разговор начат"
            CANCELLED -> "Разговор закончен"
        }

}