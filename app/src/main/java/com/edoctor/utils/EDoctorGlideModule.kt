package com.edoctor.utils

import android.content.Context
import androidx.annotation.NonNull
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import okhttp3.OkHttpClient
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

@GlideModule
class EDoctorGlideModule : AppGlideModule() {

    override fun registerComponents(@NonNull context: Context, @NonNull glide: Glide, @NonNull registry: Registry) {
        val okHttpClient = OkHttpClient.Builder()
            .apply {
                SslUtils.getTrustAllHostsSSLSocketFactory()?.let {
                    sslSocketFactory(it.first, it.second)
                    hostnameVerifier { _, _ -> true }
                }
            }
            .build()

        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(okHttpClient))
    }

}