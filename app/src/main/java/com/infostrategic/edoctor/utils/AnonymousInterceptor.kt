package com.infostrategic.edoctor.utils

import com.infostrategic.edoctor.EDoctor
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

open class AnonymousInterceptor : Interceptor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
    }

    private val applicationBasicCredentials by lazy {
        Credentials.basic(EDoctor.applicationKey, EDoctor.applicationSecret)
    }

    @Suppress("ReturnCount")
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader(AUTHORIZATION_HEADER, applicationBasicCredentials)
                .build()
        )
    }

}