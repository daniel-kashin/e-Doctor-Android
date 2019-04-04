package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.model.user.UserModelWrapper
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AccountRestApi {

    @GET("/account")
    fun getAccount(): Single<UserModelWrapper>

    @Multipart
    @POST("/account")
    fun updateAccount(
        @Part("userRequest") userRequest: UserModelWrapper,
        @Part image: MultipartBody.Part?
    ): Single<UserModelWrapper>

}