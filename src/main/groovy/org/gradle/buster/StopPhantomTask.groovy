package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Phantom


class StopPhantomTask extends DefaultTask{

    @TaskAction
    void stop() {
        if(!Phantom.running) {
            logger.warn "Phantom is not running"
        }
        logger.info("Stopping phantom")
        Phantom.stopServer()
    }
}
