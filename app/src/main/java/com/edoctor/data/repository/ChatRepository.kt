package com.edoctor.data.repository

import com.edoctor.data.remote.api.ChatService
import com.tinder.scarlet.WebSocket
import io.reactivex.Flowable

class ChatRepository(val chatService: ChatService) {

    fun observeEvents(): Flowable<WebSocket.Event> {
        return chatService.observeEvents()
    }

    fun sendMessage(message: String) {
        return chatService.sendMessage(message)
    }

}