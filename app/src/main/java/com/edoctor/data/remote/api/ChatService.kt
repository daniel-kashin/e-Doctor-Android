package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.TextMessage
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

interface ChatService {
    @Receive
    fun observeEvents(): Flowable<WebSocket.Event>
    @Send
    fun sendMessage(message: TextMessage)
}