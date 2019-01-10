package com.edoctor.presentation.architecture.activity.instance

import android.os.Bundle
import kotlin.properties.ReadWriteProperty

interface BaseSurvivalProperty<T> : ReadWriteProperty<Any, T> {
    fun putToBundle(bundle: Bundle, key: String)
    fun loadFromBundle(bundle: Bundle, key: String)
}