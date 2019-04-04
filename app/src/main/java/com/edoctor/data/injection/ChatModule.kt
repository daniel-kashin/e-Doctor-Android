package com.edoctor.data.injection

import android.content.Context
import com.edoctor.data.injection.NetworkModule.Companion.AUTHORIZED_TAG
import com.edoctor.data.injection.NetworkModule.Companion.EDOCTOR_WS_ENDPOINT
import com.edoctor.data.mapper.MessageMapper
import com.edoctor.data.remote.api.ChatRestApi
import com.edoctor.data.remote.api.ChatService
import com.edoctor.data.repository.ChatRepository
import com.edoctor.utils.RecipientEmailInterceptor
import com.edoctor.utils.StoppableLifecycle
import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import javax.inject.Named

@Module
class ChatModule(
    private val currentUserEmail: String,
    private val recipientEmail: String
) {

    private val lifecycle: StoppableLifecycle = StoppableLifecycle()

    @Provides
    fun provideChatService(
        @Named(AUTHORIZED_TAG)
        okHttpClientBuilder: OkHttpClient.Builder,
        moshi: Moshi
    ): ChatService {
        val protocol = OkHttpWebSocket(
            okHttpClient = okHttpClientBuilder
                .addInterceptor(RecipientEmailInterceptor(recipientEmail))
                .build(),
            requestFactory = OkHttpWebSocket.SimpleRequestFactory(
                { Request.Builder().url("$EDOCTOR_WS_ENDPOINT/chat").build() },
                { ShutdownReason.GRACEFUL }
            )
        )

        val configuration = Scarlet.Configuration(
            messageAdapterFactories = listOf(MoshiMessageAdapter.Factory(moshi)),
            streamAdapterFactories = listOf(RxJava2StreamAdapterFactory()),
            lifecycle = lifecycle
        )

        return Scarlet(protocol, configuration).create()
    }

    @Provides
    internal fun provideAuthorizedChatApi(
        @Named(AUTHORIZED_TAG)
        builder: Retrofit.Builder
    ): ChatRestApi = builder.build().create(ChatRestApi::class.java)

    @Provides
    fun provideChatRepository(
        chatService: ChatService,
        chatRestApi: ChatRestApi,
        context: Context
    ): ChatRepository = ChatRepository(
        currentUserEmail, recipientEmail,
        chatRestApi, chatService,
        MessageMapper(context)
    ).apply {
        onStartConnectionListener = { lifecycle.start() }
        onCloseConnectionListener = { lifecycle.stop() }
    }

}