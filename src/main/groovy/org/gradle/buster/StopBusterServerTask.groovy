package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Buster


class StopBusterServerTask extends DefaultTask {

    @TaskAction
    void stop() {
        if(!Buster.running) {
            logger.warn "Server is not running"
        }

        Buster.stopServer()
    }
}
