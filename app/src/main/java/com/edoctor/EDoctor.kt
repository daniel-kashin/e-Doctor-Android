package com.edoctor

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.cantrowitz.rxbroadcast.RxBroadcast
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.injection.DaggerApplicationComponent
import com.edoctor.data.injection.NetworkModule
import com.edoctor.data.properties.AppProperties
import com.edoctor.data.Preferences
import com.edoctor.utils.*
import com.github.anrwatchdog.ANRWatchDog

open class EDoctor : MultiDexApplication() {

    companion object {
        val applicationKey: String
            get() = AppProperties[AppProperties.Key.APPLICATION_KEY]

        val applicationSecret: String
            get() = AppProperties[AppProperties.Key.APPLICATION_SECRET]

        fun get(context: Context) = context.applicationContext as EDoctor
    }

    lateinit var applicationComponent: ApplicationComponent
        protected set

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()
        AppProperties.init(this)

        Thread.setDefaultUncaughtExceptionHandler { paramThread, paramThrowable ->
            Log.e("UncaughtException", paramThrowable.message.orEmpty(), paramThrowable)
            System.exit(2)
        }

        initAnrWatchDog()
        initPreferences()

        RxBroadcast
            .fromBroadcast(this, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            .startWith(Intent())
            .subscribe { ConnectivityNotifier.put(isNetworkAvailable()) }

        applicationComponent = initAppComponent()

        initSession()
    }

    protected open fun initAppComponent(): ApplicationComponent {
        return DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(this))
            .networkModule(NetworkModule(CredentialsInterceptor(this), AnonymousInterceptor()))
            .build()
    }

    protected open fun initSession() {
        session.tryToRestore()
            .ignoreElement()
            .onErrorComplete()
            .blockingAwait()
    }

    protected open fun initPreferences() {
        Preferences.init(this)
    }

    private fun initAnrWatchDog() {
        if (BuildConfig.DEBUG) {
            ANRWatchDog().start()
        }
    }

}