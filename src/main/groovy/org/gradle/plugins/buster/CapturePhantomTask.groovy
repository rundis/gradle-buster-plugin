package org.gradle.plugins.buster

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.buster.internal.Buster
import org.gradle.plugins.buster.internal.Phantom

class CapturePhantomTask extends DefaultTask {
    static String NAME = 'capturePhantom'

    Phantom phantom
    Buster buster
    File phantomFile

    CapturePhantomTask() {
        phantom = Phantom.instance
        buster = Buster.instance
        phantomFile = new File(project.buildDir, "phantom.js")
        onlyIf { !phantom.running }
        dependsOn StartBusterServerTask.NAME
    }

    @TaskAction
    void capturePhantomJs() {
        assert buster.running, "Buster server has not been started cannot capture Phantom"

        prepareForPhantomFile()
        def busterConfig = project.convention.getPlugin(BusterPluginConvention).busterConfig
        phantom.createPhantomFile(busterConfig, phantomFile)

        logger.info "Starting phantom and capturing with buster server using: ${phantomFile.path}"
        Map cmdResult = phantom.capturePhantom(busterConfig, phantomFile)
        if (!cmdResult.ok) {
            throw new GradleException(cmdResult.message)
        }
        logger.info cmdResult.message
    }

    private void prepareForPhantomFile() {
        if (!project.buildDir.exists()) {
            project.buildDir.mkdirs()
        }
        if (phantomFile.exists()) {
            phantomFile.delete()
        }
    }
}
