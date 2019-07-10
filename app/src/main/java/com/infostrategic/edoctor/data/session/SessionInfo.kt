package com.infostrategic.edoctor.data.session

import com.infostrategic.edoctor.data.entity.remote.model.user.UserModelWrapper
import com.infostrategic.edoctor.utils.JavaTime
import com.infostrategic.edoctor.utils.withHiddenPart
import java.util.concurrent.TimeUnit

data class SessionInfo constructor(
    val account: UserModelWrapper,
    val refreshToken: RefreshToken,
    val accessToken: AccessToken? = null
) {

    val isValid
        get() = account.doctorModel != null || account.patientModel != null

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