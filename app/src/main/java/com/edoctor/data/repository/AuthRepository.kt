package com.edoctor.data.repository

import com.edoctor.data.account.SessionInfo
import com.edoctor.data.account.SessionManager
import com.edoctor.data.entity.remote.LoginData
import com.edoctor.data.entity.remote.TokenResult
import com.edoctor.data.entity.remote.UserResult
import com.edoctor.data.remote.api.AuthRestApi
import com.edoctor.utils.onErrorConvertRetrofitThrowable
import com.edoctor.utils.unixTimeToJavaTime
import io.reactivex.Completable
import retrofit2.HttpException

class AuthRepository(
    private val authorizedApi: AuthRestApi,
    private val anonymousApi: AuthRestApi,
    private val sessionManager: SessionManager
) {

    fun getFreshestTokenByRequestToken(refreshToken: String): TokenResult {
        val result =  anonymousApi.getFreshestTokenByRefreshToken(refreshToken)
            .execute()

        return result.body() ?: throw HttpException(result)
    }

    fun register(loginData: LoginData): Completable {
        return authorizedApi.register(loginData)
            .flatMapCompletable { user ->
                anonymousApi.getFreshestTokenByPassword(loginData.password, loginData.email)
                    .flatMapCompletable { token ->
                        sessionManager.open(getSessionInfo(user, token))
                    }
            }
            .onErrorConvertRetrofitThrowable()
    }

    fun login(loginData: LoginData): Completable {
        return authorizedApi.login(loginData)
            .flatMapCompletable { user ->
                anonymousApi.getFreshestTokenByPassword(loginData.password, loginData.email)
                    .flatMapCompletable { token ->
                        sessionManager.open(getSessionInfo(user, token))
                    }
            }
            .onErrorConvertRetrofitThrowable()
    }

    private fun getSessionInfo(userResult: UserResult, tokenResult: TokenResult): SessionInfo {
        return SessionInfo(
            userResult,
            SessionInfo.RefreshToken(
                tokenResult.refreshToken
            ),
            SessionInfo.AccessToken(
                tokenResult.tokenType,
                tokenResult.accessToken,
                tokenResult.expiresIn.unixTimeToJavaTime() + System.currentTimeMillis()
            )
        )
    }

}