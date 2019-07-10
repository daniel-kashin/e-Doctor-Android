package com.infostrategic.edoctor.presentation.app.chat

import com.infostrategic.edoctor.data.injection.ChatModule
import dagger.Subcomponent

@Subcomponent(modules = [ChatModule::class])
interface ChatComponent {
    fun inject(chatFragment: ChatActivity)
}