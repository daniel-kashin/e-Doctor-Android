package com.edoctor.data.injection

import com.edoctor.data.injection.NetworkModule.Companion.ANONYMOUS_TAG
import com.edoctor.data.injection.NetworkModule.Companion.AUTHORIZED_TAG
import com.edoctor.data.remote.rest.AuthRestApi
import com.edoctor.data.repository.AuthRepository
import com.edoctor.data.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class AuthModule {

    @Provides
    @Singleton
    @Named(ANONYMOUS_TAG)
    internal fun provideAnonymousAuthRestApi(
        @Named(ANONYMOUS_TAG)
        builder: Retrofit.Builder
    ): AuthRestApi = builder.build().create(AuthRestApi::class.java)

    @Provides
    @Singleton
    @Named(AUTHORIZED_TAG)
    internal fun provideAuthorizedAuthRestApi(
        @Named(AUTHORIZED_TAG)
        builder: Retrofit.Builder
    ): AuthRestApi = builder.build().create(AuthRestApi::class.java)

    @Provides
    @Singleton
    internal fun provideAuthRepository(
        @Named(ANONYMOUS_TAG)
        anonymousApi: AuthRestApi,
        @Named(AUTHORIZED_TAG)
        authorizedApi: AuthRestApi,
        sessionManager: SessionManager
    ): AuthRepository = AuthRepository(authorizedApi, anonymousApi, sessionManager)

}

