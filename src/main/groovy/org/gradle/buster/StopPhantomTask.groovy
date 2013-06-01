package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Phantom

class StopPhantomTask extends DefaultTask {
    static String NAME = 'stopPhantom'

    StopPhantomTask() {
        onlyIf { Phantom.running }
    }

    @TaskAction
    void stop() {
        logger.info("Stopping phantom")
        Phantom.stopServer()

    }
}
