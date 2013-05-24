package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.buster.internal.Phantom


class CapturePhantomTask extends DefaultTask {

    @TaskAction
    void capturePhantomJs() {
        if(!project.buildDir.exists()) {
            project.buildDir.mkdirs()
        }

        def phantomFile = new File(project.buildDir, "phantom.js")
        if (!phantomFile.exists()) {
            Phantom.createPhantomFile(phantomFile)
        }
        Phantom.capturePhantom(phantomFile)





    }
}
