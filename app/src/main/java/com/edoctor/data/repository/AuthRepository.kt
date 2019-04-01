package com.edoctor.data.repository

import com.edoctor.data.entity.remote.request.LoginDataRequest
import com.edoctor.data.entity.remote.response.TokenResponse
import com.edoctor.data.entity.remote.response.UserResponseWrapper
import com.edoctor.data.remote.api.AuthRestApi
import com.edoctor.data.session.SessionInfo
import com.edoctor.data.session.SessionManager
import com.edoctor.utils.onErrorConvertRetrofitThrowable
import com.edoctor.utils.unixTimeToJavaTime
import io.reactivex.Completable
import retrofit2.HttpException

class AuthRepository(
    private val authorizedApi: AuthRestApi,
    private val anonymousApi: AuthRestApi,
    private val sessionManager: SessionManager
) {

    fun getFreshestTokenByRequestToken(refreshToken: String): TokenResponse {
        val result =  anonymousApi.getFreshestTokenByRefreshToken(refreshToken)
            .execute()

        return result.body() ?: throw HttpException(result)
    }

    fun register(loginData: LoginDataRequest): Completable {
        return authorizedApi.register(loginData)
            .flatMapCompletable { userResponseWrapper ->
                anonymousApi.getFreshestTokenByPassword(loginData.password, loginData.email)
                    .flatMapCompletable { token ->
                        sessionManager.open(getSessionInfo(userResponseWrapper, token))
                    }
            }
            .onErrorConvertRetrofitThrowable()
    }

    fun login(loginData: LoginDataRequest): Completable {
        return authorizedApi.login(loginData)
            .flatMapCompletable { userResponseWrapper ->
                anonymousApi.getFreshestTokenByPassword(loginData.password, loginData.email)
                    .flatMapCompletable { token ->
                        sessionManager.open(getSessionInfo(userResponseWrapper, token))
                    }
            }
            .onErrorConvertRetrofitThrowable()
    }

    fun logOut(): Completable {
        return sessionManager.close()
    }

    private fun getSessionInfo(userResponseWrapper: UserResponseWrapper, tokenResponse: TokenResponse): SessionInfo {
        return SessionInfo(
            userResponseWrapper,
            SessionInfo.RefreshToken(
                tokenResponse.refreshToken
            ),
            SessionInfo.AccessToken(
                tokenResponse.tokenType,
                tokenResponse.accessToken,
                tokenResponse.expiresIn.unixTimeToJavaTime() + System.currentTimeMillis()
            )
        )
    }

}