package com.infostrategic.edoctor.presentation.app.account

import com.infostrategic.edoctor.data.injection.AccountModule
import dagger.Subcomponent

@Subcomponent(modules = [AccountModule::class])
interface AccountComponent {
    fun inject(accountFragment: AccountFragment)
}