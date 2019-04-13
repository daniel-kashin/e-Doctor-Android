package com.edoctor.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import com.edoctor.data.session.SessionManager
import com.edoctor.presentation.app.launch.LaunchActivity
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
                overridePendingTransition(0, 0)
                startActivity(
                    LaunchActivity.authIntent(this).addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK)
                )
            }
    }

}