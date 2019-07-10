package com.danielkashin.edoctor.presentation.app.account

import com.danielkashin.edoctor.data.injection.AccountModule
import dagger.Subcomponent

@Subcomponent(modules = [AccountModule::class])
interface AccountComponent {
    fun inject(accountFragment: AccountFragment)
}