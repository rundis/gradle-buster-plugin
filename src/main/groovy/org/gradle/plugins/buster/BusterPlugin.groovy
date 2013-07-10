package org.gradle.plugins.buster

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskState
import org.gradle.plugins.buster.config.BusterConfig
import org.gradle.plugins.buster.internal.Buster
import org.gradle.plugins.buster.internal.Phantom
import org.gradle.plugins.buster.internal.browsercapture.BrowserCapturer

class BusterPlugin implements Plugin<Project>{

    Project project
    BrowserCapturer browserCapturer

    @Override
    void apply(Project project) {
        this.project = project
        this.browserCapturer = new BrowserCapturer(project.logger)


        project.tasks.create StartBusterServerTask.NAME, StartBusterServerTask
        project.tasks.create (StopBusterServerTask.NAME, StopBusterServerTask).dependsOn StopPhantomTask.NAME
        project.tasks.create (CapturePhantomTask.NAME, CapturePhantomTask).dependsOn StartBusterServerTask.NAME
        project.tasks.create StopPhantomTask.NAME, StopPhantomTask
        project.tasks.create (BusterTestTask.NAME, BusterTestTask)
                .dependsOn(CapturePhantomTask.NAME)
                .mustRunAfter(CaptureBrowsersTask.NAME)
                .addShutdownHook {
                    // TODO: Make me optional
                    browserCapturer.shutdown()
                }
        project.tasks.create (BusterAutoTestTask.NAME, BusterAutoTestTask)
                .dependsOn(CapturePhantomTask.NAME)
                .mustRunAfter(CaptureBrowsersTask.NAME)

        project.tasks.create (CaptureBrowsersTask.NAME, CaptureBrowsersTask)
            .dependsOn(StartBusterServerTask.NAME)
            .setBrowserCapturer(browserCapturer)


        project.gradle.taskGraph.afterTask {Task task, TaskState state ->
            if(state.failure && task.name == BusterTestTask.NAME && task['busterKillOnFail']) {
                if(Buster.instance.running) {
                    project.logger.info "Killing buster server due to test failure"
                    Buster.instance.stopServer()
                }
                if(Phantom.instance.running) {
                    project.logger.info "Killing phantom.js due to test failure"
                    Phantom.instance.stopServer()
                }
            }

        }

        project.extensions.create("buster", BusterConfig, project)
    }
}
