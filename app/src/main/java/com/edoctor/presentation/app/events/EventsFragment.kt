package com.edoctor.presentation.app.events

import com.edoctor.R
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.events.EventsPresenter.Event
import com.edoctor.presentation.app.events.EventsPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import javax.inject.Inject

class EventsFragment : BaseFragment<EventsPresenter, ViewState, Event>("EventsFragment") {

    @Inject
    override lateinit var presenter: EventsPresenter

    override val layoutRes: Int = R.layout.fragment_parameters

    override fun init(applicationComponent: ApplicationComponent) {

    }

    override fun render(viewState: ViewState) {

    }

    override fun showEvent(event: Event) {

    }

}