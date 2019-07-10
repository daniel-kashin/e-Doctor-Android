package com.danielkashin.edoctor.data.injection

import android.app.Application
import android.content.Context
import com.danielkashin.edoctor.EDoctor
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    companion object {
        const val MAIN_THREAD_SCHEDULER = "observe"
        const val IO_THREAD_SCHEDULER = "subscription"
    }

    @Provides
    @Named(MAIN_THREAD_SCHEDULER)
    internal fun provideMainThreadScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Named(IO_THREAD_SCHEDULER)
    internal fun provideSubscriptionScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Singleton
    internal fun provideApp(): EDoctor = application as EDoctor

    @Provides
    @Singleton
    internal fun provideContext(): Context = application

}

