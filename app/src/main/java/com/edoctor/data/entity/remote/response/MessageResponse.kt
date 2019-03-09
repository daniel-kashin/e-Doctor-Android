package com.edoctor.data.entity.remote.response

abstract class MessageResponse {
    abstract val uuid: String
    abstract val recipientEmail: String
    abstract val sendingTimestamp: Long
}

abstract class SystemMessageResponse : MessageResponse()

abstract class UserMessageResponse : MessageResponse() {
    abstract val senderEmail: String
}


data class CallStatusMessageResponse(
    override val uuid: String,
    override val senderEmail: String,
    override val recipientEmail: String,
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
    override val senderEmail: String,
    override val recipientEmail: String,
    override val sendingTimestamp: Long,
    val text: String
) : UserMessageResponse()