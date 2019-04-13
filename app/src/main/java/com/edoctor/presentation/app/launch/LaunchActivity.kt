package com.edoctor.presentation.app.launch

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.edoctor.presentation.app.main.MainActivity
import com.edoctor.presentation.app.welcome.WelcomeActivity
import com.edoctor.utils.session

class LaunchActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_SHOW_AUTH_SCREEN = "show_auth_screen"

        private var state: State? = null

        fun authIntent(context: Context): Intent =
            Intent(context, LaunchActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(EXTRA_SHOW_AUTH_SCREEN, true)
    }

    private enum class State {
        MAIN_LAUNCHED, LOGIN_LAUNCHED
    }

    override fun onResume() {
        super.onResume()
        if (state == State.MAIN_LAUNCHED || state == State.LOGIN_LAUNCHED && !session.isOpen) {
            state = null
            finish()
        } else {
            overridePendingTransition(0, 0) // to disable activity startup animation
            if (session.isOpen) {
                startMainActivity()
            } else {
                startAuthActivity()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        if (intent.getBooleanExtra(EXTRA_SHOW_AUTH_SCREEN, false)) {
            state = null
        }
    }

    override fun onDestroy() {
        if (isFinishing) {
            state = null
        }
        super.onDestroy()
    }

    private fun startAuthActivity() {
        state = State.LOGIN_LAUNCHED
        startActivity(Intent(this, WelcomeActivity::class.java))
    }

    private fun startMainActivity() {
        state = State.MAIN_LAUNCHED
        startActivity(Intent(this, MainActivity::class.java))
    }

}