package com.edoctor.data.properties

import android.content.Context
import java.io.IOException
import java.util.*

object AppProperties {

    enum class Key(val key: String) {
        APPLICATION_KEY("application_key"),
        APPLICATION_SECRET("application_secret")
    }

    private const val FILE_NAME = "edoctor.properties"

    private lateinit var properties: Properties

    fun init(context: Context) {
        try {
            context.assets.open(FILE_NAME).use {
                properties = Properties()
                properties.load(it)
            }
        } catch (e: IOException) {
            throw IllegalStateException("$FILE_NAME file not found", e)
        }
    }

    operator fun get(key: Key): String = properties.getProperty(key.key)

}
