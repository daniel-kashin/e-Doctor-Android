package com.edoctor.presentation.app.view

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.edoctor.R
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.presenter.welcome.WelcomePresenter
import com.edoctor.presentation.app.presenter.welcome.WelcomePresenter.Event
import com.edoctor.presentation.app.presenter.welcome.WelcomePresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.lazyFind
import com.edoctor.utils.nothing
import com.edoctor.utils.unreachable
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import javax.inject.Inject

class WelcomeActivity : BaseActivity<WelcomePresenter, ViewState, Event>("WelcomeActivity", true) {

    @Inject
    override lateinit var presenter: WelcomePresenter

    override fun createScreenConfig() = ScreenConfig(isOpenedSessionRequired = false)

    override val layoutRes: Int? = R.layout.activity_welcome

    override fun init(applicationComponent: ApplicationComponent) =
        applicationComponent.welcomeComponent.inject(this)

    private val emailLayout by lazyFind<TextInputLayout>(R.id.email_layout)
    private val email by lazyFind<TextInputEditText>(R.id.email)
    private val passwordLayout by lazyFind<TextInputLayout>(R.id.password_layout)
    private val password by lazyFind<EditText>(R.id.password)
    private val loginButton by lazyFind<Button>(R.id.login_button)
    private val newAtEDoctor by lazyFind<TextView>(R.id.new_at_edoctor)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginButton.setOnClickListener { view ->
            var success = true

            val email = email.text.toString()
            if (email.isEmpty()) {
                success = false
                emailLayout.error = getString(R.string.username_error)
            }

            val passwordText = password.text.toString()
            if (passwordText.isEmpty()) {
                success = false
                passwordLayout.error = getString(R.string.password_error)
            }

            if (success) {
                presenter.register(email, passwordText)
            }
        }

        newAtEDoctor.paintFlags = newAtEDoctor.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        newAtEDoctor.setOnClickListener {
            nothing()
        }
    }

    private fun initializeView() {

    }

    override fun render(viewState: ViewState) = unreachable()

    override fun showEvent(event: Event) {
        when (event) {
            Event.NoInternetExceptionEvent -> {
                Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show()
            }
            Event.UnknownExceptionEvent -> {
                Toast.makeText(this, "Необычная ошибка", Toast.LENGTH_SHORT).show()
            }
            Event.AuthSuccessEvent -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}