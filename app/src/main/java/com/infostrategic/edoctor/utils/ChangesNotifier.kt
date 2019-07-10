package com.infostrategic.edoctor.utils

import rx.Observable
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject

enum class ChangeType { CREATED, EDITED, REMOVED }

object ChangesNotifier {

    private class Item(
        val value: Any,
        val changeType: ChangeType,
        val receiver: Any
    )

    private val subject = PublishSubject.create<Item>()

    fun <T : Any> observe(
        clazz: Class<T>,
        changeType: ChangeType?,
        receiver: Any
    ): Observable<Pair<T, ChangeType>> =
        subject.asObservable()
            .observeOn(Schedulers.computation())
            .onBackpressureBuffer()
            .filter {
                it.receiver !== receiver &&
                        (changeType == null || it.changeType == changeType) &&
                        clazz.isInstance(it.value)
            }
            .map { clazz.cast(it.value) to it.changeType }

    fun <T : Any> notify(value: T, changeType: ChangeType, receiver: Any) {
        subject.onNext(Item(value, changeType, receiver))
    }
}