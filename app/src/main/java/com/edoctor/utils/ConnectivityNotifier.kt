package com.edoctor.utils

import com.edoctor.utils.rx.toV2
import rx.Observable

object ConnectivityNotifier : Notifier<Boolean>("ConnectivityNotifier", filterSameValues = false) {

    /**
     * Cold observable which emits Unit after each change ONLY when network is available.
     */
    val onAvailableObservable: Observable<Unit>
        get() = changesObservable.filter { it }.map { Unit }

    val onAvailableObservableV2: io.reactivex.Observable<Unit>
        get() = onAvailableObservable.toV2()

    /**
     * Cold observable which emits Unit after each change ONLY when network is unavailable.
     */
    val onUnavailableObservable: Observable<Unit>
        get() = changesObservable.filter { !it }.map { Unit }

    val onUnavailableObservableV2: io.reactivex.Observable<Unit>
        get() = onUnavailableObservable.toV2()

}