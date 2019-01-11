package com.edoctor.data.usecase

import rx.Scheduler

abstract class BaseUsecase(observeScheduler: Scheduler?, subscribeScheduler: Scheduler?) {

    protected lateinit var observeScheduler: Scheduler
        private set

    protected lateinit var subscribeScheduler: Scheduler
        private set

    init {
        observeScheduler?.let { this.observeScheduler = it }
        subscribeScheduler?.let { this.subscribeScheduler = it }
    }

}