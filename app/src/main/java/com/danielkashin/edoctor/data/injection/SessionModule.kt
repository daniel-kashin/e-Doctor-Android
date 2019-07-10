package com.danielkashin.edoctor.data.injection

import com.danielkashin.edoctor.data.session.SessionStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SessionModule {

    @Provides
    @Singleton
    fun provideSessionStorage(): SessionStorage = SessionStorage()

}