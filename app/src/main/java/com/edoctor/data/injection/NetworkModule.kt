package com.edoctor.data.injection

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log.d
import com.edoctor.utils.NoConnectivityException
import com.edoctor.utils.connectivityManager
import com.edoctor.utils.isNetworkAvailable
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule(
    private val credentialsInterceptor: Interceptor,
    private val anonymousInterceptor: Interceptor
) {

    companion object {
        const val EDOCTOR_HTTP_ENDPOINT = "http://localhost:9095"
        const val EDOCTOR_WS_ENDPOINT = "ws://localhost:9095"

        const val NETWORK_LOG_TAG = "Retrofit"

        const val AUTHORIZED_TAG = "authorized"
        const val ANONYMOUS_TAG = "anonymous"

        private const val CONNECT_TIMEOUT_SEC = 30L
        private const val READ_TIMEOUT_SEC = 30L

        fun getAbsoluteImageUrl(relativeImageUrl: String) = EDOCTOR_HTTP_ENDPOINT + relativeImageUrl

        fun getRelativeImageUrl(absoluteImageUrl: String) = absoluteImageUrl.removePrefix(EDOCTOR_HTTP_ENDPOINT)

        fun createConnectivityInterceptor(connectivityManager: ConnectivityManager) = Interceptor {
            if (connectivityManager.isNetworkAvailable()) {
                it.proceed(it.request())
            } else {
                throw NoConnectivityException()
            }
        }

        fun createLoggingInterceptor() = Interceptor {
            HttpLoggingInterceptor { logText -> d(NETWORK_LOG_TAG, logText) }
                .setLevel(HttpLoggingInterceptor.Level.BODY)
                .intercept(it)
        }

        private fun OkHttpClient.Builder.addConnectionTimeout(): OkHttpClient.Builder =
            connectTimeout(CONNECT_TIMEOUT_SEC, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SEC, TimeUnit.SECONDS)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .disableHtmlEscaping()
            .create()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Named(AUTHORIZED_TAG)
    fun provideAuthorizedBookmateOkHttpClientBuilder(context: Context): OkHttpClient.Builder =
        OkHttpClient.Builder()
            .addConnectionTimeout()
            .addInterceptor(createConnectivityInterceptor(context.connectivityManager))
            .addInterceptor(credentialsInterceptor)
            .addInterceptor(createLoggingInterceptor())

    @Provides
    @Named(ANONYMOUS_TAG)
    fun provideAnonymousBookmateOkHttpClientBuilder(context: Context): OkHttpClient.Builder =
        OkHttpClient.Builder()
            .addConnectionTimeout()
            .addInterceptor(createConnectivityInterceptor(context.connectivityManager))
            .addInterceptor(anonymousInterceptor)
            .addInterceptor(createLoggingInterceptor())

    @Provides
    @Singleton
    @Named(AUTHORIZED_TAG)
    fun provideAuthorizedRetrofitBuilder(
        @Named(AUTHORIZED_TAG)
        okHttpClientBuilder: OkHttpClient.Builder,
        gson: Gson
    ): Retrofit.Builder = Retrofit.Builder()
            .baseUrl(EDOCTOR_HTTP_ENDPOINT)
            .client(okHttpClientBuilder.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    @Provides
    @Singleton
    @Named(ANONYMOUS_TAG)
    fun provideAnonymousRetrofitBuilder(
        @Named(ANONYMOUS_TAG)
        okHttpClientBuilder: OkHttpClient.Builder,
        gson: Gson
    ): Retrofit.Builder = Retrofit.Builder()
        .baseUrl(EDOCTOR_HTTP_ENDPOINT)
        .client(okHttpClientBuilder.build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

}
