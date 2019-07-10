package com.infostrategic.edoctor.presentation.app.conversations

import com.infostrategic.edoctor.data.injection.ConversationsModule
import dagger.Subcomponent

@Subcomponent(modules = [ConversationsModule::class])
interface ConversationsComponent {
    fun inject(conversationsFragment: ConversationsFragment)
}