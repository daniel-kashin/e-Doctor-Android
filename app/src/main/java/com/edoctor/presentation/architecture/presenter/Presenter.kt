package com.edoctor.presentation.architecture.presenter

import androidx.annotation.CallSuper
import androidx.annotation.VisibleForTesting
import androidx.annotation.VisibleForTesting.PROTECTED
import com.edoctor.presentation.architecture.presenter.Presenter.Event
import com.edoctor.presentation.architecture.presenter.Presenter.ViewState
import io.reactivex.disposables.CompositeDisposable
import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription

abstract class Presenter<VS : ViewState, EV : Event> {

    private val viewStateSubject = BehaviorSubject.create<VS>()
    private val eventSubject = PublishSubject.create<EV>()

    @VisibleForTesting(otherwise = PROTECTED)
    fun setViewState(vs: VS) = viewStateSubject.onNext(vs)

    protected inline fun setViewState(mapFunc: VS.() -> VS) {
        val currentState = viewStateSnapshotIfExists() ?: throw IllegalStateException("Call setViewState(VS) first")
        setViewState(mapFunc(currentState))
    }

    protected fun sendEvent(ev: EV) = eventSubject.onNext(ev)

    protected val subscriptions by lazy { CompositeSubscription() }
    protected val disposables by lazy { CompositeDisposable() }

    val viewStateObservable: Observable<VS>
        get() = viewStateSubject.onBackpressureLatest().asObservable()

    @VisibleForTesting(otherwise = PROTECTED)
    fun viewStateSnapshotIfExists(): VS? = viewStateSubject.value

    fun viewStateSnapshot(): VS = viewStateSubject.value!!

    val eventObservable: Observable<EV>
        get() = eventSubject.onBackpressureBuffer().asObservable()

    @CallSuper
    open fun destroy() {
        subscriptions.clear()
        disposables.clear()
    }

    interface ViewState
    interface Event
}