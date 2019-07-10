package com.danielkashin.edoctor.presentation.architecture.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

abstract class LifecycleAwareFragment<P> : RxFragment() {

    abstract var presenter: P

    @get:LayoutRes
    protected abstract val layoutRes: Int?

    abstract fun initPresenter()
    abstract fun destroyPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            layoutRes?.let { inflater.inflate(it, container, false) }

    override fun onDestroy() {
        super.onDestroy()
        destroyPresenter()
    }

}