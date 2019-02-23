package com.edoctor.presentation.app.account

import com.edoctor.data.injection.AccountModule
import dagger.Subcomponent

@Subcomponent(modules = [AccountModule::class])
interface AccountComponent {
    fun inject(accountFragment: AccountFragment)
}