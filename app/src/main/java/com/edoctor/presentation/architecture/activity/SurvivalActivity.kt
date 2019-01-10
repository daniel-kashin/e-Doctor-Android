package com.edoctor.presentation.architecture.activity

import android.os.Bundle
import androidx.annotation.CallSuper
import com.edoctor.presentation.architecture.activity.instance.BaseSurvivalProperty
import com.edoctor.presentation.architecture.activity.instance.SurvivalLateinitProperty
import com.edoctor.presentation.architecture.activity.instance.SurvivalNullableProperty
import com.edoctor.presentation.architecture.activity.instance.SurvivalProperty

abstract class SurvivalActivity : RxActivity() {

    companion object {
        private const val PROPERTY_STATE_KEY = "property_by_index_%s"
    }

    private val survivalProperties = mutableListOf<BaseSurvivalProperty<*>>()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { bundle ->
            survivalProperties.forEachIndexed { index, property ->
                val key = PROPERTY_STATE_KEY.format(index)
                property.loadFromBundle(bundle, key)
            }
        }
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        survivalProperties.forEachIndexed { index, property ->
            val key = PROPERTY_STATE_KEY.format(index)
            property.putToBundle(outState, key)
        }
    }

    protected fun survivePlease(property: BaseSurvivalProperty<*>) {
        survivalProperties += property
    }

    protected fun <T> survivalProperty(initialValue: T) = SurvivalProperty(initialValue).also(this::survivePlease)
    protected fun <T> survivalNullableProperty(initialValue: T? = null) = SurvivalNullableProperty(initialValue).also(this::survivePlease)
    protected fun <T> survivalLateinitProperty() = SurvivalLateinitProperty<T>().also(this::survivePlease)
}