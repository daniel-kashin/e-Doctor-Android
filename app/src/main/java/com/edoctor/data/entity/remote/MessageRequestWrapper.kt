package com.edoctor.data.entity.remote

data class MessageRequestWrapper(
    val callActionRequest: CallActionRequest? = null,
    val textMessageResult: TextMessageResult? = null
)