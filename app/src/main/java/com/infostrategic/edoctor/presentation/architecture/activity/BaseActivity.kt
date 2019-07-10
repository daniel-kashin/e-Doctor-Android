package com.infostrategic.edoctor.presentation.architecture.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import com.infostrategic.edoctor.EDoctor
import com.infostrategic.edoctor.data.injection.ApplicationComponent
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter.Event
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter.ViewState
import com.infostrategic.edoctor.utils.SessionExceptionHelper.onSessionException
import com.infostrategic.edoctor.utils.session
import rx.Observable
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.reflect.KProperty1

abstract class BaseActivity<P : Presenter<VS, EV>, VS : ViewState, EV : Event>(
    protected val TAG: String?,
    protected val saveRenderedViewState: Boolean = false
) : ViewStateActivity<P, VS, EV>() {

    private val screenConfig by lazy(NONE) { createScreenConfig() }

    protected open fun createScreenConfig(): ScreenConfig = ScreenConfig()

    protected var renderedViewState: VS? = null
        get() = when {
            saveRenderedViewState -> field
            else -> throw IllegalStateException("Set saveRenderedViewState to true")
        }
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (screenConfig.isPortraitOrientationRequired) {
            try {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } catch (e: IllegalStateException) {
                // NOTE: https://issuetracker.google.com/issues/68454482
            }
        }
    }

    override fun init() = init(EDoctor.get(this).applicationComponent)

    abstract fun init(applicationComponent: ApplicationComponent)

    @CallSuper
    override fun onStart() {
        super.onStart()
        if (screenConfig.isOpenedSessionRequired && !session.isOpen) {
            onSessionException()
        }
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
    }

    @CallSuper
    override fun viewStateTransformer() = Observable.Transformer<VS, VS> { it }

    @CallSuper
    override fun eventTransformer() = Observable.Transformer<EV, EV> { it }

    @CallSuper
    override fun onRendered(viewState: VS) {
        if (saveRenderedViewState) renderedViewState = viewState
    }

    protected inline fun <T> renderIfChanged(
        property: KProperty1<VS, T>,
        action: (oldValue: T?, newValue: T) -> Unit
    ): Boolean {
        if (!saveRenderedViewState)
            throw IllegalStateException("renderIfChanged(): allowed only with saveRenderedViewState == true")

        val hasPreviousState = renderedViewState != null
        val prev = renderedViewState?.let { property.get(it) }
        val current = property.get(presenter.viewStateSnapshot())

        return (!hasPreviousState || prev != current).also {
            if (it) action(prev, current)
        }
    }

    protected fun v(message: String) = Log.v(TAG, message)
    protected fun d(message: String) = Log.d(TAG, message)
    protected fun i(message: String) = Log.i(TAG, message)
    protected fun w(message: String) = Log.w(TAG, message)

    class ScreenConfig constructor(
        val isPortraitOrientationRequired: Boolean = false,
        val isOpenedSessionRequired: Boolean = true
    )
}