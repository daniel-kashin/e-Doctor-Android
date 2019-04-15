package com.edoctor.data.remote.rest

import com.edoctor.data.entity.remote.response.MessagesResponse
import io.reactivex.Completable
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.*

interface ChatRestApi {

    @GET("/messages")
    fun getMessages(
        @Query("fromTimestamp") fromTimestamp: Long,
        @Query("recipientEmail") recipientEmail: String
    ): Single<MessagesResponse>

    @Multipart
    @POST("/images/send")
    fun sendImage(
        @Query("recipientEmail") recipientEmail: String,
        @Part image: MultipartBody.Part?
    ): Completable

}