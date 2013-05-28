package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.specs.Spec
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Phantom

class StopPhantomTask extends DefaultTask{
    static String NAME = 'stopPhantom'

    StopPhantomTask() {
        getOutputs().upToDateWhen(new Spec<Task>() {
            boolean isSatisfiedBy(Task element) {
                !Phantom.running
            }
        })

    }

    @TaskAction
    void stop() {
        logger.info("Stopping phantom")
        Phantom.stopServer()

    }
}
