package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.response.ConversationsResponse
import io.reactivex.Single
import retrofit2.http.GET

interface ConversationsRestApi {

    @GET("/conversations")
    fun getConversations(): Single<ConversationsResponse>

}