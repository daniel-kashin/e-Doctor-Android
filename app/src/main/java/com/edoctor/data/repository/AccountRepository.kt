package com.edoctor.data.repository

import com.edoctor.data.entity.remote.model.user.UserModel
import com.edoctor.data.mapper.UserMapper.unwrapResponse
import com.edoctor.data.mapper.UserMapper.withAbsoluteUrl
import com.edoctor.data.mapper.UserMapper.wrapRequest
import com.edoctor.data.remote.rest.AccountRestApi
import com.edoctor.data.session.SessionManager
import com.edoctor.utils.asImageBodyPart
import io.reactivex.Single
import java.io.File

class AccountRepository(
    private val api: AccountRestApi,
    private val sessionManager: SessionManager
) {

    fun getCurrentAccount(refresh: Boolean = false): Single<UserModel> {
        val fromSessionManager: Single<UserModel> = Single
            .fromCallable {
                unwrapResponse(sessionManager.info.account)
            }

        val fromNetwork: Single<UserModel> = api.getAccount()
            .map { requireNotNull(withAbsoluteUrl(it)) }
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

    fun updateAccount(userModel: UserModel, imageFile: File?): Single<UserModel> {
        return api.updateAccount(wrapRequest(userModel), imageFile?.asImageBodyPart("image"))
            .map { requireNotNull(withAbsoluteUrl(it)) }
            .flatMap { userResponseWrapper ->
                sessionManager
                    .update { it.copy(account = userResponseWrapper) }
                    .toSingleDefault(userResponseWrapper)
            }
            .map { unwrapResponse(it) }
    }

}