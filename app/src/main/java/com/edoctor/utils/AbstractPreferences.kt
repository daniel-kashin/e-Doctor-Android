package com.edoctor.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaType

@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
abstract class AbstractPreferences(private val name: String) {

    private val gson by lazy { GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() }

    protected lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(name, 0)
    }

    fun clearData() {
        prefs.edit().clear().apply()
    }

    protected class SharedPreferenceDelegate<T : Any>(
        private val name: String?,
        private val default: T,
        private val kClass: KClass<T>
    ) : ReadWriteProperty<AbstractPreferences, T> {

        companion object {
            inline fun <reified T : Any> create(name: String? = null, defaultValue: T? = null): SharedPreferenceDelegate<T> {
                val default: T = defaultValue ?: when (T::class) {
                    Boolean::class -> false
                    Int::class -> 0
                    Float::class -> 0f
                    Double::class -> 0.0
                    Long::class -> 0L
                    String::class -> ""
                    else -> throw IllegalArgumentException("Specify default value for SharedPreferenceDelegate $name")
                } as T
                return SharedPreferenceDelegate(name, default, T::class)
            }
        }

        override fun getValue(thisRef: AbstractPreferences, property: KProperty<*>): T {
            val string = runOrElse(null) { thisRef.prefs.getString(name ?: property.name, null) }?.takeIfNotEmpty()
            return when (kClass) {
                Boolean::class -> string?.toBoolean() ?: default
                Int::class -> string?.toInt() ?: default
                Float::class -> string?.toFloat() ?: default
                Double::class -> string?.toDouble() ?: default
                Long::class -> string?.toLong() ?: default
                String::class -> string ?: default
                else -> runOrElse(default) { thisRef.gson.fromJson(string, property.returnType.javaType) as? T ?: default }
            } as T
        }

        override fun setValue(thisRef: AbstractPreferences, property: KProperty<*>, value: T) {
            val string = when (kClass) {
                Boolean::class, Int::class, Float::class, Double::class, Long::class, String::class -> value.toString()
                else -> thisRef.gson.toJson(value)
            }
            thisRef.prefs.edit().putString(name ?: property.name, string).apply()
        }
    }

    protected class SharedPreferenceNullableDelegate<T : Any>(
        private val name: String? = null,
        private val kClass: KClass<T>
    ) : ReadWriteProperty<AbstractPreferences, T?> {

        companion object {
            inline fun <reified T : Any> create(name: String? = null): SharedPreferenceNullableDelegate<T> {
                return SharedPreferenceNullableDelegate(name, T::class)
            }
        }

        override fun getValue(thisRef: AbstractPreferences, property: KProperty<*>): T? {
            val string = runOrElse(null) { thisRef.prefs.getString(name ?: property.name, null) }

            if (string.isNullOrEmpty()) return null

            return when (kClass) {
                Boolean::class -> string.toBoolean()
                Int::class -> string.toInt()
                Float::class -> string.toFloat()
                Double::class -> string.toDouble()
                Long::class -> string.toLong()
                String::class -> string
                else -> runOrElse(null) { thisRef.gson.fromJson(string, property.returnType.javaType) as T? }
            } as T?
        }

        override fun setValue(thisRef: AbstractPreferences, property: KProperty<*>, value: T?) {
            val string = when (kClass) {
                Boolean::class, Int::class, Float::class, Double::class, Long::class, String::class -> value?.toString()
                else -> value?.let { thisRef.gson.toJson(it) }
            }
            thisRef.prefs.edit().putString(name ?: property.name, string).apply()
        }
    }
}