package com.infostrategic.edoctor.presentation.architecture.activity

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.infostrategic.edoctor.presentation.architecture.activity.ActivityEvent.*
import rx.Observable
import rx.subjects.BehaviorSubject

abstract class RxActivity : AppCompatActivity() {

    private val lifecycle = BehaviorSubject.create<ActivityEvent>()

    fun peekLifecycle(): ActivityEvent = lifecycle.value
        ?: throw IllegalStateException("Unable to peek lifecycle before Activity.onCreate()")

    fun lifecycle(): Observable<ActivityEvent> = lifecycle.asObservable()

    fun <T> bindToLifecycle(): Observable.Transformer<T, T> = lifecycle.untilCorrespondingEventTransformer()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.onNext(CREATE)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        lifecycle.onNext(START)
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        lifecycle.onNext(RESUME)
    }

    @CallSuper
    override fun onPause() {
        lifecycle.onNext(PAUSE)
        super.onPause()
    }

    @CallSuper
    override fun onStop() {
        lifecycle.onNext(STOP)
        super.onStop()
    }

    @CallSuper
    override fun onDestroy() {
        lifecycle.onNext(DESTROY)
        super.onDestroy()
    }

}