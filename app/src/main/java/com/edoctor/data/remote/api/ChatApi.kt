package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.TextMessage
import io.reactivex.Single
import retrofit2.http.GET

interface ChatApi {

    @GET("/messages")
    fun getMessages(): Single<List<TextMessage>>

}