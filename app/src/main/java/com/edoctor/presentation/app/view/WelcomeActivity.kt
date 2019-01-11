package com.edoctor.presentation.app.view

import com.edoctor.R
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.presenter.welcome.WelcomePresenter
import com.edoctor.presentation.app.presenter.welcome.WelcomePresenter.Event
import com.edoctor.presentation.app.presenter.welcome.WelcomePresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import javax.inject.Inject

class WelcomeActivity : BaseActivity<WelcomePresenter, ViewState, Event>("WelcomeActivity", true) {

    @Inject override lateinit var presenter: WelcomePresenter

    override val layoutRes: Int? = R.layout.welcome_activity

    override fun init(applicationComponent: ApplicationComponent) =
        applicationComponent.welcomeComponent.inject(this)

    override fun render(viewState: ViewState) {
    }

    override fun showEvent(event: Event) {
    }
}