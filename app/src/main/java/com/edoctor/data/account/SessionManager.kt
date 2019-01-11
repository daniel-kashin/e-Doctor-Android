package com.edoctor.data.account

import com.edoctor.utils.SynchronizedDelegate
import rx.Completable
import rx.Single
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val sessionStorage: SessionStorage
) {

    private var sessionInfo by SynchronizedDelegate<SessionInfo?>(null)
    private val isSessionClosingInProgress = AtomicBoolean()

    val isOpen: Boolean
        get() = sessionInfo != null

    val info: SessionInfo
        get() = sessionInfo ?: throw IllegalStateException("info: session must be opened")

    inline fun <R> runIfOpened(action: (SessionInfo) -> R): R? =
        if (isOpen) {
            action(info)
        } else {
            null
        }

    fun tryToRestore(): Single<Boolean> {
        sessionInfo?.let { return Single.just(true) }

        return sessionStorage.get()
            .doOnSuccess { info -> sessionInfo = info }
            .map { it != null }
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
                updateSessionFunc(sessionInfo ?: throw IllegalStateException("update(): session must be opened"))
            }
            .doOnSuccess { sessionInfo = it }
            .flatMapCompletable { sessionStorage.save(it) }

    fun close(): Completable = Completable.defer {
        if (isSessionClosingInProgress.get() || !isOpen) {
            Completable.complete()
        } else {
            Completable.fromAction { isSessionClosingInProgress.set(true) }
                .andThen(sessionStorage.remove())
                .onErrorComplete()
                .doOnCompleted {
                    if (sessionInfo != null) {
                        sessionInfo = null
                    }
                    isSessionClosingInProgress.set(false)
                }
        }
    }

}