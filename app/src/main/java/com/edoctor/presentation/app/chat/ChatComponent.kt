package com.edoctor.presentation.app.chat

import com.edoctor.data.injection.ChatModule
import dagger.Subcomponent

@Subcomponent(modules = [ChatModule::class])
interface ChatComponent {
    fun inject(chatFragment: ChatActivity)
}