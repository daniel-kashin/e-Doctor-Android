package com.edoctor.data.usecase

import com.edoctor.data.remote.result.TokenResult
import com.edoctor.data.repository.AuthRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenUsecase @Inject constructor(
    private val repository: AuthRepository
) : BaseUsecase(null, null) {

    fun getFreshestToken(refreshToken: String): Single<TokenResult> =
        try {
            repository.getFreshestToken(refreshToken)
        } catch (throwable: Throwable) {
            throw throwable
        }

}