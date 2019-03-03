package com.edoctor.data.entity.remote.request

data class CallActionRequest(val callStatus: Int) {

    companion object {
        const val CALL_ACTION_INITIATE = 1
        const val CALL_ACTION_ENTER = 2
        const val CALL_ACTION_LEAVE = 3
    }

}
