package com.danielkashin.edoctor.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.inputmethod.InputMethodManager

val Context.connectivityManager get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
val Context.inputMethodManager get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
