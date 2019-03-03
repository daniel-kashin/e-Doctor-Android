package com.edoctor.data.entity.remote

data class MessageWrapperResult(
    val textMessage: TextMessageResult?,
    val callStatusMessage: CallStatusMessageResult?
)