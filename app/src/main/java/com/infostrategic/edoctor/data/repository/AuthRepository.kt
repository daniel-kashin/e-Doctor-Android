package com.infostrategic.edoctor.data.repository

import com.infostrategic.edoctor.data.entity.remote.request.LoginDataRequest
import com.infostrategic.edoctor.data.entity.remote.response.TokenResponse
import com.infostrategic.edoctor.data.entity.remote.model.user.UserModelWrapper
import com.infostrategic.edoctor.data.remote.rest.AuthRestApi
import com.infostrategic.edoctor.data.session.SessionInfo
import com.infostrategic.edoctor.data.session.SessionManager
import com.infostrategic.edoctor.utils.unixTimeToJavaTime
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
    }

    fun login(loginData: LoginDataRequest): Completable {
        return authorizedApi.login(loginData)
            .flatMapCompletable { userResponseWrapper ->
                anonymousApi.getFreshestTokenByPassword(loginData.password, loginData.email)
                    .flatMapCompletable { token ->
                        sessionManager.open(getSessionInfo(userResponseWrapper, token))
                    }
            }
    }

    fun logOut(): Completable {
        return sessionManager.close()
    }

    private fun getSessionInfo(userModelWrapper: UserModelWrapper, tokenResponse: TokenResponse): SessionInfo {
        return SessionInfo(
            userModelWrapper,
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