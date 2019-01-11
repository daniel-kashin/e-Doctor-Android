package com.edoctor

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.multidex.MultiDexApplication
import com.cantrowitz.rxbroadcast.RxBroadcast
import com.edoctor.data.Preferences
import com.edoctor.data.account.SessionPreferences
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.data.injection.ApplicationModule
import com.edoctor.data.injection.DaggerApplicationComponent
import com.edoctor.data.injection.NetworkModule
import com.edoctor.data.properties.AppProperties
import com.edoctor.utils.ConnectivityNotifier
import com.edoctor.utils.CredentialsInterceptor
import com.edoctor.utils.isNetworkAvailable
import com.edoctor.utils.session
import com.github.anrwatchdog.ANRWatchDog

open class EDoctor : MultiDexApplication() {

    companion object {
        private const val TAG = "EDoctor"

        val applicationKey: String
            get() = AppProperties[AppProperties.Key.APPLICATION_KEY]

        val applicationSecret: String
            get() = AppProperties[AppProperties.Key.APPLICATION_SECRET]

        fun get(context: Context) = context.applicationContext as EDoctor
    }

    lateinit var applicationComponent: ApplicationComponent
        protected set

    override fun onCreate() {
        super.onCreate()
        AppProperties.init(this)

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
            .networkModule(NetworkModule(CredentialsInterceptor(this)))
            .build()
    }

    protected open fun initSession() {
        session.tryToRestore()
            .toCompletable()
            .onErrorComplete()
            .await()
    }

    protected open fun initPreferences() {
        SessionPreferences.init(this)
        Preferences.init(this)
    }

    private fun initAnrWatchDog() {
        if (BuildConfig.DEBUG) {
            ANRWatchDog().start()
        }
    }

}