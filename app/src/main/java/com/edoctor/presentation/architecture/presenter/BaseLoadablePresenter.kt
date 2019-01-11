package com.edoctor.presentation.architecture.presenter

import com.bookmate.app.base.BasePresenter
import com.edoctor.presentation.architecture.presenter.BaseLoadablePresenter.SimpleLoadState.COMPLETED
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class BaseLoadablePresenter<VS : BaseLoadablePresenter.ViewState, SE : Presenter.Event>(TAG: String? = null)
    : BasePresenter<VS, SE>(TAG) {

    private var isLoadingCompleted: Boolean = false

    fun load() {
        when {
            isLoadingCompleted -> v("load(): loading is completed")
            viewStateSnapshot().isLoading -> v("load(): loading is in progress")
            else -> loadInternal()
        }
    }

    protected abstract fun loadInternal()

    interface ViewState : Presenter.ViewState {
        val isLoading: Boolean
    }

    class LoadStateDelegate<T : Enum<T>>(
            initialValue: T,
            private val completedValue: T
    ) : ReadWriteProperty<BaseLoadablePresenter<*, *>, T> {

        private var field: T = initialValue

        override fun getValue(thisRef: BaseLoadablePresenter<*, *>, property: KProperty<*>): T = field

        override fun setValue(thisRef: BaseLoadablePresenter<*, *>, property: KProperty<*>, value: T) {
            field = value
            thisRef.isLoadingCompleted = value == completedValue
        }
    }

    protected enum class SimpleLoadState { ITEMS, COMPLETED }

    protected fun simpleLoadStateDelegate() = LoadStateDelegate(initialValue = SimpleLoadState.ITEMS, completedValue = COMPLETED)

}