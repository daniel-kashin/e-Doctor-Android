package com.edoctor.presentation.architecture.activity.instance

import android.os.Bundle
import com.edoctor.utils.put
import kotlin.reflect.KProperty

open class SurvivalProperty<T>(private var field: T) : BaseSurvivalProperty<T> {

    override fun putToBundle(bundle: Bundle, key: String) {
        bundle.save(key, field)
    }

    override fun loadFromBundle(bundle: Bundle, key: String) {
        field = bundle.retrieve(key)
    }

    protected open fun Bundle.save(key: String, value: T) {
        put(key, value)
    }

    @Suppress("UNCHECKED_CAST")
    protected open fun Bundle.retrieve(key: String): T = get(key) as T

    override fun getValue(thisRef: Any, property: KProperty<*>): T = field

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        field = value
    }

}