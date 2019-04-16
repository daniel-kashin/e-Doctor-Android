package com.edoctor.utils

import okhttp3.Interceptor
import okhttp3.Response

open class RecipientUuidInterceptor(val recipientUuid: String) : Interceptor {

    companion object {
        private const val RECIPIENT_UUID_HEADER = "recipient-uuid"
    }

    @Suppress("ReturnCount")
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder()
                .addHeader(RECIPIENT_UUID_HEADER, recipientUuid)
                .build()
        )
    }

}