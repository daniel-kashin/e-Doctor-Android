package com.danielkashin.edoctor.presentation.architecture.presenter

import android.util.Log
import com.danielkashin.edoctor.presentation.architecture.presenter.Presenter.Event
import com.danielkashin.edoctor.presentation.architecture.presenter.Presenter.ViewState
import com.danielkashin.edoctor.utils.ChangeType.*
import com.danielkashin.edoctor.utils.ChangesNotifier
import com.danielkashin.edoctor.utils.ConnectivityNotifier
import com.danielkashin.edoctor.utils.plusAssign
import kotlin.reflect.KClass

@Suppress("TooManyFunctions")
abstract class BasePresenter<VS : ViewState, SE : Event>(protected val TAG: String? = null) : Presenter<VS, SE>() {

    protected fun isNetworkAvailable() = ConnectivityNotifier.lastValue!!

    protected inline fun subscribeToConnectivityChanges(
        skipFirst: Boolean = true,
        crossinline action: (isNetworkAvailable: Boolean) -> Unit
    ) {
        subscriptions += ConnectivityNotifier.asObservable
            .skip(if (skipFirst) 1 else 0)
            .subscribe { action(it) }
    }

    protected inline fun <reified T : Any> subscribeToChanges(crossinline onChangeAction: (T) -> Unit) =
        subscribeToChanges(T::class, onChangeAction)

    protected inline fun <T : Any> subscribeToChanges(clazz: KClass<T>, crossinline onChangeAction: (T) -> Unit) {
        subscriptions += ChangesNotifier.observe(clazz.java, EDITED, this)
            .subscribe { (value) -> onChangeAction(value) }
    }

    protected inline fun <reified T : Any> subscribeToCreating(crossinline onCreatedAction: (T) -> Unit) {
        subscriptions += ChangesNotifier.observe(T::class.java, CREATED, this)
            .subscribe { (value) -> onCreatedAction(value) }
    }

    protected inline fun <reified T : Any> subscribeToRemoving(crossinline onRemovedAction: (T) -> Unit) {
        subscriptions += ChangesNotifier.observe(T::class.java, REMOVED, this)
            .subscribe { (value) -> onRemovedAction(value) }
    }

    protected fun <T : Any> notifyCreated(value: T) = ChangesNotifier.notify(value, CREATED, this)
    protected fun <T : Any> notifyChanged(value: T) = ChangesNotifier.notify(value, EDITED, this)
    protected fun <T : Any> notifyRemoved(value: T) = ChangesNotifier.notify(value, REMOVED, this)

    protected fun v(message: String) = Log.v(TAG, message)
    protected fun d(message: String) = Log.d(TAG, message)
    protected fun i(message: String) = Log.i(TAG, message)
    protected fun w(message: String) = Log.w(TAG, message)
    protected fun e(throwable: Throwable, message: String) = Log.e(TAG, message, throwable)

    protected inline fun <T> invokeNotNull(value: T?, continueAction: (T) -> Unit) {
        if (value != null) {
            continueAction(value)
        }
    }

}