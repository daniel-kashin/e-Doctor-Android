package com.edoctor.presentation.app.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.edoctor.R
import com.edoctor.data.injection.AccountModule
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.presenter.account.AccountPresenter
import com.edoctor.presentation.app.presenter.account.AccountPresenter.Event
import com.edoctor.presentation.app.presenter.account.AccountPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import javax.inject.Inject

class AccountFragment : BaseFragment<AccountPresenter, ViewState, Event>("AccountFragment") {

    @Inject
    override lateinit var presenter: AccountPresenter

    override val layoutRes: Int = R.layout.fragment_account

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var textViewInfo: TextView
    private lateinit var buttonLogOut: Button

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.plus(AccountModule()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        textViewInfo = view.findViewById(R.id.text_view_info)
        buttonLogOut = view.findViewById(R.id.button_log_out)

        buttonLogOut.setOnClickListener {
            presenter.logOut()
        }
    }

    override fun render(viewState: ViewState) {
        swipeRefreshLayout.isRefreshing = viewState.isLoading
        textViewInfo.text = viewState.account?.run {
            "email = $email \n${if (isPatient) "пациент" else "доктор"}"
        }
    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowSessionException -> activity?.onSessionException()
        }
    }

}