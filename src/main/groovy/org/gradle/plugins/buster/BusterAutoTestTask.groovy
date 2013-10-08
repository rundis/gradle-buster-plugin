package org.gradle.plugins.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.buster.internal.BusterJSParser
import org.gradle.plugins.buster.internal.BusterTestingService
import org.gradle.plugins.buster.internal.BusterWatcher

class BusterAutoTestTask extends  DefaultTask {
    static NAME = "busterAutoTest"

    protected BusterTestingService service


    protected BusterAutoTestTask setService(BusterTestingService service) {
        this.service = service
        this
    }

    @TaskAction
    void test() {
        def busterJs = resolveBusterJs()
        def globPatterns = new BusterJSParser().parseGlobPatterns(busterJs.text)


        Closure listener = {kind, path ->
            project.logger.info("Starting testrun due to change in path: $path")
            def busterConfig = project.buster

            def execResult = project.exec {
                executable busterConfig.testExecutablePath
                args = ["--reporter", "specification", "--server", busterConfig.serverUrl, "--config", busterJs]
                ignoreExitValue = true
            }
        }


        service.prepareForTest()

        BusterWatcher.create(project, project.projectDir.absolutePath, globPatterns, listener).processEvents()
    }


    private File resolveBusterJs() {
        File busterJsFile = project.buster.resolveConfigFile(project)

        if(!busterJsFile) {
            throw new IllegalArgumentException("No default buster config file found and no config file specified in options")
        }

        busterJsFile
    }

}
