package com.edoctor.utils

import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager
import android.view.inputmethod.InputMethodManager

val Context.connectivityManager get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
val Context.telephonyManager get() = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
val Context.inputMethodManager get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
val Context.notificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager