package com.danielkashin.edoctor.data.remote.socket

import com.danielkashin.edoctor.data.entity.remote.request.MessageRequestWrapper
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

interface ChatSocketApi {

    @Receive
    fun observeEvents(): Flowable<WebSocketEvent>

    @Send
    fun sendMessage(message: MessageRequestWrapper)

}