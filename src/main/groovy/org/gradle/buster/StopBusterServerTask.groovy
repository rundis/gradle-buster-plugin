package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Buster

class StopBusterServerTask extends DefaultTask {
    static NAME = 'stopBusterServer'

    StopBusterServerTask() {
        onlyIf { Buster.running }
        dependsOn StopPhantomTask.NAME
    }

    @TaskAction
    void stop() {
        logger.info "Stopping buster server with pid: ${Buster.pid}"
        Buster.stopServer()
        assert !Buster.running, "Failed to stop Buster server"
    }
}
