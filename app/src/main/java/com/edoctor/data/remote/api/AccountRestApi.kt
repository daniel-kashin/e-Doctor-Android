package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.response.UserResponseWrapper
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AccountRestApi {

    @GET("/account")
    fun getAccount(): Single<UserResponseWrapper>

    @Multipart
    @POST("/account")
    fun updateAccount(
        @Part("userRequest") userRequest: UserResponseWrapper,
        @Part image: MultipartBody.Part?
    ): Single<UserResponseWrapper>

}