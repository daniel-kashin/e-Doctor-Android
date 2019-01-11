package com.edoctor.presentation.architecture

object SessionHelper {

    private const val RESTORE_DELAY = 5 * 1000
    private const val APP_OPENED_TRACK_DELAY = 30 * 60 * 1000

    var onNewSessionListener: (() -> Unit)? = null

    private var lastStopTime = 0L

    fun resume() {
//        val preferences = Preferences
//        val currentTime = System.currentTimeMillis()
//        val lastOpenedTime = preferences.lastAppOpenedTimeMillis
//
//        val appWasClosed = currentTime - lastStopTime > RESTORE_DELAY
//        val sessionIsExpired = currentTime - lastOpenedTime > APP_OPENED_TRACK_DELAY
//
//        if (appWasClosed && sessionIsExpired) {
//            preferences.isFirstAppLaunchAfterInstall = lastOpenedTime == 0L
//            preferences.lastAppOpenedTimeMillis = currentTime
//
//            onNewSessionListener?.invoke()
//        }
    }

    fun pause() {
//        lastStopTime = System.currentTimeMillis()
    }
}