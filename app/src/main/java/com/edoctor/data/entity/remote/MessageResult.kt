package com.edoctor.data.entity.remote

import com.google.gson.annotations.SerializedName

abstract class MessageResult {
    abstract val uuid: String
    abstract val recipientEmail: String
    abstract val sendingTimestamp: Long
}

abstract class SystemMessageResult : MessageResult()

abstract class UserMessageResult : MessageResult() {
    abstract val senderEmail: String
}


data class CallStatusMessageResult(
    override val uuid: String,
    override val senderEmail: String,
    override val recipientEmail: String,
    override val sendingTimestamp: Long,
    @SerializedName("callStatus")
    val callStatusValue: Int
) : UserMessageResult() {

    companion object {
        const val CALL_STATUS_INITIATED = 1
        const val CALL_STATUS_STARTED = 2
        const val CALL_STATUS_CANCELLED = 3
    }

}

data class TextMessageResult(
    override val uuid: String,
    override val senderEmail: String,
    override val recipientEmail: String,
    override val sendingTimestamp: Long,
    val text: String
) : UserMessageResult()