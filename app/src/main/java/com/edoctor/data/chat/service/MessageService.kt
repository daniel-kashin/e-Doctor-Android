package com.edoctor.data.chat.service

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

interface MessageService {
    @Receive
    fun observeEvent(): Flowable<WebSocket.Event>
    @Receive
    fun observeMessages(): Flowable<String>
    @Send
    fun sendMessage(message: String)
}