package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.TextMessage
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ChatApi {

    @GET("/messages")
    fun getMessages(
        @Query("fromTimestamp") fromTimestamp: Long,
        @Query("recipientEmail") recipientEmail: String
    ): Single<List<TextMessage>>

}