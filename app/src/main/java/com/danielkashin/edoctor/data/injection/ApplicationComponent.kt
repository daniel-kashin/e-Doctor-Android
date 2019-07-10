package com.danielkashin.edoctor.data.injection

import com.danielkashin.edoctor.data.repository.AuthRepository
import com.danielkashin.edoctor.data.session.SessionManager
import com.danielkashin.edoctor.presentation.app.account.AccountComponent
import com.danielkashin.edoctor.presentation.app.chat.ChatComponent
import com.danielkashin.edoctor.presentation.app.conversations.ConversationsComponent
import com.danielkashin.edoctor.presentation.app.findDoctor.FindDoctorComponent
import com.danielkashin.edoctor.presentation.app.medcard.MedicalRecordsComponent
import com.danielkashin.edoctor.presentation.app.welcome.WelcomeComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        AuthModule::class,
        NetworkModule::class,
        SessionModule::class,
        MedicalRecordsModule::class,
        DatabaseModule::class
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