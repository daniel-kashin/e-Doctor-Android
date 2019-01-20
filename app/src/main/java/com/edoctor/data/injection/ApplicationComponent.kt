package com.edoctor.data.injection

import com.edoctor.data.account.SessionManager
import com.edoctor.data.repository.AuthRepository
import com.edoctor.presentation.app.presenter.chat.ChatComponent
import com.edoctor.presentation.app.presenter.welcome.WelcomeComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        AuthModule::class,
//        DatabaseModule::class,
        NetworkModule::class,
//        DownloaderModule::class,
//        DataModule::class,
        SessionModule::class
    ]
)
interface ApplicationComponent {

    val sessionManager: SessionManager

    val authRepository: AuthRepository

    val welcomeComponent: WelcomeComponent

    fun plus(chatModule: ChatModule) : ChatComponent

}