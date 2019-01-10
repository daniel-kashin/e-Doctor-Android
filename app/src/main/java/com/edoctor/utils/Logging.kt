package com.edoctor.utils

@JvmOverloads
fun String?.withHiddenPart(replacement: String = "***"): String =
    when {
        this == null -> "null"
        length < 7 -> "so small value"
        length < 11 -> replaceRange(2, length - 2, replacement)
        else -> replaceRange(5, length - 5, replacement)
    }