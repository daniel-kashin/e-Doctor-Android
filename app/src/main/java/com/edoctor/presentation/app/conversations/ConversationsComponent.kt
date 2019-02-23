package com.edoctor.presentation.app.conversations

import com.edoctor.data.injection.ConversationsModule
import dagger.Subcomponent

@Subcomponent(modules = [ConversationsModule::class])
interface ConversationsComponent {
    fun inject(conversationsFragment: ConversationsFragment)
}