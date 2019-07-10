package com.danielkashin.edoctor.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log.e
import android.util.Log.w

@Suppress("TooGenericExceptionCaught")
abstract class CheckedIntentBuilder : IntentBuilder {

    constructor(context: Context) : super(context)
    constructor(fragment: androidx.fragment.app.Fragment) : super(fragment)

    override fun start() {
        try {
            if (!areParamsValid()) throw IllegalStateException("Invalid params")
            super.start()
        } catch (throwable: Throwable) {
            e("CheckedIntentBuilder", "start(): unable to start activity", throwable)
        }
    }

    override fun startForResult(requestCode: Int) {
        try {
            if (!areParamsValid()) throw IllegalStateException("Invalid params")
            super.startForResult(requestCode)
        } catch (throwable: Throwable) {
            e("CheckedIntentBuilder", "startForResult(): unable to start activity", throwable)
        }
    }

    abstract fun areParamsValid(): Boolean
}

abstract class IntentBuilder {

    protected val context: Context

    constructor(context: Context) {
        this.context = context
    }

    constructor(fragment: androidx.fragment.app.Fragment) {
        this.context = fragment.activity!!
    }

    open fun start() {
        context.startActivity(get(), withAnimation())
    }

    open fun startForResult(requestCode: Int) {
        if (context is Activity) {
            context.startActivityForResult(get(), requestCode, withAnimation())
        } else {
            w("IntentBuilder", "startForResult(): context must be instance of Activity!")
            context.startActivity(get(), withAnimation())
        }
    }

    abstract fun get(): Intent

    open fun withAnimation(): Bundle? = null
}
