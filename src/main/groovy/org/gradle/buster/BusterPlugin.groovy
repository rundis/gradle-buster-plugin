package org.gradle.buster

import org.gradle.api.Plugin
import org.gradle.api.Project


class BusterPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.tasks.add "startBusterServer", StartBusterServerTask
        project.tasks.add "stopBusterServer", StopBusterServerTask
        project.tasks.add "capturePhantom", CapturePhantomTask
        project.tasks.add "stopPhantom", StopPhantomTask
        project.tasks.add "busterTest", BusterTestTask
    }
}
