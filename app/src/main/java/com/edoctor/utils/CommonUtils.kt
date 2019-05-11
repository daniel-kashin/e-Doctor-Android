package com.edoctor.utils

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import rx.Subscription
import rx.subscriptions.CompositeSubscription

fun nothing(@Suppress("UNUSED_PARAMETER") param: Any? = null) {}

fun <R> Boolean.either(left: R, right: R) = if (this) left else right

fun String?.takeIfNotEmpty() = this?.takeIf(String::isNotEmpty)
fun String?.takeIfNotBlank() = this?.takeIf(String::isNotBlank)

fun String.digitsOnly() = replace("[^0-9]".toRegex(), "")

fun <T : Collection<*>> T.takeIfNotEmpty() = takeIf { it.isNotEmpty() }

inline fun <T : Collection<*>> T.synchronized(func: T.() -> Unit) {
    synchronized(this) {
        func(this)
    }
}

inline fun <reified R> Iterable<*>.findIsInstance(): R = filterIsInstance<R>().first()

inline fun <T, R : Any> Iterable<T>?.mapNotNullSafely(transform: (T) -> R?): List<R> =
    this?.mapNotNull {
        try {
            transform(it)
        } catch (throwable: Throwable) {
            null
        }
    } ?: emptyList()

inline fun <T> runOrElse(defaultValue: T, action: () -> T): T =
    try {
        action()
    } catch (throwable: Throwable) {
        defaultValue
    }

inline fun <T> runIf(condition: Boolean, action: () -> T): T? =
    if (condition) action()
    else null

operator fun CompositeSubscription.plusAssign(subscription: Subscription) {
    add(subscription)
}

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}

fun Double.ceil() = Math.ceil(this)