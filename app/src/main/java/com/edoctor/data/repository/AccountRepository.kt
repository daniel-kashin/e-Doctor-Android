package com.edoctor.data.repository

import com.edoctor.data.entity.remote.UserResult
import com.edoctor.data.remote.api.AccountRestApi
import com.edoctor.data.session.SessionManager
import io.reactivex.Single

class AccountRepository(
    val api: AccountRestApi,
    val sessionManager: SessionManager
) {

    fun getCurrentAccount(refresh: Boolean = false): Single<UserResult> {
        return when {
            !sessionManager.isOpen -> {
                Single.error(SessionManager.SessionNotOpenedException())
            }
            refresh -> {
                api
                    .getAccount()
                    .flatMap { userResult ->
                        sessionManager
                            .update { it.copy(account = userResult) }
                            .toSingleDefault(userResult)
                    }
            }
            else -> {
                Single.fromCallable { sessionManager.info.account }
            }
        }
    }

}