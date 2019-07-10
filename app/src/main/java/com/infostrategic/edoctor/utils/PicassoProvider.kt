package com.infostrategic.edoctor.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.MainThread
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Protocol

class PicassoProvider {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: Picasso? = null

        @MainThread
        fun get(context: Context): Picasso {
            val currentInstance = instance
            if (currentInstance != null) {
                return currentInstance
            }

            val newInstance = create(context)
            instance = newInstance
            return newInstance
        }

        fun create(context: Context): Picasso {
            val okHttpClient = OkHttpClient.Builder()
                .apply {
                    protocols(listOf(Protocol.HTTP_1_1))
                    SslUtils.getTrustAllHostsSSLSocketFactory()?.let {
                        sslSocketFactory(it.first, it.second)
                        hostnameVerifier { _, _ -> true }
                    }
                }
                .build()

            return Picasso.Builder(context.applicationContext)
                .downloader(OkHttp3Downloader(okHttpClient))
                .build()
        }
    }

}