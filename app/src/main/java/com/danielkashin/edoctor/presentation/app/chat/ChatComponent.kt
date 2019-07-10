package com.danielkashin.edoctor.presentation.app.chat

import com.danielkashin.edoctor.data.injection.ChatModule
import dagger.Subcomponent

@Subcomponent(modules = [ChatModule::class])
interface ChatComponent {
    fun inject(chatFragment: ChatActivity)
}