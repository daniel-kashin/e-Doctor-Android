package com.infostrategic.edoctor.data.injection

import android.content.Context
import com.infostrategic.edoctor.data.entity.remote.model.user.UserModel
import com.infostrategic.edoctor.data.local.message.MessagesLocalStore
import com.infostrategic.edoctor.data.mapper.MessageMapper
import com.infostrategic.edoctor.data.remote.rest.ConversationsRestApi
import com.infostrategic.edoctor.data.repository.ConversationsRepository
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
class ConversationsModule(private val currentUser: UserModel) {

    @Provides
    internal fun provideConversationsRestApi(
        @Named(NetworkModule.AUTHORIZED_TAG)
        builder: Retrofit.Builder
    ): ConversationsRestApi = builder.build().create(ConversationsRestApi::class.java)

    @Provides
    internal fun provideConversationsRepository(
        api: ConversationsRestApi,
        messagesLocalStore: MessagesLocalStore,
        context: Context
    ): ConversationsRepository = ConversationsRepository(currentUser, api, MessageMapper(context), messagesLocalStore)

}