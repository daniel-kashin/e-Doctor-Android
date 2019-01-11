package com.edoctor.data.injection

import com.edoctor.data.account.SessionStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SessionModule {

    @Provides
    @Singleton
    fun provideSessionStorage(): SessionStorage = SessionStorage()

}