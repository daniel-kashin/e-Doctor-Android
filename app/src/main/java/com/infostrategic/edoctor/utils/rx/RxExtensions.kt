package com.infostrategic.edoctor.utils.rx

import io.reactivex.Flowable

object RxExtensions {

    fun <T : Any> justOrEmptyFlowable(value: T?): Flowable<T> =
            if (value != null) {
                Flowable.just(value)
            } else {
                Flowable.empty()
            }

}