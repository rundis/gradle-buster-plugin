package com.github.rundis.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.github.rundis.buster.internal.BusterJSParser
import com.github.rundis.buster.internal.BusterTestingService
import com.github.rundis.buster.internal.BusterWatcher

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

        Closure listener = {params ->
            project.logger.info("Starting testrun due to change in path: $params.path")
            def busterConfig = project.buster

            def execResult = project.exec {
                executable busterConfig.testExecutablePath
                args = ["--server", busterConfig.serverUrl, "--config", busterJs]
                ignoreExitValue = true
            }
        }


        service.prepareForTest()

        BusterWatcher.create(project, new BusterJSParser(), listener).processEvents()
    }


    private File resolveBusterJs() {
        File busterJsFile = project.buster.resolveConfigFile(project)

        if(!busterJsFile) {
            throw new IllegalArgumentException("No default buster config file found and no config file specified in options")
        }

        busterJsFile
    }

}
