package com.edoctor.utils

/**
 * Denotes that marked Long or Int represents time in java format (unixTimestamp * 1000)
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class JavaTime

@Target(AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
annotation class UnixTime

@Suppress("MagicNumber")
fun Long.unixTimeToJavaTime() = this * 1000

@Suppress("MagicNumber")
fun Long.javaTimeToUnixTime() = this / 1000

fun currentJavaTime() = System.currentTimeMillis()

fun currentUnixTime() = System.currentTimeMillis().javaTimeToUnixTime()