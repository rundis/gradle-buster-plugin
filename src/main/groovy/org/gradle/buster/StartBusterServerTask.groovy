package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Buster

class StartBusterServerTask extends DefaultTask {
    static String NAME = 'busterServer'


    StartBusterServerTask() {
        getOutputs().upToDateWhen(new Spec<Task>() {
            boolean isSatisfiedBy(Task element) {
                Buster.running
            }
        })
    }

    @TaskAction
    void start() {
        logger.info "Starting buster server"
        Map cmdResult = Buster.startServer()
        if(!cmdResult.ok) {
            throw new GradleException("Error starting Buster Server: $cmdResult.message")
        }
        logger.info cmdResult.message

    }
}
