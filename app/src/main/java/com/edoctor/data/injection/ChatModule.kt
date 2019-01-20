package com.edoctor.data.injection

import com.edoctor.EDoctor
import com.edoctor.data.account.SessionManager
import com.edoctor.data.injection.NetworkModule.Companion.AUTHORIZED_TAG
import com.edoctor.data.injection.NetworkModule.Companion.EDOCTOR_WS_ENDPOINT
import com.edoctor.data.remote.api.ChatService
import com.edoctor.data.repository.ChatRepository
import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Named

@Module
class ChatModule(val recipientEmail: String) {

    @Provides
    fun provideChatService(
        application: EDoctor,
        @Named(AUTHORIZED_TAG)
        okHttpClientBuilder: OkHttpClient.Builder,
        moshi: Moshi
    ): ChatService =
        Scarlet.Builder()
            .webSocketFactory(okHttpClientBuilder.build().newWebSocketFactory("${EDOCTOR_WS_ENDPOINT}chat"))
            .lifecycle(AndroidLifecycle.ofApplicationForeground(application))
            .addMessageAdapterFactory(MoshiMessageAdapter.Factory(moshi))
            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
            .build()
            .create()

    @Provides
    fun provideChatRepository(
        chatService: ChatService,
        sessionManager: SessionManager
    ) = ChatRepository(recipientEmail, chatService, sessionManager)

}