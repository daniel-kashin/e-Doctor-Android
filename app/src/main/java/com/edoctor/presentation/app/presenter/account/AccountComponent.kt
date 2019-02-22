package com.edoctor.presentation.app.presenter.account

import com.edoctor.data.injection.AccountModule
import com.edoctor.presentation.app.view.AccountFragment
import dagger.Subcomponent

@Subcomponent(modules = [AccountModule::class])
interface AccountComponent {
    fun inject(accountFragment: AccountFragment)
}