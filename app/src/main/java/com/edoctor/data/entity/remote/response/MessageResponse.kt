package com.edoctor.data.entity.remote.response

import com.edoctor.data.entity.remote.model.user.UserModelWrapper

sealed class MessageResponse {
    abstract val uuid: String
    abstract val recipientUser: UserModelWrapper
    abstract val sendingTimestamp: Long
}

sealed class UserMessageResponse : MessageResponse() {
    abstract val senderUser: UserModelWrapper
}


data class CallStatusMessageResponse(
    override val uuid: String,
    override val senderUser: UserModelWrapper,
    override val recipientUser: UserModelWrapper,
    override val sendingTimestamp: Long,
    val callStatus: Int,
    val callUuid: String
) : UserMessageResponse() {

    companion object {
        const val CALL_STATUS_INITIATED = 1
        const val CALL_STATUS_STARTED = 2
        const val CALL_STATUS_CANCELLED = 3
    }

}

data class TextMessageResponse(
    override val uuid: String,
    override val senderUser: UserModelWrapper,
    override val recipientUser: UserModelWrapper,
    override val sendingTimestamp: Long,
    val text: String
) : UserMessageResponse()

data class MedicalAccessesMessageResponse(
    override val uuid: String,
    override val senderUser: UserModelWrapper,
    override val recipientUser: UserModelWrapper,
    override val sendingTimestamp: Long
) : UserMessageResponse()

data class MedicalRecordRequestMessageResponse(
    override val uuid: String,
    override val senderUser: UserModelWrapper,
    override val recipientUser: UserModelWrapper,
    override val sendingTimestamp: Long
) : UserMessageResponse()