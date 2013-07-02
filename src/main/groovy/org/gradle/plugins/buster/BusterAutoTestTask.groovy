package org.gradle.plugins.buster

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.plugins.buster.internal.Buster
import org.gradle.plugins.buster.internal.BusterConfigParser
import org.gradle.plugins.buster.internal.BusterWatcher


class BusterAutoTestTask extends  DefaultTask {
    static NAME = "busterAutoTest"

    Buster buster

    Closure listener = {kind, path ->
        project.logger.info("Starting testrun due to change in path: $path")
        def busterConfig = project.convention.getPlugin(BusterPluginConvention).busterConfig
        def execResult = project.exec {
            executable "buster"
            args = ["test", "--reporter", "specification", "--server", "http://localhost:${busterConfig.port}"]
            ignoreExitValue = true
        }
    }

    BusterAutoTestTask() {
        buster = Buster.instance
    }

    @TaskAction
    void test() {
        def busterJs = new File(project.projectDir, "buster.js").text
        def globPatterns = new BusterConfigParser().parseGlobPatterns(busterJs)

        BusterWatcher.create(project, project.projectDir.absolutePath, globPatterns, listener).processEvents()
    }

}
