package com.edoctor.data.entity.remote

data class TokenResult(
    val accessToken: String,
    val tokenType: String,
    val refreshToken: String,
    val expiresIn: Long,
    val scope: String
) {
    companion object {
        val EMPTY = TokenResult("", "", "", -1, "")
    }
}