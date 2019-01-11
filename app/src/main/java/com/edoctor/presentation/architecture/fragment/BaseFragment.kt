package com.edoctor.presentation.architecture.fragment

import androidx.annotation.CallSuper
import com.edoctor.EDoctor
import com.edoctor.data.injection.ApplicationComponent
import com.edoctor.presentation.architecture.presenter.Presenter
import com.edoctor.presentation.architecture.presenter.Presenter.Event
import com.edoctor.presentation.architecture.presenter.Presenter.ViewState
import rx.Observable

abstract class BaseFragment<P : Presenter<VS, EV>, VS : ViewState, EV : Event>(
        protected val TAG: String?,
        private val saveRenderedViewState: Boolean = false
) : ViewStateFragment<P, VS, EV>() {

    protected var renderedViewState: VS? = null
        get() = when {
            saveRenderedViewState -> field
            else -> throw IllegalStateException("Set saveRenderedViewState to true")
        }
        private set

    override fun init() = init(EDoctor.get(context!!).applicationComponent)

    abstract fun init(applicationComponent: ApplicationComponent)

    @CallSuper
    override fun viewStateTransformer() = Observable.Transformer<VS, VS> { it }

    @CallSuper
    override fun eventTransformer() = Observable.Transformer<EV, EV> { it }

    @CallSuper
    override fun onRendered(viewState: VS) {
        if (saveRenderedViewState) renderedViewState = viewState
    }
}