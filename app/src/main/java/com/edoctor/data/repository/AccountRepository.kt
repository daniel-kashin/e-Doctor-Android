package com.edoctor.data.repository

import com.edoctor.data.entity.remote.response.UserResponse
import com.edoctor.data.mapper.UserMapper.unwrapResponse
import com.edoctor.data.mapper.UserMapper.wrapRequest
import com.edoctor.data.remote.api.AccountRestApi
import com.edoctor.data.session.SessionManager
import com.edoctor.utils.asImageBodyPart
import io.reactivex.Single
import java.io.File

class AccountRepository(
    private val api: AccountRestApi,
    private val sessionManager: SessionManager
) {

    fun getCurrentAccount(refresh: Boolean = false): Single<UserResponse> {
        val fromSessionManager = Single
            .fromCallable {
                unwrapResponse(sessionManager.info.account)
            }

        val fromNetwork = api.getAccount()
            .flatMap { userResponseWrapper ->
                sessionManager
                    .update { it.copy(account = userResponseWrapper) }
                    .toSingleDefault(userResponseWrapper)
            }
            .map { unwrapResponse(it) }

        return if (refresh) {
            fromNetwork
        } else {
            fromSessionManager
        }
    }

    fun updateAccount(userResponse: UserResponse, imageFile: File?): Single<UserResponse> {
        return api.updateAccount(wrapRequest(userResponse), imageFile?.asImageBodyPart("image"))
            .flatMap { userResponseWrapper ->
                sessionManager
                    .update { it.copy(account = userResponseWrapper) }
                    .toSingleDefault(userResponseWrapper)
            }
            .map { unwrapResponse(it) }
    }

}