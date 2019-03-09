package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.response.UserResponse
import io.reactivex.Single
import retrofit2.http.GET

interface AccountRestApi {

    @GET("/account")
    fun getAccount() : Single<UserResponse>

//    TODO
//    @POST("/account")
//    fun updateAccount(userResult: UserResponse)

}