package com.edoctor.data.repository

import com.edoctor.data.entity.remote.response.UserResponse
import com.edoctor.data.mapper.UserMapper.unwrapResponse
import com.edoctor.data.mapper.UserMapper.wrapRequest
import com.edoctor.data.remote.api.AccountRestApi
import com.edoctor.data.session.SessionManager
import io.reactivex.Single

class AccountRepository(
    private val api: AccountRestApi,
    private val sessionManager: SessionManager
) {

    fun getCurrentAccount(refresh: Boolean = false): Single<UserResponse> {
        val fromSessionManager = Single
            .fromCallable {
                sessionManager.info.account
            }

        val fromNetwork = api.getAccount()
            .map { unwrapResponse(it) }
            .flatMap { userResult ->
                sessionManager
                    .update { it.copy(account = userResult) }
                    .toSingleDefault(userResult)
            }

        return if (refresh) {
            fromNetwork
        } else {
            fromSessionManager
        }
    }

    fun updateAccount(userResponse: UserResponse): Single<UserResponse> {
        return api
            .updateAccount(wrapRequest(userResponse))
            .map { unwrapResponse(it) }
    }

}