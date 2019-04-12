package com.edoctor.data.injection

import com.edoctor.data.repository.AuthRepository
import com.edoctor.data.session.SessionManager
import com.edoctor.presentation.app.account.AccountComponent
import com.edoctor.presentation.app.chat.ChatComponent
import com.edoctor.presentation.app.conversations.ConversationsComponent
import com.edoctor.presentation.app.findDoctor.FindDoctorComponent
import com.edoctor.presentation.app.medcard.MedicalRecordsComponent
import com.edoctor.presentation.app.welcome.WelcomeComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        AuthModule::class,
        NetworkModule::class,
        SessionModule::class,
        MedicalRecordsModule::class
    ]
)
interface ApplicationComponent {

    val sessionManager: SessionManager

    val authRepository: AuthRepository

    val welcomeComponent: WelcomeComponent

    val medicalRecordsComponent: MedicalRecordsComponent

    val medicalAccessComponent: MedicalAccessComponent

    fun plus(chatModule: ChatModule): ChatComponent

    fun plus(accountModule: AccountModule): AccountComponent

    fun plus(conversationsModule: ConversationsModule): ConversationsComponent

    fun plus(findDoctorModule: FindDoctorModule): FindDoctorComponent

}