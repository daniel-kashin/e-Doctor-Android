package com.edoctor.data.remote.api

import com.edoctor.data.entity.remote.LoginData
import com.edoctor.data.entity.remote.TokenResult
import com.edoctor.data.entity.remote.UserResult
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthRestApi {

    @FormUrlEncoded
    @POST("/oauth/token")
    fun getFreshestTokenByRefreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): Call<TokenResult>

    @FormUrlEncoded
    @POST("/oauth/token")
    fun getFreshestTokenByPassword(
        @Field("password") password: String,
        @Field("username") email: String,
        @Field("grant_type") grantType: String = "password"
    ): Single<TokenResult>

    @POST("/register")
    fun register(
       @Body loginData: LoginData
    ): Single<UserResult>

    @POST("/login")
    fun login(
        @Body loginData: LoginData
    ): Single<UserResult>

}