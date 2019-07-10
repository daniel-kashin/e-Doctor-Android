package com.infostrategic.edoctor.presentation.architecture.activity

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes

abstract class LifecycleAwareActivity<P> : SurvivalActivity() {

    abstract var presenter: P

    @get:LayoutRes
    protected abstract val layoutRes: Int?

    abstract fun initPresenter()
    abstract fun destroyPresenter()

    @CallSuper
    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutRes?.let { setContentView(it) }

        lastCustomNonConfigurationInstance.let {
            if (it == null) {
                initPresenter()
            } else {
                presenter = it as P
            }
        }
    }

    @CallSuper
    override fun onRetainCustomNonConfigurationInstance(): Any? = presenter

    @CallSuper
    override fun onDestroy() {
        if (!isChangingConfigurations) {
            destroyPresenter()
        }
        super.onDestroy()
    }

}