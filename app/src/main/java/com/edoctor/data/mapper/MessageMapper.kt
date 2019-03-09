package com.edoctor.data.mapper

import com.edoctor.data.entity.presentation.*
import com.edoctor.data.entity.presentation.CallActionRequest.CallAction
import com.edoctor.data.entity.presentation.CallActionRequest.CallAction.*
import com.edoctor.data.entity.presentation.CallStatusMessage.CallStatus
import com.edoctor.data.entity.presentation.CallStatusMessage.CallStatus.*
import com.edoctor.data.entity.remote.request.CallActionMessageRequest
import com.edoctor.data.entity.remote.request.CallActionMessageRequest.Companion.CALL_ACTION_ENTER
import com.edoctor.data.entity.remote.request.CallActionMessageRequest.Companion.CALL_ACTION_INITIATE
import com.edoctor.data.entity.remote.request.CallActionMessageRequest.Companion.CALL_ACTION_LEAVE
import com.edoctor.data.entity.remote.response.CallStatusMessageResponse
import com.edoctor.data.entity.remote.response.CallStatusMessageResponse.Companion.CALL_STATUS_CANCELLED
import com.edoctor.data.entity.remote.response.CallStatusMessageResponse.Companion.CALL_STATUS_INITIATED
import com.edoctor.data.entity.remote.response.CallStatusMessageResponse.Companion.CALL_STATUS_STARTED
import com.edoctor.data.entity.remote.response.MessageResponseWrapper
import com.edoctor.data.entity.remote.response.MessagesResponse
import com.edoctor.data.entity.remote.response.TextMessageResponse

object MessageMapper {

    fun toPresentation(messagesResponse: MessagesResponse): List<Message> = messagesResponse.run {
        messages.mapNotNull { toPresentation(it) }
    }

    fun toPresentation(messageWrapperResponse: MessageResponseWrapper): UserMessage? = messageWrapperResponse.run {
        when {
            textMessageResponse != null -> toPresentation(textMessageResponse)
            callStatusMessageResponse != null -> toPresentation(callStatusMessageResponse)
            else -> null
        }
    }

    fun toNetwork(callActionRequest: CallActionRequest) =
        CallActionMessageRequest(
            getValueFromCallAction(callActionRequest.callAction),
            callActionRequest.callUuid
        )

    private fun toPresentation(textMessageResult: TextMessageResponse): TextMessage =
        textMessageResult.run {
            TextMessage(uuid, senderEmail, recipientEmail, sendingTimestamp, text)
        }

    private fun toPresentation(callStatusMessage: CallStatusMessageResponse): CallStatusMessage =
        callStatusMessage.run {
            val callStatus = getCallStatusFromValue(callStatus)
            val text = callStatus.toText()
            CallStatusMessage(uuid, senderEmail, recipientEmail, sendingTimestamp, callStatus, callUuid, text)
        }

    private fun getValueFromCallAction(callAction: CallAction): Int {
        return when (callAction) {
            INITIATE -> CALL_ACTION_INITIATE
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