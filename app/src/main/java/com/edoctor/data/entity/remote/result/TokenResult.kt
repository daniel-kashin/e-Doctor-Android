package com.edoctor.data.entity.remote.result

import com.google.gson.annotations.SerializedName

data class TokenResult(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresIn: Long,
    @SerializedName("scope")
    val scope: String
) {
    companion object {
        val EMPTY = TokenResult("", "", "", -1, "")
    }
}