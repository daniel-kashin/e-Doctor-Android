package com.infostrategic.edoctor.presentation.architecture.activity

import com.infostrategic.edoctor.presentation.architecture.activity.ActivityEvent.*
import rx.Observable
import rx.subjects.BehaviorSubject

enum class ActivityEvent {
    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY
}

fun <T> BehaviorSubject<ActivityEvent>.untilCorrespondingEventTransformer(): Observable.Transformer<T, T> {
    val correspondingEvent = value.correspondingEvent()

    return Observable.Transformer { observable ->
        observable.takeUntil(filter { it == correspondingEvent })
    }
}

fun ActivityEvent?.correspondingEvent() = when (this) {
    null, CREATE -> DESTROY
    START -> STOP
    RESUME -> PAUSE
    PAUSE -> STOP
    STOP -> DESTROY
    DESTROY -> throw IllegalStateException("Unable to create transformer after Activity.onDestroy()")
}