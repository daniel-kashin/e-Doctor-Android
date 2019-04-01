package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.response.UserResponseWrapper
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AccountRestApi {

    @GET("/account")
    fun getAccount(): Single<UserResponseWrapper>

    //    TODO
    @POST("/account")
    fun updateAccount(
        @Body userRequest: UserResponseWrapper
    ): Single<UserResponseWrapper>

}