package com.edoctor.presentation.app.account

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.edoctor.R
import com.edoctor.data.injection.AccountModule
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.account.AccountPresenter.Event
import com.edoctor.presentation.app.account.AccountPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.edoctor.utils.hide
import com.edoctor.utils.show
import com.edoctor.utils.toast
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import javax.inject.Inject

class AccountFragment : BaseFragment<AccountPresenter, ViewState, Event>("AccountFragment") {

    @Inject
    override lateinit var presenter: AccountPresenter

    override val layoutRes: Int = R.layout.fragment_account

    private val displayedCalendar: Calendar? = null

    private lateinit var contentLayout: ConstraintLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var cityEditText: TextInputEditText
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var dateOfBirthEditText: TextInputEditText
    private lateinit var logOutButton: Button
    private lateinit var saveButton: Button

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.plus(AccountModule()).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentLayout = view.findViewById(R.id.content_layout)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        fullNameEditText = view.findViewById(R.id.full_name)
        dateOfBirthEditText = view.findViewById(R.id.date_of_birth)
        cityEditText = view.findViewById(R.id.city)
        logOutButton = view.findViewById(R.id.log_out_button)
        saveButton = view.findViewById(R.id.save_button)

        logOutButton.setOnClickListener {
            presenter.logOut()
        }

        swipeRefreshLayout.setOnRefreshListener {
            presenter.refreshAccount()
        }

        dateOfBirthEditText.setOnClickListener {

        }
    }

    override fun render(viewState: ViewState) {
        swipeRefreshLayout.isRefreshing = viewState.isLoading

        saveButton.setOnClickListener {
            if (!viewState.isLoading && viewState.account != null) {
                presenter.updateAccount(
                    fullName = fullNameEditText.text?.toString() ?: "",
                    city = cityEditText.text?.toString() ?: ""
                )
            }
        }

        if (viewState.account != null) {
            contentLayout.show()
            fullNameEditText.setText(viewState.account.fullName)
            cityEditText.setText(viewState.account.city)
        } else {
            contentLayout.hide()
        }

    }

    override fun showEvent(event: Event) {
        when (event) {
            is Event.ShowSessionException -> activity?.onSessionException()
            is Event.ShowNoNetworkException -> context.toast(getString(R.string.network_error_message))
        }
    }

}