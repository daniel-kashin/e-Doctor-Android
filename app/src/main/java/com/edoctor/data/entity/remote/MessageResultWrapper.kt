package com.edoctor.data.entity.remote

data class MessageResultWrapper(
    val textMessage: TextMessageResult?,
    val callStatusMessage: CallStatusMessageResult?
)