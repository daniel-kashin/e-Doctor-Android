package com.infostrategic.edoctor.data.entity.presentation

data class CallActionRequest(
    val callAction: CallAction,
    val callUuid: String
) {

    enum class CallAction {
        INITIATE,
        ENTER,
        LEAVE
    }

}
