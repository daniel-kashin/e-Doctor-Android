package com.edoctor.data.mapper

import com.edoctor.data.entity.presentation.CallAction
import com.edoctor.data.entity.presentation.CallAction.ENTER
import com.edoctor.data.entity.presentation.CallAction.LEAVE
import com.edoctor.data.entity.presentation.CallStatusMessage
import com.edoctor.data.entity.presentation.CallStatusMessage.CallStatus
import com.edoctor.data.entity.presentation.CallStatusMessage.CallStatus.*
import com.edoctor.data.entity.presentation.Message
import com.edoctor.data.entity.presentation.TextMessage
import com.edoctor.data.entity.remote.*
import com.edoctor.data.entity.remote.CallActionRequest.Companion.CALL_ACTION_ENTER
import com.edoctor.data.entity.remote.CallActionRequest.Companion.CALL_ACTION_LEAVE
import com.edoctor.data.entity.remote.CallStatusMessageResult.Companion.CALL_STATUS_CANCELLED
import com.edoctor.data.entity.remote.CallStatusMessageResult.Companion.CALL_STATUS_INITIATED
import com.edoctor.data.entity.remote.CallStatusMessageResult.Companion.CALL_STATUS_STARTED

object MessageMapper {

    fun toPresentation(messagesResult: MessagesResult): List<Message> = messagesResult.run {
        messages.mapNotNull { toPresentation(it) }
    }

    fun toPresentation(messageWrapperResult: MessageResultWrapper): Message? = messageWrapperResult.run {
        when {
            textMessage != null -> toPresentation(textMessage)
            callStatusMessage != null -> toPresentation(callStatusMessage)
            else -> null
        }
    }

    fun toNetwork(callAction: CallAction) =
        CallActionRequest(
            getValueFromCallAction(callAction)
        )

    private fun toPresentation(textMessageResult: TextMessageResult): TextMessage =
        textMessageResult.run {
            TextMessage(uuid, senderEmail, recipientEmail, sendingTimestamp, text)
        }

    private fun toPresentation(callStatusMessage: CallStatusMessageResult): CallStatusMessage =
        callStatusMessage.run {
            val callStatus = getCallStatusFromValue(callStatus)
            val text = callStatus.toText()
            CallStatusMessage(uuid, senderEmail, recipientEmail, sendingTimestamp, callStatus, text)
        }

    private fun getValueFromCallAction(callAction: CallAction): Int {
        return when (callAction) {
            ENTER -> CALL_ACTION_ENTER
            LEAVE -> CALL_ACTION_LEAVE
        }
    }

    private fun getCallStatusFromValue(value: Int): CallStatusMessage.CallStatus {
        return when (value) {
            CALL_STATUS_INITIATED -> INITIATED
            CALL_STATUS_CANCELLED -> CANCELLED
            CALL_STATUS_STARTED -> STARTED
            else -> throw IllegalArgumentException()
        }
    }

    // TODO
    private fun CallStatus.toText(): String =
        when (this) {
            CallStatus.INITIATED -> "Разговор инициирован"
            CallStatus.STARTED -> "Разговор начат"
            CallStatus.CANCELLED -> "Разговор закончен"
        }

}