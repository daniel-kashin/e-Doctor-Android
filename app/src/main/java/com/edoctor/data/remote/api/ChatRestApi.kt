package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.response.MessagesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatRestApi {

    @GET("/messages")
    fun getMessages(
        @Query("fromTimestamp") fromTimestamp: Long,
        @Query("recipientEmail") recipientEmail: String
    ): Single<MessagesResponse>

}