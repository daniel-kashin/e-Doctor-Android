package com.edoctor.presentation.app.presenter.welcome

import com.bookmate.app.base.BasePresenter
import com.edoctor.presentation.architecture.presenter.Presenter
import javax.inject.Inject

class WelcomePresenter @Inject constructor(

) : BasePresenter<WelcomePresenter.ViewState, WelcomePresenter.Event>("WelcomePresenter") {

    class ViewState : Presenter.ViewState
    class Event : Presenter.Event

}