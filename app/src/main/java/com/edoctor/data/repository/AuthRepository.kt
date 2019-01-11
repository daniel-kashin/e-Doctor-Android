package com.edoctor.data.repository

import com.edoctor.data.remote.api.AuthRestApi
import com.edoctor.data.remote.result.TokenResult
import io.reactivex.Single

class AuthRepository(
    private val api: AuthRestApi
) {

    fun getFreshestToken(refreshToken: String): Single<TokenResult> {
        return api.getFreshestToken(refreshToken)
    }

}