package com.edoctor.data.injection

import android.app.Application
import android.content.Context
import com.edoctor.EDoctor
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

@Module
open class ApplicationModule(private val edoctor: Application) {

    companion object {
        const val MAIN_THREAD_SCHEDULER = "observe"
        const val IO_THREAD_SCHEDULER = "subscription"
    }

    @Provides
    @Named(MAIN_THREAD_SCHEDULER)
    internal open fun provideMainThreadScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @Named(IO_THREAD_SCHEDULER)
    internal open fun provideSubscriptionScheduler(): Scheduler = Schedulers.io()

    @Provides
    @Singleton
    internal fun provideApp(): EDoctor = edoctor as EDoctor

    @Provides
    @Singleton
    internal fun provideContext(): Context = edoctor

}

