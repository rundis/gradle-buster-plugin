package org.gradle.plugins.buster

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
import org.gradle.plugins.buster.internal.Buster
import org.gradle.plugins.buster.internal.Phantom


class BusterPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {
        project.convention.plugins.put 'buster', new BusterPluginConvention(project)

        project.tasks.create StartBusterServerTask.NAME, StartBusterServerTask
        project.tasks.create (StopBusterServerTask.NAME, StopBusterServerTask).dependsOn StopPhantomTask.NAME
        project.tasks.create (CapturePhantomTask.NAME, CapturePhantomTask).dependsOn StartBusterServerTask.NAME
        project.tasks.create StopPhantomTask.NAME, StopPhantomTask
        project.tasks.create (BusterTestTask.NAME, BusterTestTask)
                .dependsOn(CapturePhantomTask.NAME)
                .addShutdownHook {
                    //println "Shutdown stuff... note will not play well with daemon"
                }
        project.tasks.create (BusterAutoTestTask.NAME, BusterAutoTestTask).dependsOn CapturePhantomTask.NAME


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
    }
}
