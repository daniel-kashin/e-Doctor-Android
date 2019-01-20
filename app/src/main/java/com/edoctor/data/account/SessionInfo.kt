package com.edoctor.data.account

import com.edoctor.data.entity.remote.UserResult
import com.edoctor.utils.JavaTime
import com.edoctor.utils.withHiddenPart
import java.util.concurrent.TimeUnit

data class SessionInfo(
    val profile: UserResult,
    val refreshToken: RefreshToken,
    val accessToken: AccessToken? = null
) {

    data class RefreshToken(val value: String) {
        override fun toString() = "Token(value='${value.withHiddenPart()}')"
    }

    data class AccessToken(
        val type: String,
        val value: String,
        @JavaTime
        val expiresAfter: Long
    ) {

        companion object {
            private val DEFAULT_RESERVED_MILLIS = TimeUnit.MINUTES.toMillis(3L)
        }

        fun isExpired(reservedMillis: Long = DEFAULT_RESERVED_MILLIS): Boolean =
            System.currentTimeMillis() > expiresAfter - reservedMillis

        val headerRepresentation: String
            get() = "$type $value"

        override fun toString() =
            "AccessToken(" +
                    "type='$type', " +
                    "value='${value.withHiddenPart()}', " +
                    "expiresAfter=$expiresAfter)"
    }
}