package com.edoctor.data.remote.api

import com.edoctor.data.remote.result.TokenResult
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthRestApi {

    @FormUrlEncoded
    @POST("/oauth/token")
    fun getFreshestToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): Single<TokenResult>

}