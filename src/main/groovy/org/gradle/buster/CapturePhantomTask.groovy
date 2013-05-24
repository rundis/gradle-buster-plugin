package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Buster
import org.gradle.buster.internal.Phantom


class CapturePhantomTask extends DefaultTask {
    static String NAME = 'capturePhantom'

    CapturePhantomTask() {
        onlyIf { !Phantom.running }
        dependsOn StartBusterServerTask.NAME
    }

    @TaskAction
    void capturePhantomJs() {
        assert Buster.running, "Buster server has not been started cannot capture Phantom"

        if(!project.buildDir.exists()) {
            project.buildDir.mkdirs()
        }

        def phantomFile = new File(project.buildDir, "phantom.js")
        if (!phantomFile.exists()) {
            Phantom.createPhantomFile(phantomFile)
        }
        logger.info "Starting phantom and capturing with buster server"
        Phantom.capturePhantom(phantomFile)
    }
}
