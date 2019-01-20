package com.edoctor.data.injection

import com.edoctor.EDoctor
import com.edoctor.data.account.SessionManager
import com.edoctor.data.injection.NetworkModule.Companion.AUTHORIZED_TAG
import com.edoctor.data.injection.NetworkModule.Companion.EDOCTOR_WS_ENDPOINT
import com.edoctor.data.remote.api.ChatService
import com.edoctor.data.repository.ChatRepository
import com.edoctor.utils.StoppableLifecycle
import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Named

@Module
class ChatModule(
    private val senderEmail: String,
    private val recipientEmail: String
) {

    private val lifecycle: StoppableLifecycle = StoppableLifecycle()

    @Provides
    fun provideChatService(
        application: EDoctor,
        @Named(AUTHORIZED_TAG)
        okHttpClientBuilder: OkHttpClient.Builder,
        moshi: Moshi
    ): ChatService {
        val protocol = OkHttpWebSocket(
            okHttpClientBuilder.build(),
            OkHttpWebSocket.SimpleRequestFactory(
                { Request.Builder().url("${EDOCTOR_WS_ENDPOINT}chat").build() },
                { ShutdownReason.GRACEFUL }
            )
        )

        val configuration = Scarlet.Configuration(
            messageAdapterFactories = listOf(MoshiMessageAdapter.Factory(moshi)),
            streamAdapterFactories = listOf(RxJava2StreamAdapterFactory()),
            lifecycle = lifecycle.combineWith(AndroidLifecycle.ofApplicationForeground(application))
        )

        return Scarlet(protocol, configuration).create()
    }

    @Provides
    fun provideChatRepository(
        chatService: ChatService,
        sessionManager: SessionManager
    ) = ChatRepository(senderEmail, recipientEmail, chatService, sessionManager).apply {
        onDisposeListener = { lifecycle.stop() }
    }

}