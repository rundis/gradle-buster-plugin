package org.gradle.plugins.buster

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.buster.internal.Buster

class StartBusterServerTask extends DefaultTask {
    static String NAME = 'busterServer'

    Buster buster

    StartBusterServerTask() {
        buster = Buster.instance
        onlyIf { !buster.running }
    }

    @TaskAction
    void start() {
        logger.info "Starting buster server"
        def busterConfig = project.convention.getPlugin(BusterPluginConvention).busterConfig
        Map cmdResult = buster.startServer(busterConfig)
        if(!cmdResult.ok) {
            throw new GradleException("Error starting Buster Server: $cmdResult.message")
        }
        logger.info cmdResult.message

    }
}
