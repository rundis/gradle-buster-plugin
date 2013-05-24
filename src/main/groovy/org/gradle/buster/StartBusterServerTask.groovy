package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Buster


class StartBusterServerTask extends DefaultTask {

    @TaskAction
    void start() {
        if(Buster.running) {
            logger.warn "Buster server is already running"
            return
        }

        logger.info "Starting buster server"
        Buster.startServer()

    }
}
