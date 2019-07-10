package com.infostrategic.edoctor.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes

private val uiHandler = Handler(Looper.getMainLooper())

enum class Duration { SHORT, LONG }

fun Context?.toast(@StringRes textRes: Int, duration: Duration = Duration.SHORT) = this?.toast(getString(textRes), duration)

fun Context?.toast(text: String?, duration: Duration = Duration.SHORT) = this?.run {
    if (text.isNullOrBlank()) return@run
    uiHandler.post {
        Toast.makeText(this, text, if (duration == Duration.SHORT) Toast.LENGTH_SHORT else Toast.LENGTH_LONG).show()
    }
}
