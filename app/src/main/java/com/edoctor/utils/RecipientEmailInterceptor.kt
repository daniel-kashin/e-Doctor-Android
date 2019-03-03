package com.edoctor.utils

import okhttp3.Interceptor
import okhttp3.Response

open class RecipientEmailInterceptor(val recipientEmail: String) : Interceptor {

    companion object {
        private const val RECIPIENT_EMAIL_HEADER = "recipient-email"
    }

    @Suppress("ReturnCount")
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader(RECIPIENT_EMAIL_HEADER, recipientEmail)
                .build()
        )
    }

}