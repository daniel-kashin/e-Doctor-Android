package com.edoctor.utils

import android.app.Application
import com.edoctor.EDoctor
import com.edoctor.data.account.SessionInfo.AccessToken
import com.edoctor.data.account.SessionInfo.RefreshToken
import com.edoctor.data.account.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class CredentialsInterceptor(private val context: Application) : AnonymousInterceptor() {

    companion object {
        private const val HTTP_ERROR_EXPIRED_TOKEN = 401

        private val AUTHORIZATION_HEADER = "Authorization"
    }

    private val REFRESH_TOKEN_LOCK = Any()

    private val authRepository by lazy {
        EDoctor.get(context).applicationComponent.authRepository
    }

    @Suppress("ReturnCount")
    override fun intercept(chain: Interceptor.Chain): Response = context.session.run {
        if (!isOpen) {
            return super.intercept(chain)
        }

        val token = info.accessToken?.takeUnless { it.isExpired() } ?: getFreshestAccessToken(this)

        val response = chain.proceed(
            chain.request().newBuilder()
                .addHeader(AUTHORIZATION_HEADER, token.headerRepresentation)
                .build()
        )

        if (response.code() == HTTP_ERROR_EXPIRED_TOKEN && isOpen) {
            val freshestToken = getFreshestAccessToken(this)

            return chain.proceed(
                chain.request().newBuilder()
                    .addHeader(AUTHORIZATION_HEADER, freshestToken.headerRepresentation)
                    .build()
            )
        }

        return response
    }

    private fun getFreshestAccessToken(session: SessionManager) = synchronized(REFRESH_TOKEN_LOCK) {
        authRepository
            .getFreshestTokenByRequestToken(session.info.refreshToken.value)
            .blockingGet()
            .let {
                val expiresAfter = it.expiresIn.unixTimeToJavaTime() + currentJavaTime()
                val newRefreshToken = RefreshToken(it.refreshToken)
                val accessToken = AccessToken(it.tokenType, it.accessToken, expiresAfter)
                session.update { it.copy(refreshToken = newRefreshToken, accessToken = accessToken) }
                accessToken
            }
    }

}