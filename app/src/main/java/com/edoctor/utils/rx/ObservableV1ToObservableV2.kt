package com.edoctor.utils.rx

internal class ObservableV1ToObservableV2<T>(
        val source: rx.Observable<T>
) : io.reactivex.Observable<T>() {

    override fun subscribeActual(s: io.reactivex.Observer<in T>) {
        val parent = ObservableSubscriber(s)
        s.onSubscribe(parent)
        source.unsafeSubscribe(parent)
    }

    internal class ObservableSubscriber<T>(
            private val actual: io.reactivex.Observer<in T>
    ) : rx.Subscriber<T>(), io.reactivex.disposables.Disposable {
        var done: Boolean = false

        override fun onNext(t: T?) {
            if (done) return
            if (t == null) {
                unsubscribe()
                onError(NullPointerException("The upstream 1.x Observable signalled a null value which is not supported in 2.x"))
            } else {
                actual.onNext(t)
            }
        }

        override fun onError(e: Throwable) {
            if (done) {
                io.reactivex.plugins.RxJavaPlugins.onError(e)
                return
            }
            done = true
            actual.onError(e)
        }

        override fun onCompleted() {
            if (done) return
            done = true
            actual.onComplete()
        }

        override fun dispose() = unsubscribe()
        override fun isDisposed(): Boolean = isUnsubscribed
    }
}

fun <T> rx.Observable<T>.toV2(): io.reactivex.Observable<T> = ObservableV1ToObservableV2(this)