package com.edoctor.data.entity.remote

data class CallActionRequest(val callStatus: Int) {

    companion object {
        const val CALL_ACTION_ENTER = 1
        const val CALL_ACTION_LEAVE = 2
    }

}
