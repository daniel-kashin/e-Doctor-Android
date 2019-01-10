package com.edoctor.utils

import io.reactivex.disposables.Disposable
import rx.Subscription
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

val subscriptionDelegate get() = Delegates.observable<Subscription?>(null) { _, oldValue, _ -> oldValue?.unsubscribe() }

val disposableDelegate get() = Delegates.observable<Disposable?>(null) { _, oldValue, _ -> oldValue?.dispose() }

val <T> Lazy<T>.valueIfInitialized: T? get() = if (isInitialized()) value else null

inline fun <T> changesObservableDelegate(initialValue: T, crossinline onChanged: (oldValue: T, newValue: T) -> Unit) =
    Delegates.observable(initialValue) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            onChanged(oldValue, newValue)
        }
    }

inline fun <T> changesObservable(initialValue: T, crossinline onChanged: (oldValue: T, newValue: T) -> Unit) =
    Delegates.observable(initialValue) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            onChanged(oldValue, newValue)
        }
    }

inline fun <T : Any> nonNullChangesObservableDelegate(crossinline onChanged: (newValue: T) -> Unit) =
    NotNullObservableDelegate<T> { _, new ->
        if (new != null) onChanged(new)
    }

open class ProxyDelegate<T>(private val getProperty: () -> KMutableProperty0<T>) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = getProperty().get()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        getProperty().set(value)
    }
}

class ChangesProxyDelegate<T>(
    private val property: () -> KMutableProperty0<T>,
    private val onChanged: (newValue: T) -> Unit
) : ReadWriteProperty<Any?, T> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return this.property().get()
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (this.property != value) {
            this.property().set(value)
            onChanged(value)
        }
    }
}

class NotNullObservableDelegate<T : Any>(private val afterChange: (old: T?, new: T?) -> Unit) :
    ReadWriteProperty<Any?, T> {

    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("Property ${property.name} should be initialized before get.")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val old = this.value
        this.value = value
        afterChange(old, value)
    }

    fun hasValue(): Boolean = value != null

    fun reset() {
        if (hasValue()) {
            val old = this.value
            this.value = null
            afterChange(old, null)
        }
    }
}

class SynchronizedDelegate<T>(defaultValue: T) : ReadWriteProperty<Any, T> {

    private var value: T = defaultValue

    override fun getValue(thisRef: Any, property: KProperty<*>): T = synchronized(this) { value }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        synchronized(this) {
            this.value = value
        }
    }
}