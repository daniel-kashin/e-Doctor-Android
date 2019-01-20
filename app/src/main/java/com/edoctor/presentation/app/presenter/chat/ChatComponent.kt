package com.edoctor.presentation.app.presenter.chat

import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.view.ChatActivity
import dagger.Subcomponent

@Subcomponent(modules = [ChatModule::class])
interface ChatComponent {
    fun inject(chatFragment: ChatActivity)
}