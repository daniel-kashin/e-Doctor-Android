package com.danielkashin.edoctor.presentation.architecture.fragment

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.danielkashin.edoctor.presentation.architecture.fragment.FragmentEvent.*
import rx.Observable
import rx.subjects.BehaviorSubject

abstract class RxFragment : Fragment() {

    private val lifecycle = BehaviorSubject.create<FragmentEvent>()

    fun peekLifecycle(): FragmentEvent = lifecycle.value
        ?: throw IllegalStateException("Unable to peek lifecycle before Fragment.onAttach()")

    fun lifecycle(): Observable<FragmentEvent> = lifecycle.asObservable()

    fun <T> bindToLifecycle(): Observable.Transformer<T, T> = lifecycle.untilCorrespondingEventTransformer()

    @Suppress("DEPRECATION", "OverridingDeprecatedMember")
    @CallSuper
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        lifecycle.onNext(ATTACH)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.onNext(CREATE)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycle.onNext(CREATE_VIEW)
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
    override fun onDestroyView() {
        lifecycle.onNext(DESTROY_VIEW)
        super.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        lifecycle.onNext(DESTROY)
        super.onDestroy()
    }

    @CallSuper
    override fun onDetach() {
        lifecycle.onNext(DETACH)
        super.onDetach()
    }

}