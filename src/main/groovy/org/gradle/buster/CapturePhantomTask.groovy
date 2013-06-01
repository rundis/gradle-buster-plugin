package org.gradle.buster

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
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

        if (!project.buildDir.exists()) {
            project.buildDir.mkdirs()
        }

        def phantomFile = new File(project.buildDir, "phantom.js")
        if (phantomFile.exists()) {
            phantomFile.delete()
        }
        Phantom.createPhantomFile(phantomFile)

        logger.info "Starting phantom and capturing with buster server using: ${phantomFile.path}"
        Map cmdResult = Phantom.capturePhantom(phantomFile)
        if (!cmdResult.ok) {
            throw new GradleException(cmdResult.message)
        }
        logger.info cmdResult.message
    }
}
