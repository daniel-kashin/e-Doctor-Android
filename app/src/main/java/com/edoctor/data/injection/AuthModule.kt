package com.edoctor.data.injection

import com.edoctor.data.remote.api.AuthRestApi
import com.edoctor.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class AuthModule {

    @Provides
    @Singleton
    internal fun provideAuthRestApi(
        builder: Retrofit.Builder
    ): AuthRestApi = builder.build().create(AuthRestApi::class.java)

    @Provides
    @Singleton
    internal fun provideAuthRepository(
        api: AuthRestApi
    ): AuthRepository = AuthRepository(api)

}

