package com.github.rundis.buster.internal

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


class Throttler {
    final long delay
    final Closure closure
    final scheduler = Executors.newScheduledThreadPool(1)
    private Future future = null

    public Throttler(long delay, Closure closure) {
        this.delay = delay
        this.closure = closure
    }


    def queue(args) {
        future?.cancel(false)
        future = scheduler.schedule({closure(args)} as Runnable, delay, TimeUnit.MILLISECONDS)
    }
}
