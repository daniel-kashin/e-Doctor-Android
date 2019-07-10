package com.danielkashin.edoctor.data.mapper

import android.content.Context
import com.danielkashin.edoctor.R
import com.danielkashin.edoctor.data.entity.local.message.MessageEntity
import com.danielkashin.edoctor.data.entity.presentation.*
import com.danielkashin.edoctor.data.entity.presentation.CallActionRequest.CallAction
import com.danielkashin.edoctor.data.entity.presentation.CallActionRequest.CallAction.*
import com.danielkashin.edoctor.data.entity.presentation.CallStatusMessage.CallStatus
import com.danielkashin.edoctor.data.entity.presentation.CallStatusMessage.CallStatus.*
import com.danielkashin.edoctor.data.entity.remote.model.user.DoctorModel
import com.danielkashin.edoctor.data.entity.remote.model.user.PatientModel
import com.danielkashin.edoctor.data.entity.remote.model.user.UserModel
import com.danielkashin.edoctor.data.entity.remote.request.CallActionMessageRequest
import com.danielkashin.edoctor.data.entity.remote.request.CallActionMessageRequest.Companion.CALL_ACTION_ENTER
import com.danielkashin.edoctor.data.entity.remote.request.CallActionMessageRequest.Companion.CALL_ACTION_INITIATE
import com.danielkashin.edoctor.data.entity.remote.request.CallActionMessageRequest.Companion.CALL_ACTION_LEAVE
import com.danielkashin.edoctor.data.entity.remote.response.*
import com.danielkashin.edoctor.data.entity.remote.response.CallStatusMessageResponse.Companion.CALL_STATUS_CANCELLED
import com.danielkashin.edoctor.data.entity.remote.response.CallStatusMessageResponse.Companion.CALL_STATUS_INITIATED
import com.danielkashin.edoctor.data.entity.remote.response.CallStatusMessageResponse.Companion.CALL_STATUS_STARTED
import com.danielkashin.edoctor.data.injection.NetworkModule.Companion.getAbsoluteImageUrl
import com.danielkashin.edoctor.data.injection.NetworkModule.Companion.getRelativeImageUrl
import com.danielkashin.edoctor.data.mapper.UserMapper.unwrapResponse
import com.danielkashin.edoctor.data.mapper.UserMapper.withAbsoluteUrl

class MessageMapper(context: Context) {

    companion object {
        private const val MESSAGE_TYPE_MEDICAL_ACCESSES = 0
        private const val MESSAGE_TYPE_MEDICAL_RECORD_REQUEST = 1
        private const val MESSAGE_TYPE_TEXT = 2
        private const val MESSAGE_TYPE_CALL_STATUS = 3
        private const val MESSAGE_TYPE_IMAGE = 4
    }

    private val applicationContext: Context = context.applicationContext

    fun toLocalFromPresentation(message: Message): MessageEntity? = message.run {
        if (this !is UserMessage) return@run null

        when (this) {
            is TextMessage -> MessageEntity(
                uuid = uuid,
                timestamp = sendingTimestamp,
                senderUuid = senderUser.uuid,
                recipientUuid = recipientUser.uuid,
                type = MESSAGE_TYPE_TEXT,
                text = text,
                senderFullName = senderUser.fullName,
                recipientFullName = recipientUser.fullName
            )
            is CallStatusMessage -> MessageEntity(
                uuid = uuid,
                timestamp = sendingTimestamp,
                senderUuid = senderUser.uuid,
                recipientUuid = recipientUser.uuid,
                type = MESSAGE_TYPE_CALL_STATUS,
                callStatus = toValueFromPresentation(callStatus),
                callUuid = callUuid,
                senderFullName = senderUser.fullName,
                recipientFullName = recipientUser.fullName
            )
            is MedicalRecordRequestMessage -> MessageEntity(
                uuid = uuid,
                timestamp = sendingTimestamp,
                senderUuid = senderUser.uuid,
                recipientUuid = recipientUser.uuid,
                type = MESSAGE_TYPE_MEDICAL_RECORD_REQUEST,
                senderFullName = senderUser.fullName,
                recipientFullName = recipientUser.fullName
            )
            is MedicalAccessesMessage -> MessageEntity(
                uuid = uuid,
                timestamp = sendingTimestamp,
                senderUuid = senderUser.uuid,
                recipientUuid = recipientUser.uuid,
                type = MESSAGE_TYPE_MEDICAL_ACCESSES,
                senderFullName = senderUser.fullName,
                recipientFullName = recipientUser.fullName
            )
            is ImageMessage -> MessageEntity(
                uuid = uuid,
                timestamp = sendingTimestamp,
                senderUuid = senderUser.uuid,
                recipientUuid = recipientUser.uuid,
                type = MESSAGE_TYPE_IMAGE,
                imageRelativeUrl = getRelativeImageUrl(imageUrl),
                senderFullName = senderUser.fullName,
                recipientFullName = recipientUser.fullName
            )
        }
    }

    fun toPresentationFromLocal(
        messageEntity: MessageEntity,
        currentUser: UserModel
    ): UserMessage? = messageEntity.run {
        val isFromCurrentUser = currentUser.uuid == senderUuid
        val currentUserIsDoctor = currentUser is DoctorModel
        val senderIsDoctor = if (isFromCurrentUser) currentUserIsDoctor else !currentUserIsDoctor

        val sender = if (senderIsDoctor) {
            DoctorModel(uuid = senderUuid, fullName = senderFullName)
        } else {
            PatientModel(uuid = senderUuid, fullName = senderFullName)
        }
        val recipient = if (senderIsDoctor) {
            PatientModel(uuid = recipientUuid, fullName = recipientFullName)
        } else {
            DoctorModel(uuid = recipientUuid, fullName = recipientFullName)
        }

        return when {
            type == MESSAGE_TYPE_TEXT && text != null -> {
                TextMessage(uuid, sender, recipient, timestamp, text)
            }
            type == MESSAGE_TYPE_CALL_STATUS && callStatus != null && callUuid != null -> {
                toPresentationFromValue(callStatus)?.let { callStatus ->
                    CallStatusMessage(
                        uuid,
                        sender,
                        recipient,
                        timestamp,
                        callStatus,
                        callUuid,
                        callStatus.toText(isFromCurrentUser)
                    )
                }
            }
            type == MESSAGE_TYPE_MEDICAL_RECORD_REQUEST -> {
                MedicalRecordRequestMessage(
                    uuid,
                    sender,
                    recipient,
                    timestamp,
                    applicationContext.getString(R.string.new_record_request_was_added_hyperlink)
                )
            }
            type == MESSAGE_TYPE_MEDICAL_ACCESSES -> {
                MedicalAccessesMessage(
                    uuid,
                    sender,
                    recipient,
                    timestamp,
                    applicationContext.getString(R.string.medcard_access_changed_hyperlink)
                )
            }
            type == MESSAGE_TYPE_IMAGE && imageRelativeUrl != null -> {
                ImageMessage(
                    uuid,
                    sender,
                    recipient,
                    timestamp,
                    getAbsoluteImageUrl(imageRelativeUrl),
                    applicationContext.getString(R.string.attached_image)
                )
            }
            else -> null
        }
    }

    fun toConversationFromNetwork(
        messageWrapperResponse: MessageResponseWrapper,
        currentUser: UserModel
    ): Conversation? {
        return toPresentationFromNetwork(messageWrapperResponse, currentUser)?.let {
            val doctorString = applicationContext.getString(R.string.doctor).capitalize()
            val patientString = applicationContext.getString(R.string.patient).capitalize()
            Conversation(currentUser, doctorString, patientString, it)
        }
    }

    fun toConversationFromLocal(
        messageEntity: MessageEntity,
        currentUser: UserModel
    ): Conversation? {
        return toPresentationFromLocal(messageEntity, currentUser)?.let {
            val doctorString = applicationContext.getString(R.string.doctor).capitalize()
            val patientString = applicationContext.getString(R.string.patient).capitalize()
            Conversation(currentUser, doctorString, patientString, it)
        }
    }

    fun toPresentationFromNetwork(
        messagesResponse: MessagesResponse,
        currentUser: UserModel
    ): List<Message> = messagesResponse.run {
        messages.mapNotNull { toPresentationFromNetwork(it, currentUser) }
    }

    fun toPresentationFromNetwork(
        messageWrapperResponse: MessageResponseWrapper,
        currentUser: UserModel
    ): UserMessage? =
        messageWrapperResponse.run {
            when {
                textMessageResponse != null -> toPresentationFromNetwork(textMessageResponse)
                callStatusMessageResponse != null -> toPresentationFromNetwork(callStatusMessageResponse, currentUser)
                medicalAccessesMessageResponse != null -> toPresentationFromNetwork(medicalAccessesMessageResponse)
                medicalRecordRequestResponse != null -> toPresentationFromNetwork(medicalRecordRequestResponse)
                imageMessageResponse != null -> toPresentationFromNetwork(imageMessageResponse)
                else -> null
            }
        }

    fun toNetworkFromPresentation(callActionRequest: CallActionRequest) =
        CallActionMessageRequest(
            toValueFromPresentation(callActionRequest.callAction),
            callActionRequest.callUuid
        )

    private fun toPresentationFromNetwork(
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

    private fun toPresentationFromNetwork(
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

    private fun toPresentationFromNetwork(
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

    private fun toPresentationFromNetwork(
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

    private fun toPresentationFromNetwork(
        callStatusMessage: CallStatusMessageResponse,
        currentUser: UserModel
    ): CallStatusMessage? =
        callStatusMessage.run {
            val callStatus = toPresentationFromValue(callStatus) ?: return@run null
            val senderUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(senderUser) ?: return@run null) ?: return@run null
            val recipientUserUnwrapped =
                unwrapResponse(withAbsoluteUrl(recipientUser) ?: return@run null) ?: return@run null
            val isFromCurrentUser = currentUser.uuid == senderUserUnwrapped.uuid
            CallStatusMessage(
                uuid,
                senderUserUnwrapped, recipientUserUnwrapped,
                sendingTimestamp,
                callStatus, callUuid,
                callStatus.toText(isFromCurrentUser)
            )
        }

    private fun toValueFromPresentation(callAction: CallAction): Int {
        return when (callAction) {
            INITIATE -> CALL_ACTION_INITIATE
            ENTER -> CALL_ACTION_ENTER
            LEAVE -> CALL_ACTION_LEAVE
        }
    }

    private fun toValueFromPresentation(callStatus: CallStatusMessage.CallStatus): Int {
        return when (callStatus) {
            INITIATED -> CALL_STATUS_INITIATED
            CANCELLED -> CALL_STATUS_CANCELLED
            STARTED -> CALL_STATUS_STARTED
        }
    }

    private fun toPresentationFromValue(value: Int): CallStatusMessage.CallStatus? {
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