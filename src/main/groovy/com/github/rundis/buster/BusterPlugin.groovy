package com.github.rundis.buster

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import com.github.rundis.buster.config.BusterConfig
import com.github.rundis.buster.internal.BusterTestingService
import com.github.rundis.buster.internal.browsercapture.BrowserCapturer
import com.github.rundis.buster.internal.browsercapture.BrowserDriverUtil

class BusterPlugin implements Plugin<Project>{
    BusterTestingService busterService


    @Override
    void apply(Project project) {
        this.busterService = new BusterTestingService(
                browserCapturer: new BrowserCapturer(project.logger),
                project: project
        )

        project.tasks.create (BusterSetupWebDriversTask.NAME, BusterSetupWebDriversTask)
            .setDriverUtil(new BrowserDriverUtil(ant: new AntBuilder()))


        project.tasks.create (BusterTestTask.NAME, BusterTestTask)
                .setService(busterService)
                .dependsOn(BusterSetupWebDriversTask.NAME)
                .logging.captureStandardError(LogLevel.INFO)

        project.tasks.create (BusterAutoTestTask.NAME, BusterAutoTestTask)
                .setService(busterService)
                .dependsOn(BusterSetupWebDriversTask.NAME)
                .addShutdownHook {
                    busterService.tearDownAfterTest()
                }

        project.extensions.create("buster", BusterConfig, project)
    }
}
