package com.edoctor.data.entity.remote.request

import com.edoctor.data.entity.remote.result.TextMessageResult

data class MessageRequestWrapper(
    val callActionRequest: CallActionRequest? = null,
    val textMessageResult: TextMessageResult? = null
)