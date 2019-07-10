package com.danielkashin.edoctor.data.session

import com.danielkashin.edoctor.data.Preferences
import io.reactivex.Completable
import io.reactivex.Maybe

class SessionStorage {

    fun get(): Maybe<SessionInfo> =
        Maybe.fromCallable {
            Preferences.sessionInfo
        }

    fun save(sessionInfo: SessionInfo): Completable =
        Completable.fromAction {
            Preferences.sessionInfo = sessionInfo
        }

    fun remove(): Completable =
        Completable.fromAction {
            Preferences.sessionInfo = null
        }

}