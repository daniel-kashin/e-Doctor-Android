package com.edoctor.presentation.app.presenter.welcome

import com.edoctor.presentation.app.view.WelcomeActivity
import dagger.Subcomponent

@Subcomponent
interface WelcomeComponent {
    fun inject(welcomeActivity: WelcomeActivity)
}