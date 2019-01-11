package com.edoctor.utils

import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Single
import io.reactivex.SingleSource
import java.io.IOException
import java.net.UnknownHostException
import javax.net.ssl.SSLException


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

class NoConnectivityException : IOException("No connectivity")

fun Throwable.isNoNetworkError(): Boolean =
    this is NoConnectivityException
            || this is UnknownHostException
            || this is SSLException
            || (cause?.isNoNetworkError() ?: false)


private fun <T> Single<T>.onErrorConvertRetrofitThrowable(): Single<T> =
    onErrorResumeNext { convertRetrofitThrowable(it) }

private fun <T> convertRetrofitThrowable(error: Throwable): SingleSource<T> =
    if (error.isNoNetworkError()) {
        Single.error(NoConnectivityException())
    } else {
        Single.error(error)
    }
