package com.danielkashin.edoctor.utils

import io.reactivex.disposables.Disposable
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

val disposableDelegate get() = Delegates.observable<Disposable?>(null) { _, oldValue, _ -> oldValue?.dispose() }

inline fun <T> changesObservableDelegate(initialValue: T, crossinline onChanged: (oldValue: T, newValue: T) -> Unit) =
    Delegates.observable(initialValue) { _, oldValue, newValue ->
        if (oldValue != newValue) {
            onChanged(oldValue, newValue)
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