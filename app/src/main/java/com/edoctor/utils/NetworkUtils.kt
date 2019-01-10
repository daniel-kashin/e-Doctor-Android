package com.edoctor.utils

import android.content.Context
import android.net.ConnectivityManager

fun Context.isNetworkAvailable(): Boolean = connectivityManager.isNetworkAvailable()
fun Context.isWifiAvailable(): Boolean = connectivityManager.isWifiAvailable()
fun Context.isCellularAvailable(): Boolean = connectivityManager.isCellularAvailable()

fun ConnectivityManager.isNetworkAvailable(): Boolean =
    activeNetworkInfo?.isConnected ?: false

@Suppress("DEPRECATION")
fun ConnectivityManager.isWifiAvailable(): Boolean =
    getNetworkInfo(ConnectivityManager.TYPE_WIFI)?.isConnected ?: false

@Suppress("DEPRECATION")
fun ConnectivityManager.isCellularAvailable(): Boolean =
    getNetworkInfo(ConnectivityManager.TYPE_MOBILE)?.isConnected ?: false

fun ConnectivityManager.isConnectedOrConnecting(): Boolean =
    activeNetworkInfo?.isConnectedOrConnecting ?: false