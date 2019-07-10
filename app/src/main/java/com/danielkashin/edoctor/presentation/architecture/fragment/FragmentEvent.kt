package com.danielkashin.edoctor.presentation.architecture.fragment

import com.danielkashin.edoctor.presentation.architecture.fragment.FragmentEvent.*
import rx.Observable
import rx.subjects.BehaviorSubject

enum class FragmentEvent {
    ATTACH,
    CREATE,
    CREATE_VIEW,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY_VIEW,
    DESTROY,
    DETACH
}

fun <T> BehaviorSubject<FragmentEvent>.untilCorrespondingEventTransformer(): Observable.Transformer<T, T> {
    val correspondingEvent = when (value) {
        null, ATTACH -> DETACH
        CREATE -> DESTROY
        CREATE_VIEW -> DESTROY_VIEW
        START -> STOP
        RESUME -> PAUSE
        PAUSE -> STOP
        STOP -> DESTROY_VIEW
        DESTROY_VIEW -> DESTROY
        DESTROY -> DETACH
        DETACH -> throw IllegalStateException("Unable to create transformer after Fragment.onDetach()")
    }
    return Observable.Transformer { it.takeUntil(filter { it == correspondingEvent }) }
}