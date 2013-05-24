package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Buster

class StartBusterServerTask extends DefaultTask {
    static String NAME = 'busterServer'

    StartBusterServerTask() {
        onlyIf { !Buster.running }
    }

    @TaskAction
    void start() {
        logger.info "Starting buster server"
        Buster.startServer()

    }
}
