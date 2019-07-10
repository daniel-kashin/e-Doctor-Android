package com.danielkashin.edoctor.data.session

import com.danielkashin.edoctor.data.Preferences
import com.danielkashin.edoctor.data.entity.remote.model.user.UserModel
import com.danielkashin.edoctor.data.local.base.DatabaseOpenHelper
import com.danielkashin.edoctor.data.mapper.UserMapper.unwrapResponse
import com.danielkashin.edoctor.utils.SynchronizedDelegate
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val sessionStorage: SessionStorage,
    private val databaseOpenHelper: DatabaseOpenHelper
) {

    private var sessionInfo by SynchronizedDelegate<SessionInfo?>(null)
    private val isSessionClosingInProgress = AtomicBoolean()

    val isOpen: Boolean
        get() = sessionInfo?.takeIf { it.isValid } != null

    val info: SessionInfo
        get() = sessionInfo?.takeIf { it.isValid } ?: throw SessionNotOpenedException()

    inline fun <R> runIfOpened(action: (UserModel) -> R): R? =
        if (isOpen) {
            val userInfo = unwrapResponse(info.account)
            if (userInfo != null) {
                action(userInfo)
            } else {
                null
            }
        } else {
            null
        }

    fun tryToRestore(): Single<Boolean> {
        sessionInfo?.let { return Single.just(true) }

        return sessionStorage.get()
            .filter { it.isValid }
            .doOnSuccess { info -> sessionInfo = info }
            .map { true }
            .toSingle(false)
    }

    @Suppress("TooGenericExceptionThrown")
    fun open(sessionInfo: SessionInfo): Completable =
        Completable
            .defer {
                if (isOpen) {
                    Completable.error(IllegalStateException("Session already opened! [sessionInfo = $sessionInfo]"))
                } else {
                    this.sessionInfo = sessionInfo
                    sessionStorage.save(sessionInfo)
                }
            }

    fun update(updateSessionFunc: (SessionInfo) -> SessionInfo): Completable =
        Single
            .fromCallable {
                updateSessionFunc(sessionInfo ?: throw SessionNotOpenedException())
            }
            .flatMapCompletable {
                sessionStorage.save(it)
                    .doOnComplete { sessionInfo = it }
            }

    fun close(): Completable = Completable.defer {
        if (isSessionClosingInProgress.get() || !isOpen) {
            Completable.complete()
        } else {
            Completable.fromAction { isSessionClosingInProgress.set(true) }
                .andThen(sessionStorage.remove())
                .doOnComplete {
                    databaseOpenHelper.recreateTables()
                    Preferences.clearData()
                }
                .onErrorComplete()
                .doOnComplete {
                    if (sessionInfo != null) {
                        sessionInfo = null
                    }
                    isSessionClosingInProgress.set(false)
                }
        }
    }

    class SessionNotOpenedException : Exception()

}