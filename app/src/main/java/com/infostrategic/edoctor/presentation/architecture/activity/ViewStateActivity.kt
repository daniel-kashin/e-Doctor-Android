package com.infostrategic.edoctor.presentation.architecture.activity

import androidx.annotation.CallSuper
import androidx.annotation.UiThread
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter.Event
import com.infostrategic.edoctor.presentation.architecture.presenter.Presenter.ViewState
import com.infostrategic.edoctor.utils.nothing
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

abstract class ViewStateActivity<P : Presenter<VS, EV>, VS : ViewState, EV : Event> : LifecycleAwareActivity<P>() {

    abstract fun init()

    override fun initPresenter() = init()
    override fun destroyPresenter() = presenter.destroy()

    @CallSuper
    override fun onStart() {
        super.onStart()

        presenter.viewStateObservable
            .observeOn(Schedulers.io())
            .compose(viewStateTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindToLifecycle())
            .subscribe {
                render(it)
                onRendered(it)
            }

        presenter.eventObservable
            .observeOn(Schedulers.io())
            .compose(eventTransformer())
            .observeOn(AndroidSchedulers.mainThread())
            .compose(bindToLifecycle())
            .subscribe(this::showEvent)
    }

    @UiThread
    protected abstract fun render(viewState: VS)

    @UiThread
    protected abstract fun showEvent(event: EV)

    protected open fun viewStateTransformer() = Observable.Transformer<VS, VS> { it }

    protected open fun eventTransformer() = Observable.Transformer<EV, EV> { it }

    @UiThread
    protected open fun onRendered(viewState: VS) = nothing()

}