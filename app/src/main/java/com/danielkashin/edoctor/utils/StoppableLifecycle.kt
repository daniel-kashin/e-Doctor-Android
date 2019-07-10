package com.danielkashin.edoctor.utils

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.lifecycle.LifecycleRegistry

class StoppableLifecycle constructor(
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry()
) : Lifecycle by lifecycleRegistry {

    fun start() {
        lifecycleRegistry.onNext(LifecycleState.Started)
    }

    fun stop() {
        lifecycleRegistry.onNext(LifecycleState.Stopped)
    }

}