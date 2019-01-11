package com.edoctor.data.remote.result

data class TokenResult(
    val accessToken: String,
    val tokenType: String,
    val refreshToken: String,
    val expiresIn: Long,
    val scope: String
)