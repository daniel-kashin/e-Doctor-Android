package com.danielkashin.edoctor.utils

import rx.Observable
import rx.subjects.BehaviorSubject

abstract class Notifier<T>(protected val tag: String, private val filterSameValues: Boolean = true) {

    protected val subject: BehaviorSubject<T> = BehaviorSubject.create()

    fun put(value: T): Boolean =
        if (!filterSameValues || subject.value != value) {
            subject.onNext(value)
            true
        } else {
            false
        }

    val lastValue: T? get() = subject.value

    /**
     * Hot observable which emits user's subscriptions after each change.
     */
    val asObservable: Observable<T> get() = subject.asObservable().onBackpressureLatest()

    /**
     * Same as [asObservable] but cold.
     */
    val changesObservable: Observable<T> get() = asObservable.skip(1)
}

