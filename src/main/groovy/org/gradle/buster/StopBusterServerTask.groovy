package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Buster

class StopBusterServerTask extends DefaultTask {
    static NAME = 'stopBusterServer'

    Buster buster

    StopBusterServerTask() {
        buster = Buster.instance
        onlyIf { buster.running }
        dependsOn StopPhantomTask.NAME
    }

    @TaskAction
    void stop() {
        logger.info "Stopping buster server with pid: ${buster.pid}"
        buster.stopServer()
        assert !buster.running, "Failed to stop Buster server"
    }
}
