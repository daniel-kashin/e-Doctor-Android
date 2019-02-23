package com.edoctor.data.injection

import com.edoctor.data.remote.api.ConversationsRestApi
import com.edoctor.data.repository.ConversationsRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
class ConversationsModule(private val currentUserEmail: String) {

    @Provides
    internal fun provideConversationsRestApi(
        @Named(NetworkModule.AUTHORIZED_TAG)
        builder: Retrofit.Builder
    ): ConversationsRestApi = builder.build().create(ConversationsRestApi::class.java)

    @Provides
    internal fun provideConversationsRepository(
        api: ConversationsRestApi
    ): ConversationsRepository = ConversationsRepository(currentUserEmail, api)

}