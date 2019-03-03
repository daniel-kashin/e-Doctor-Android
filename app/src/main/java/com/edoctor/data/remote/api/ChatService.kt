package com.edoctor.data.remote.api

import com.edoctor.data.entity.presentation.TextMessage
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

interface ChatService {

    @Receive
    fun observeEvents(): Flowable<WebSocketEvent>

    @Send
    fun sendMessage(message: TextMessage)

}