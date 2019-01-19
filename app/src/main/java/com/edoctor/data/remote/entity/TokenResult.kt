package com.edoctor.data.remote.entity

data class TokenResult(
    val accessToken: String,
    val tokenType: String,
    val refreshToken: String,
    val expiresIn: Long,
    val scope: String
)