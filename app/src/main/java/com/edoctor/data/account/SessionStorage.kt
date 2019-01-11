package com.edoctor.data.account

import rx.Completable
import rx.Single

class SessionStorage {

    fun get(): Single<SessionInfo?> =
        Single.fromCallable {
            SessionPreferences.sessionInfo
        }

    fun save(sessionInfo: SessionInfo): Completable =
        Completable.fromAction {
            SessionPreferences.sessionInfo = sessionInfo
        }

    fun remove(): Completable =
        Completable.fromAction {
            SessionPreferences.sessionInfo = null
        }
}