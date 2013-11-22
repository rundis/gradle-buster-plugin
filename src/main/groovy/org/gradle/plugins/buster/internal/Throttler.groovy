package org.gradle.plugins.buster.internal

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


class Throttler {
    long delay
    Closure closure
    final scheduler = Executors.newScheduledThreadPool(1)
    private Future future = null

    def queue(args) {
        if(future && !future.done) {
            future.cancel(false)
        }
        future = scheduler.schedule({closure(args)} as Runnable, 100, TimeUnit.MILLISECONDS)
    }
}
