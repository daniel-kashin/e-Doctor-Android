package com.edoctor.data.entity.remote.result

data class MessageResultWrapper(
    val textMessage: TextMessageResult?,
    val callStatusMessage: CallStatusMessageResult?
)