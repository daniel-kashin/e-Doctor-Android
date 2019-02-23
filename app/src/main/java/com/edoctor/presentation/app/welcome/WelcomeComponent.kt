package com.edoctor.presentation.app.welcome

import dagger.Subcomponent

@Subcomponent
interface WelcomeComponent {
    fun inject(welcomeActivity: WelcomeActivity)
}