package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.UserResult
import io.reactivex.Single
import retrofit2.http.GET

interface AccountRestApi {

    @GET("/account")
    fun getAccount() : Single<UserResult>

//    TODO
//    @POST("/account")
//    fun updateAccount(userResult: UserResult)

}