package com.edoctor.data.session

import io.reactivex.Completable
import io.reactivex.Maybe

class SessionStorage {

    fun get(): Maybe<SessionInfo> =
        Maybe.fromCallable {
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