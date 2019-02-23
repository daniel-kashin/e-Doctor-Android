package com.edoctor.utils

import android.annotation.SuppressLint
import android.app.Activity
import com.edoctor.data.session.SessionManager
import com.edoctor.presentation.app.view.LaunchActivity
import retrofit2.HttpException

object SessionExceptionHelper {

    fun Throwable.isSessionException(): Boolean {
        return this is SessionManager.SessionNotOpenedException ||
                this is HttpException && this.code() == 401
    }

    @SuppressLint("CheckResult")
    fun Activity.onSessionException() {
        session
            .close()
            .subscribe {
                startActivity(LaunchActivity.authIntent(this))
                finish()
            }
    }

}