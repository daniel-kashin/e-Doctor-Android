package com.edoctor.presentation.app.account

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.edoctor.R
import com.edoctor.data.injection.AccountModule
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.account.AccountPresenter.Event
import com.edoctor.presentation.app.account.AccountPresenter.ViewState
import com.edoctor.presentation.architecture.fragment.BaseFragment
import com.edoctor.utils.*
import com.edoctor.utils.SessionExceptionHelper.onSessionException
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class AccountFragment : BaseFragment<AccountPresenter, ViewState, Event>("AccountFragment") {

    companion object {
        const val DATE_OF_BIRTH_TIMESTAMP_PARAM = "date_of_birth_timestamp"
        const val IS_MALE_PARAM = "is_male"
    }

    @Inject
    override lateinit var presenter: AccountPresenter

    override val layoutRes: Int = R.layout.fragment_account

    private var dateOfBirthTimestamp: Long? = null
    private var isMale: Boolean? = null

    private lateinit var contentLayout: ConstraintLayout
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var cityEditText: TextInputEditText
    private lateinit var fullNameEditText: TextInputEditText
    private lateinit var dateOfBirthEditText: TextInputEditText
    private lateinit var gender: TextInputEditText
    private lateinit var logOutButton: Button
    private lateinit var saveButton: Button

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.plus(AccountModule()).inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dateOfBirthTimestamp = savedInstanceState?.get(DATE_OF_BIRTH_TIMESTAMP_PARAM) as? Long
        isMale = savedInstanceState?.get(IS_MALE_PARAM) as? Boolean
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contentLayout = view.findViewById(R.id.content_layout)
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        fullNameEditText = view.findViewById(R.id.full_name)
        dateOfBirthEditText = view.findViewById(R.id.date_of_birth)
        gender = view.findViewById(R.id.gender)
        cityEditText = view.findViewById(R.id.city)
        logOutButton = view.findViewById(R.id.log_out_button)
        saveButton = view.findViewById(R.id.save_button)

        contentLayout.hide()

        logOutButton.setOnClickListener {
            presenter.logOut()
        }

        swipeRefreshLayout.setOnRefreshListener {
            presenter.refreshAccount()
        }

        dateOfBirthEditText.setOnClickListener {
            val nowCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

            DatePickerDialog(
                view.context,
                { _, year, month, dayOfMonth ->
                    nowCalendar.set(year, month, dayOfMonth, 0, 0, 0)
                    dateOfBirthTimestamp = nowCalendar.timeInMillis.javaTimeToUnixTime()
                    dateOfBirthEditText.setText(SimpleDateFormat("dd.MM.yyyy").format(nowCalendar.time))
                },
                nowCalendar.get(Calendar.YEAR) - 20,
                nowCalendar.get(Calendar.MONTH),
                nowCalendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = nowCalendar.timeInMillis
                show()
            }
        }

        gender.setOnClickListener {
            PopupMenu(gender.context, gender).apply {
                menuInflater.inflate(R.menu.gender, menu)
                setOnMenuItemClickListener { item ->
                    gender.setText(item.title)
                    isMale = item.itemId == R.id.male
                    true
                }
                show()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun render(viewState: ViewState) {
        isMale = viewState.account?.isMale
        dateOfBirthTimestamp = viewState.account?.dateOfBirthTimestamp

        swipeRefreshLayout.isRefreshing = viewState.isLoading

        saveButton.setOnClickListener {
            if (!viewState.isLoading && viewState.account != null) {
                presenter.updateAccount(
                    fullName = fullNameEditText.text?.toString()?.takeIfNotEmpty(),
                    city = cityEditText.text?.toString()?.takeIfNotEmpty(),
                    dateOfBirthTimestamp = dateOfBirthTimestamp,
                    isMale = isMale
                )
            }
        }

        if (viewState.account != null) {
            contentLayout.show()
            fullNameEditText.setText(viewState.account.fullName)
            cityEditText.setText(viewState.account.city)
            gender.setText(
                when (viewState.account.isMale) {
                    true -> getString(R.string.male)
                    false -> getString(R.string.female)
                    else -> null
                }
            )

            val dateOfBirthTimestamp = viewState.account.dateOfBirthTimestamp
            dateOfBirthEditText.setText(
                if (dateOfBirthTimestamp != null) {
                    SimpleDateFormat("dd.MM.yyyy").format(Date(dateOfBirthTimestamp.unixTimeToJavaTime()))
                } else {
                    null
                }
            )
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.put(DATE_OF_BIRTH_TIMESTAMP_PARAM, dateOfBirthTimestamp)
        outState.put(IS_MALE_PARAM, isMale)
        super.onSaveInstanceState(outState)
    }

}