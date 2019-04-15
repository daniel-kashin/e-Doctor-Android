package com.edoctor.data.mapper

import android.content.Context
import com.edoctor.R
import com.edoctor.data.entity.presentation.*
import com.edoctor.data.entity.presentation.CallActionRequest.CallAction
import com.edoctor.data.entity.presentation.CallActionRequest.CallAction.*
import com.edoctor.data.entity.presentation.CallStatusMessage.CallStatus
import com.edoctor.data.entity.presentation.CallStatusMessage.CallStatus.*
import com.edoctor.data.entity.remote.model.user.UserModel
import com.edoctor.data.entity.remote.request.CallActionMessageRequest
import com.edoctor.data.entity.remote.request.CallActionMessageRequest.Companion.CALL_ACTION_ENTER
import com.edoctor.data.entity.remote.request.CallActionMessageRequest.Companion.CALL_ACTION_INITIATE
import com.edoctor.data.entity.remote.request.CallActionMessageRequest.Companion.CALL_ACTION_LEAVE
import com.edoctor.data.entity.remote.response.*
import com.edoctor.data.entity.remote.response.CallStatusMessageResponse.Companion.CALL_STATUS_CANCELLED
import com.edoctor.data.entity.remote.response.CallStatusMessageResponse.Companion.CALL_STATUS_INITIATED
import com.edoctor.data.entity.remote.response.CallStatusMessageResponse.Companion.CALL_STATUS_STARTED
import com.edoctor.data.injection.NetworkModule.Companion.getAbsoluteImageUrl
import com.edoctor.data.mapper.UserMapper.unwrapResponse
import com.edoctor.data.mapper.UserMapper.withAbsoluteUrl

class MessageMapper(context: Context) {

    private val applicationContext: Context = context.applicationContext

    fun toConversation(
        messageWrapperResponse: MessageResponseWrapper,
        currentUser: UserModel
    ): Conversation? {
        return toPresentation(messageWrapperResponse, currentUser)?.let {
            val doctorString = applicationContext.getString(R.string.doctor).capitalize()
            val patientString = applicationContext.getString(R.string.patient).capitalize()
            Conversation(currentUser, doctorString, patientString, it)
        }
    }

    fun toPresentation(
        messagesResponse: MessagesResponse,
        currentUser: UserModel
    ): List<Message> = messagesResponse.run {
        messages.mapNotNull { toPresentation(it, currentUser) }
    }

    fun toPresentation(
        messageWrapperResponse: MessageResponseWrapper,
        currentUser: UserModel
    ): UserMessage? =
        messageWrapperResponse.run {
            when {
                textMessageResponse != null -> toPresentation(textMessageResponse)
                callStatusMessageResponse != null -> toPresentation(callStatusMessageResponse, currentUser)
                medicalAccessesMessageResponse != null -> toPresentation(medicalAccessesMessageResponse)
                medicalRecordRequestResponse != null -> toPresentation(medicalRecordRequestResponse)
                imageMessageResponse != null -> toPresentation(imageMessageResponse)
                else -> null
            }
        }

    fun toNetwork(callActionRequest: CallActionRequest) =
        CallActionMessageRequest(
            getValueFromCallAction(callActionRequest.callAction),
            callActionRequest.callUuid
        )

    private fun toPresentation(
        imageMessageResponse: ImageMessageResponse
    ): ImageMessage? =
        imageMessageResponse.run {
            val senderUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(senderUser) ?: return@run null) ?: return@run null
            val recipientUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(recipientUser) ?: return@run null) ?: return@run null
            ImageMessage(
                uuid,
                senderUserUnwrapped,
                recipientUserUnwrapped,
                sendingTimestamp,
                getAbsoluteImageUrl(relativeImageUrl),
                applicationContext.getString(R.string.attached_image)
            )
        }

    private fun toPresentation(
        medicalRecordRequestMessageResponse: MedicalRecordRequestMessageResponse
    ): MedicalRecordRequestMessage? =
        medicalRecordRequestMessageResponse.run {
            val senderUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(senderUser) ?: return@run null) ?: return@run null
            val recipientUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(recipientUser) ?: return@run null) ?: return@run null
            MedicalRecordRequestMessage(
                uuid,
                senderUserUnwrapped,
                recipientUserUnwrapped,
                sendingTimestamp,
                applicationContext.getString(R.string.new_record_request_was_added_hyperlink)
            )
        }

    private fun toPresentation(
        medicalAccessesMessageResponse: MedicalAccessesMessageResponse
    ): MedicalAccessesMessage? =
        medicalAccessesMessageResponse.run {
            val senderUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(senderUser) ?: return@run null) ?: return@run null
            val recipientUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(recipientUser) ?: return@run null) ?: return@run null
            MedicalAccessesMessage(
                uuid,
                senderUserUnwrapped,
                recipientUserUnwrapped,
                sendingTimestamp,
                applicationContext.getString(R.string.medcard_access_changed_hyperlink)
            )
        }

    private fun toPresentation(
        textMessageResult: TextMessageResponse
    ): TextMessage? =
        textMessageResult.run {
            val senderUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(senderUser) ?: return@run null) ?: return@run null
            val recipientUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(recipientUser) ?: return@run null) ?: return@run null
            TextMessage(
                uuid,
                senderUserUnwrapped,
                recipientUserUnwrapped,
                sendingTimestamp,
                text
            )
        }

    private fun toPresentation(
        callStatusMessage: CallStatusMessageResponse,
        currentUser: UserModel
    ): CallStatusMessage? =
        callStatusMessage.run {
            val callStatus = getCallStatusFromValue(callStatus) ?: return@run null
            val senderUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(senderUser) ?: return@run null) ?: return@run null
            val recipientUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(recipientUser) ?: return@run null) ?: return@run null
            val isFromCurrentUser = currentUser.email == senderUserUnwrapped.email
            val text = callStatus.toText(isFromCurrentUser)
            CallStatusMessage(
                uuid,
                senderUserUnwrapped, recipientUserUnwrapped,
                sendingTimestamp,
                callStatus, callUuid,
                isFromCurrentUser,
                text
            )
        }

    private fun getValueFromCallAction(callAction: CallAction): Int {
        return when (callAction) {
            INITIATE -> CALL_ACTION_INITIATE
            ENTER -> CALL_ACTION_ENTER
            LEAVE -> CALL_ACTION_LEAVE
        }
    }

    private fun getCallStatusFromValue(value: Int): CallStatusMessage.CallStatus? {
        return when (value) {
            CALL_STATUS_INITIATED -> INITIATED
            CALL_STATUS_CANCELLED -> CANCELLED
            CALL_STATUS_STARTED -> STARTED
            else -> null
        }
    }

    private fun CallStatus.toText(isFromCurrentUser: Boolean): String = applicationContext.getString(
        when (this) {
            CallStatus.INITIATED -> if (isFromCurrentUser) R.string.outcoming_call else R.string.incoming_call
            CallStatus.STARTED -> R.string.call_started
            CallStatus.CANCELLED -> R.string.call_ended
        }
    )

}