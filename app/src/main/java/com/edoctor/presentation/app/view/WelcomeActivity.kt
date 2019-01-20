package com.edoctor.presentation.app.view

import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.edoctor.R
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.app.presenter.welcome.WelcomePresenter
import com.edoctor.presentation.app.presenter.welcome.WelcomePresenter.Event
import com.edoctor.presentation.app.presenter.welcome.WelcomePresenter.ViewState
import com.edoctor.presentation.architecture.activity.BaseActivity
import com.edoctor.utils.lazyFind
import com.edoctor.utils.toast
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import javax.inject.Inject

class WelcomeActivity : BaseActivity<WelcomePresenter, ViewState, Event>("WelcomeActivity", true) {

    @Inject
    override lateinit var presenter: WelcomePresenter

    override fun createScreenConfig() = ScreenConfig(isOpenedSessionRequired = false)

    override val layoutRes: Int? = R.layout.activity_welcome

    override fun init(applicationComponent: ApplicationComponent) {
        applicationComponent.welcomeComponent.inject(this)
        presenter.init()
    }

    private val emailLayout by lazyFind<TextInputLayout>(R.id.email_layout)
    private val email by lazyFind<TextInputEditText>(R.id.email)
    private val passwordLayout by lazyFind<TextInputLayout>(R.id.password_layout)
    private val password by lazyFind<EditText>(R.id.password)
    private val authButton by lazyFind<Button>(R.id.auth_button)
    private val newAtEDoctor by lazyFind<TextView>(R.id.new_at_edoctor)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authButton.setOnClickListener { view ->
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
                presenter.auth(email, passwordText)
            }
        }

        newAtEDoctor.paintFlags = newAtEDoctor.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        newAtEDoctor.setOnClickListener {
            presenter.changeAuthType()
        }
    }

    override fun render(viewState: ViewState) {
        if (viewState.isLogin) {
            newAtEDoctor.text = "Впервые у нас?"
            authButton.text = "Войти"
        } else {
            newAtEDoctor.text = "Уже зарегистрированы?"
            authButton.text = "Зарегистрироваться"
        }
    }

    override fun showEvent(event: Event) {
        when (event) {
            Event.NoInternetExceptionEvent -> toast("Нет интернета")
            Event.PasswordIsWrong -> toast("Неверный пароль")
            Event.UserNotFound -> toast("Пользователь с данной почтой не найден")
            Event.UserAlreadyExists -> toast("Пользователь с данной почтой уже существует")
            Event.UnknownExceptionEvent -> toast("Необычная ошибка")
            Event.AuthSuccessEvent -> finish()
        }
    }
}