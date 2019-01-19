package com.edoctor.presentation.app.presenter.chat

import com.edoctor.data.injection.ChatModule
import com.edoctor.presentation.app.view.ChatFragment
import dagger.Subcomponent

@Subcomponent(modules = [ChatModule::class])
interface ChatComponent {
    fun inject(chatFragment: ChatFragment)
}