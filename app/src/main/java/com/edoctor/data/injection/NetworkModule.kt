package com.edoctor.data.injection

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log.d
import com.edoctor.utils.NoConnectivityException
import com.edoctor.utils.connectivityManager
import com.edoctor.utils.isNetworkAvailable
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule(private val credentialsInterceptor: Interceptor) {

    companion object {
        const val EDOCTOR_HTTP_ENDPOINT = "http://localhost:9095/"
        const val EDOCTOR_WS_ENDPOINT = "ws://localhost:9095/"

        const val NETWORK_LOG_TAG = "Retrofit"

        private const val CONNECT_TIMEOUT_SEC = 30L
        private const val READ_TIMEOUT_SEC = 30L

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
    fun provideBookmateOkHttpClientBuilder(context: Context): OkHttpClient.Builder =
        OkHttpClient.Builder()
            .addConnectionTimeout()
            .addInterceptor(createConnectivityInterceptor(context.connectivityManager))
            .addInterceptor(credentialsInterceptor)
            .addInterceptor(createLoggingInterceptor())

    @Provides
    @Singleton
    fun provideGson(): Gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .disableHtmlEscaping()
            .create()

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        okHttpClientBuilder: OkHttpClient.Builder,
        gson: Gson
    ): Retrofit.Builder = Retrofit.Builder()
        .baseUrl(EDOCTOR_HTTP_ENDPOINT)
        .client(okHttpClientBuilder.build())
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())

}
