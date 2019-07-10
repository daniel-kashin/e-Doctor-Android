package com.infostrategic.edoctor.data.entity.remote.request

data class MessageRequestWrapper(
    val callActionMessageRequest: CallActionMessageRequest? = null,
    val textMessageRequest: TextMessageRequest? = null
)